package org.agfjord.grammar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.agfjord.domain.AbstractSyntaxTree;
import org.agfjord.domain.Query;
import org.agfjord.server.result.NameResult;
import org.agfjord.server.result.Question;
import org.agfjord.server.result.TreeResult;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.NercLiteralCallback;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.PGFError;
import org.grammaticalframework.pgf.ParseError;


public class Parser {

	private PGF gr;
	private SolrServer treesServer = new HttpSolrServer("http://localhost:8080/solr-instrucs/trees");
	private SolrServer namesServer = new HttpSolrServer("http://localhost:8080/solr-instrucs/names");
	final private Properties prop = new Properties();
    
    // Maximum nr of variants on the Abs trees
    Integer max_nr_of_trees = 10;
    Integer nr_of_additional_suggestions = 5;
    
	static Logger log = Logger.getLogger(
			Parser.class.getName());

	public Parser() throws SolrServerException, FileNotFoundException {
		try {
			URL url = this.getClass().getClassLoader().getResource("Instrucs.pgf");
			gr = PGF.readPGF(url.openStream());
		}
		catch (PGFError e) { 
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			URL url = this.getClass().getClassLoader().getResource("config.properties");
			prop.load(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		gr.getLanguages().get("InstrucsEngRGL").addLiteral("Symb", new NercLiteralCallback());
		gr.getLanguages().get("InstrucsEngConcat").addLiteral("Symb", new NercLiteralCallback());
		gr.getLanguages().get("InstrucsSweRGL").addLiteral("Symb", new NercLiteralCallback());
	}

	public String closestQuestion(String nlQuestion){
		String[] words = nlQuestion.split("\\s+");
		int cut = -1;
		for(int i=words.length-1; i > 0; i--){
			if(words[i].equals("and") || words[i].equals("or")){
				cut = i;
				break;
			}
		}
		StringBuilder closestQuestion = new StringBuilder();
		for(int i=0; i < cut; i++){
			closestQuestion.append(words[i] + " ");
		}
		closestQuestion.deleteCharAt(closestQuestion.length()-1);
		return nlQuestion;
	}
	
	/*
	 * Parse a string @question in a language @parseLang. Retrieve all distinct abstract syntax trees
	 * and their distinct linearizations. I think there exists a bug in GF which causes a NLP-sentence
	 * to be parsed into multiple equal abstract syntax trees, the same with linearizations. drbean @ #gf @ freenode
	 * told me this bug existed.
	 * 
	 */
	public List<AbstractSyntaxTree> parse(String question, String parseLang) throws ParseError {
		Iterable<ExprProb> exprProbs;
		// Map is used to only have distinct asts, Set is used to only have distinct linearizations
		Map<String,Set<Query>> astQuery = new HashMap<String,Set<Query>>();
		exprProbs = gr.getLanguages().get(parseLang).parse(gr.getStartCat(), question);
		for(String key : gr.getLanguages().keySet()){
			Concr lang = gr.getLanguages().get(key);
			for(ExprProb exprProb : exprProbs) {
				Set<Query> qs = astQuery.get(exprProb.getExpr().toString());
				if(qs == null){
					qs = new TreeSet<Query>(comparator);
					astQuery.put(exprProb.getExpr().toString(), qs);
				}
				qs.add(new Query(lang.linearize(exprProb.getExpr()), lang.getName()));	
			}
		}
		List<AbstractSyntaxTree> asts = new ArrayList<AbstractSyntaxTree>();
		for(String key : astQuery.keySet()){
			asts.add(new AbstractSyntaxTree(key, astQuery.get(key)));
		}
		return asts;
	}
	
	Comparator<Query> comparator = new Comparator<Query>(){
        public int compare(Query a, Query b){
//        	return 1;
            return a.getLanguage().compareTo(b.getLanguage());
        }
    };

	//people who know Skill0 and who work in Location0 ==> add(sub(Skill_i,1),sub(Location_i,1))

	//people who know Skill0 and who work in Location0 and who work with Solr ==> add(add(sub(Skill_i,1),sub(Location_i,1)),sub(Object_i,1))

	private String getSort(Map<String,List<NameResult>> names) {
		StringBuilder sb = new StringBuilder();
		int sum = 0;
		for(String key : names.keySet()){
			sum += names.get(key).size();
		}
		if(sum == 0){
			return null;
		}
		if(names.keySet().size() > 1) {
			sb.append("add(");
		}
		for(String nameType : names.keySet()){
			sb.append("abs(");
			sb.append("sub(" + nameType + "_i" + "," + (names.get(nameType).size()) + ")");
			sb.append(")");
			sb.append(",");
			// "abs(add(add(sub(Location_i,1),sub(Skill_i,1)),%20sub(Object_i,0)))%20asc,%20score%20desc";	
		}// abs(add(add(add(sub(Skill_i,0),sub(Object_i,0),sub(Location_i,1),)
		sb.deleteCharAt(sb.length()-1);
		if(names.keySet().size() > 1){
			sb.append(")");
		}
		return sb.toString();
	}
	
	public Map<String,List<NameResult>> parseQuestionIntoNameResults(String nlQuestion) throws SolrServerException{
		Map<String,List<NameResult>> names = new HashMap<String,List<NameResult>>();
		Scanner sc = new Scanner((String) prop.get("name_types"));
		sc.useDelimiter(", ");
		while(sc.hasNext()){
			names.put(sc.next(), new ArrayList<NameResult>());
		}
		SolrQuery treesQuery = new SolrQuery();
		SolrQuery namesQuery = new SolrQuery();
		namesQuery.addSort("score", ORDER.desc);
		//treesQuery.setRows(5);
        treesQuery.setRows(max_nr_of_trees);
		
		String[] words = nlQuestion.split("\\s+");

		// Find all names with their corresponding types in the question.
		// Add type and names to the hashmap names.
		for(String word : words){
			QueryResponse rsp;
			treesQuery.setQuery(word);
			rsp = treesServer.query(treesQuery);
			long numFound = rsp.getResults().getNumFound();
			// Check if current word exists in a generated query
			// E.g. word = 'people' or 'who' or 'and' ...
			if(numFound != 0){
				word += "~0.7";
			} 
			// Word is a name, we check if it exists in our name core in Solr
			// If it exists, we fix any misspellings.
			else {
				namesQuery.setQuery(word + "~0.7");
				namesQuery.addSort("abs(sub(length," + word.length() + "))", ORDER.asc);
				rsp = namesServer.query(namesQuery);
				List<NameResult> nameResults = rsp.getBeans(NameResult.class);
				// Assume a word can only be represented by one name in the index
				// i.e. we only care about best match (the first)
				if(!nameResults.isEmpty()){
					NameResult nameResult = nameResults.get(0);
					nameResult.setOriginalName(word);
					// Change the word into its type + index
					// E.g. 'Java' ==> 'Skill0'
					names.get(nameResult.getType()).add(nameResult);
				}
			}
		}
		return names;
	}
	
	public String replaceNames(String nlQuestion, Map<String,List<NameResult>> names, String replaceWith){
		for(String nameType : names.keySet()){
			for(int i=0; i < names.get(nameType).size(); i++){
				NameResult nameResult = names.get(nameType).get(i);
				String toReplace, replacement;
				if(replaceWith.equals("types")){ // We replace name with type
					toReplace = nameResult.getOriginalName();
					replacement = nameResult.getType() + i;
				}else{ // We replace type with name
					toReplace = nameResult.getType() + i;
					replacement = nameResult.getName();
				}
				nlQuestion = nlQuestion.replace(toReplace, replacement);
			}
		}
		return nlQuestion;
	}
    public List<Question> completeSentence2(String nlQuestion, String parseLang) throws SolrServerException{
        List<Question> questions = new ArrayList<Question>();
        
        SolrQuery treesQuery = new SolrQuery();
        // 
		treesQuery.setRows(max_nr_of_trees);
        
        Map<String,List<NameResult>> namesInQuestion = parseQuestionIntoNameResults(nlQuestion);
		nlQuestion = replaceNames(nlQuestion, namesInQuestion, "types");
        
        treesQuery.setQuery("linearizations:" + nlQuestion);
		treesQuery.addFilterQuery("lang:" + parseLang);
        
        // My guess: Weighting suggestions based on what already appear in the query 
        String sorting = getSort(namesInQuestion);
        if(sorting != null){
			treesQuery.addSort(SortClause.asc(sorting));			
		}
        
        // Sorting based on suggestion length and score
        treesQuery.addSort(SortClause.desc("score"));
		treesQuery.addSort(SortClause.asc("length"));
        
        // Run query for getting suggestion templates
        System.out.println(treesQuery.toString());
		QueryResponse treesResp = treesServer.query(treesQuery);
        List<TreeResult> templateLinearizationDocs = treesResp.getBeans(TreeResult.class);
        //SolrDocumentList templateLinearizationDocs = treesResp.getResults();
        
        Map<String, String> queryForNamesNotInQuestion 
                = createQueryForExcludingNames(namesInQuestion);
        
        for(TreeResult templateLinearizationDoc : templateLinearizationDocs) {
            
            // Find names that were not found in the current question
            Map<String, List<NameResult>> namesNotInQuestion
                    = findNamesNotInQuestion(namesInQuestion,
                            templateLinearizationDoc, 
                            queryForNamesNotInQuestion);
            
            // For each linearization template, create suggestions
            // using various combination of names
            List<Question> suggestions
                    = createSuggestionsForLinearization(
                    namesInQuestion,
                    templateLinearizationDoc,
                    namesNotInQuestion);
            
            questions.addAll(suggestions);
        }
        
        return questions;
    }

    public Map<String, List<NameResult>> findNamesNotInQuestion(Map<String, List<NameResult>> namesInQuestion, TreeResult templateLinearizationDoc, Map<String, String> queryForNamesNotInQuestion) throws SolrServerException {
        
        SolrQuery namesQuery = new SolrQuery();
        namesQuery.addSort("score", ORDER.desc);
        namesQuery.addSort("length", ORDER.asc);
        
        Map<String,List<NameResult>> namesNotInQuestion = new HashMap<>();
        for(String nameType : namesInQuestion.keySet()){
            
            int nameCountT = templateLinearizationDoc
                    .getNameCounts()
                    .get(nameType+"_i");
            String queryForNamesNotInQuestionT = queryForNamesNotInQuestion.get(nameType);
            List<NameResult> nameResultsT = namesInQuestion.get(nameType);
            
            SolrQuery namesQueryT = namesQuery.getCopy();
            int numberOfPlaceholdersOfType = nameCountT - nameResultsT.size();
            int numberOfWantedSuggestions = numberOfPlaceholdersOfType>0?
                    numberOfPlaceholdersOfType + nr_of_additional_suggestions : 0;
            namesQueryT.setRows(numberOfWantedSuggestions);
            namesQueryT.setQuery("*:*");
            namesQueryT.setFilterQueries(queryForNamesNotInQuestionT);
            
            // TODO We may be able to run queries before this method instead??
            QueryResponse namesRespT = namesServer.query(namesQueryT);
            
            // We rotate the list of names *not in the question* until we have wrapped
            // around. We should stop whenever we have a certain amount of suggestions.
            
            // Names in the current type that are not found in the question
            List<NameResult> namesNotInQuestionT
                    = namesRespT.getBeans(NameResult.class);
            namesNotInQuestion.put(nameType,namesNotInQuestionT);
        }
        return namesNotInQuestion;
    }

    public List<Question> createSuggestionsForLinearization(
            Map<String, List<NameResult>> namesInQuestion, 
            TreeResult templateLinearizationDoc,
             Map<String, List<NameResult>> namesNotInQuestion
            // Map<String, String> queryForNamesNotInQuestion
        ) throws SolrServerException {
        // We only care about one of the linearizations when it comes to
        // suggesting
        String linearization = templateLinearizationDoc.getLinearizations().get(0);
        List<Question> suggestions = new ArrayList<>();
        SolrQuery namesQuery = new SolrQuery();
        namesQuery.addSort("score", ORDER.desc);
        namesQuery.addSort("length", ORDER.asc);
        
        // Put the names that we think were in the original sentence
        // into the new linerization
        for(String nameType : namesInQuestion.keySet()){
            List<NameResult> namesInQuestionT = namesInQuestion.get(nameType);
            
            for (int i = 0; i < namesInQuestionT.size(); i++) {
                NameResult nameInQuestionT = namesInQuestionT.get(i);
                linearization = linearization.replace(nameInQuestionT.getType()+i, nameInQuestionT.getName());
            }
            
        }
        
        // How many placeholders for each type remain?
        Map<String,Integer> placeholderCount = getNumberOfFreePlaceholders(
                templateLinearizationDoc,
                namesInQuestion);
        int totalPlaceHolderCount = 0;
        for(Integer placeholderCount_:placeholderCount.values()){
            totalPlaceHolderCount+=placeholderCount_;
        }
        if(totalPlaceHolderCount>1){
            // We only want suggestions with one new name suggestion
            return new ArrayList();
        } else if (totalPlaceHolderCount==0){
            Question question = new Question();
            question.setLinearizations(new ArrayList<>(Arrays.asList(linearization)));
            suggestions.add(question);
            return suggestions;
        }
        for(String nameType : namesNotInQuestion.keySet()){
            List<NameResult> namesNotInQuestionT = namesNotInQuestion.get(nameType);
            if(namesNotInQuestionT.size()>0){
                for(NameResult nameR : namesNotInQuestionT){
                    String name = nameR.getName();
                    //String name = namesNotInQuestionT.get(0).getName();
                    String linearizationFilled = linearization.replaceFirst(nameType+"\\d{1}", name);
                    Question question = new Question();
                    question.setLinearizations(new ArrayList<>(Arrays.asList(linearizationFilled)));
                    suggestions.add(question);
                }
            }
        }
        /*Map<String,Integer> placeholderCount = getNumberOfFreePlaceholders(namesNotInQuestion);

        
        Map<String,List<NameResult>> namesNotInQuestionShifted = cloneNamesMap(namesInQuestion);
        for (int j = 0; j < numberOfPermutations(placeholderCount); j++){
            String suggestion = linearization;
            for(String nameType : namesNotInQuestionShifted.keySet()){
                List<NameResult> namesNotInQuestionShiftedT = namesNotInQuestionShifted.get(nameType);
                
                for (int i = 0; i < placeholderCount.get(nameType); i++) {
                    String nameNotInQuestionT = namesNotInQuestionShiftedT.get(i).getName();
                    suggestion = suggestion.replace(nameType+i, nameNotInQuestionT);
                }
            }
            Question question = new Question();
            question.setLinearizations(new ArrayList<>(Arrays.asList(suggestion)));
            suggestions.add(question);
            namesNotInQuestionShifted = shiftAllOnce(namesNotInQuestionShifted);
        }*/
        /*// Create suggestions by filling the linearization using not yet used names
        for(String nameType : namesNotInQuestion.keySet()){
            
            List<NameResult> namesNotInQuestionT
                    = namesNotInQuestion.get(nameType);
            
            List<NameResult> namesNotInQuestionTShifted = namesNotInQuestionT;
            for (int i = 0; i < namesNotInQuestionT.size(); i++) {
                
                
                String suggestion = linearization;
                for (int j = 0; j < namesNotInQuestionT.size(); j++) {
                    NameResult nameNotInQuestionTShifted = namesNotInQuestionTShifted.get(j);
                    suggestion = suggestion.replace(nameNotInQuestionTShifted.getType()+j, nameNotInQuestionTShifted.getName());
                }
                Question question = new Question();
                question.setLinearizations(new ArrayList<>(Arrays.asList(suggestion)));
                suggestions.add(question);
                
                // We shift the list of suggestion one step to produce a new ordering
                namesNotInQuestionTShifted = shiftOnce(namesNotInQuestionTShifted);
                
                // There should be one suggestion per shift
                // But for each suggestion we need to iterate
                // through all types. Therefore the type iteration must be
                // the innermost iteration. And so we need to shift all lists '
                // of typed names outside the type iteration.
            }
        }*/
        return suggestions;
    }

    public Map<String, String> createQueryForExcludingNames(Map<String, List<NameResult>> namesInQuestion) {
        // For each name already used, create a query excluding it
        // and sort the queries so that we have them separated by type
        Map<String,String> queryForNamesNotInQuestion = new HashMap<>();
        for(String nameType : namesInQuestion.keySet()){
            String queryForNamesNotInQuestionT = "type:" + nameType;
            List<NameResult> namesInQuestionT = namesInQuestion.get(nameType);
            for (int i = 0; i < namesInQuestionT.size(); i++) {
                NameResult nameInQuestionT = namesInQuestionT.get(i);
                queryForNamesNotInQuestionT += " -name:" + nameInQuestionT.getName();
            }
            queryForNamesNotInQuestion.put(nameType, queryForNamesNotInQuestionT);
        }
        return queryForNamesNotInQuestion;
    }

	/**
	 * Complete a string into a valid question. Psuedocode of the algorithm:
	 * 1. Determine the words of the string which are names
	 * 2. Replace those names with their type in the index, e.g. Java ==> Skill0
	 * 3. Ask the index of questions similar to the string
	 * 4. Change the types back into their names (not the original if misspelled)
	 * 5. If the question consists of more names than what is inputed, ask index
	 *    of more names and add them to the question
	 * 6. Return all questions
	 * 
	 * @param nlQuestion - A question in a natural language
	 * @param parseLang - A natural language
	 * @return Valid questions
	 * @throws SolrServerException
	 */
	public List<Question> completeSentence(String nlQuestion, String parseLang) throws SolrServerException{
		SolrQuery treesQuery = new SolrQuery();
		SolrQuery namesQuery = new SolrQuery();
		namesQuery.addSort("score", ORDER.desc);
		namesQuery.addSort("length", ORDER.asc);
		treesQuery.setRows(5);

		Map<String,List<NameResult>> namesInQuestion = parseQuestionIntoNameResults(nlQuestion);
		nlQuestion = replaceNames(nlQuestion, namesInQuestion, "types");
		treesQuery.setQuery("linearizations:" + nlQuestion);
		treesQuery.addFilterQuery("lang:" + parseLang);
		String sorting = getSort(namesInQuestion);
		if(sorting != null){
			treesQuery.addSort(SortClause.asc(sorting));			
		}
		treesQuery.addSort(SortClause.desc("score"));
		treesQuery.addSort(SortClause.asc("length"));
		//		treesQuery.addSort(SortClause.asc("length"));
		System.out.println(treesQuery.toString());
		QueryResponse rsp = treesServer.query(treesQuery);
		SolrDocumentList templateLinearizationDocs = rsp.getResults();
		System.out.println(templateLinearizationDocs);
        // FIX Pick the first Solr document -> The first set of linearizations
        // The other sets will just expand the abstract sentence. 
        // So put those suggestions after everything else! 
        // (Although they should arrive so late it wont matter anyway)
		List<Question> questions = new ArrayList<Question>();
		// Change back name types into their actual name
		// E.g. 'which persons know Skill0 ?' ==> 'which persons know Java ?'
		outer: for(SolrDocument templateLinearizationDoc : templateLinearizationDocs) {
			Question question = new Question();
			question.setLinearizations((List<String>) templateLinearizationDoc.get("linearizations"));

			for(String nameType : namesInQuestion.keySet()){
				// Store all names we have so far in notQuery
				// This will be used to find more names (and not those we already have)
				String notQuery = "type:" + nameType;
				List<NameResult> namesInQuestionT = namesInQuestion.get(nameType);
				for(int i=0; i < namesInQuestionT.size(); i++){
					NameResult nameResult = namesInQuestionT.get(i);
					notQuery += " -name:" + nameResult.getName();
				}
				// Iterate all linearizations to replace types
				for(int j=0; j < question.getLinearizations().size(); j++){
					String linearization = question.getLinearizations().get(j);
					for(int i=0; i < namesInQuestionT.size(); i++){
						NameResult nameInQuestionT = namesInQuestionT.get(i);
						linearization = linearization.replace(nameInQuestionT.getType()+i, nameInQuestionT.getName());
					}
					int nameCount = (int) templateLinearizationDoc.get(nameType + "_i");
					// If the query want more names than what we've got
					// We fetch more names to suggest
					if(namesInQuestionT.size() < nameCount){
						namesQuery.setRows(nameCount - namesInQuestionT.size());
						namesQuery.setQuery("*:*");
						namesQuery.setFilterQueries(notQuery);
						rsp = namesServer.query(namesQuery);
						Iterator<NameResult> namesNotInQuestionT = rsp.getBeans(NameResult.class).iterator();
						// Replace name types with new names
						for(int i=namesInQuestionT.size(); i < nameCount; i++){
							if(namesNotInQuestionT.hasNext()){
                                // What happens: The list of suggested names will be used to fill in the 
                                // linearizations once for each abstract syntax. But we want many suggestions
                                // per abstract syntax rule. (We probably only want suggetions that has the current
                                // question as a (approximate) substring of one of its linearizations.)
                                // We want many suggestions per abstract syntax rule each based off the same 
                                // concrete linearization but with variations on the names.
                                
                                // If we only had a single name to fill, then:
                                // We would iterate over each name and fill in the linearization to create
                                // a suggestion and then go to the next linearization. But we have many spots to
                                // fill in for each linearization. So it will have to be slightly more
                                // involved.
                                
                                // Suggested algo: For each linearization, replace first with the suggested name
                                // For the next suggestion, replace with the second suggested name first
                                // For the third suggestion, replace with the third suggested name first
                                // In other words, for each suggestion, shift the topmost suggested name
                                // to the end of the list of suggested names. Then create a new suggestion 
                                // from the *same* linearization. Only start on a new linearization once you
                                // have shifted the list of suggested names back around to the starting positions.
                                // (The order and composition of the list of suggested names are determined 
                                // by the solr query constructed elsewhere.)
								NameResult nameNotInQuestionT = namesNotInQuestionT.next();
								linearization = linearization.replace(nameType+i, nameNotInQuestionT.getName());
							} else{
								// If we cannot find any word to replace with, don't add this suggestion
								continue outer;
							}

						}
					}
					question.getLinearizations().set(j, linearization);
				}
			}
			questions.add(question);
		}
		return questions;		
	}

	public List<String> replaceAll(List<String> list, CharSequence target, CharSequence replacement){
		for(int i=0; i < list.size(); i++){
			list.set(i, list.get(i).replace(target, replacement));
		}
		return list;
	}

//	public Set<CompletionWord> completeQuery(String question, String parseLang) throws ParseError {
//		Concr lang = gr.getLanguages().get(parseLang);
//		String toComplete = "";
//		String[] words = question.split("\\s+");
//		//If last char is not whitespace
//		if(question.length() > 0 && question.charAt(question.length()-1) != ' '){ 
//			//Split words on whitespace
//			StringBuilder partialQuestion = new StringBuilder();
//			for(int i=0; i < words.length-1; i++){
//				partialQuestion.append(words[i] + " ");
//			}
//			question = partialQuestion.toString();
//			toComplete = words[words.length-1];
//		}
//
//		Set<CompletionWord> tokens = new HashSet<CompletionWord>();
//		for (TokenProb tp : lang.complete(gr.getStartCat(), question, toComplete)) {
//			boolean partial = false;
//			if(!toComplete.equals("")){
//				partial = tp.getToken().contains(toComplete);
//				//Omit to suggest the word to complete
//				if(tp.getToken().equals(toComplete)){
//					continue ;
//				}
//			}
//			tokens.add(new CompletionWord(partial, tp.getToken()));
//		}
//		//If list only contains the last word or is empty
//		if(tokens.size() <= 1){
//			//Also predict next word
//			for (TokenProb tp : lang.complete(gr.getStartCat(), question + toComplete + " ", "")) {
//				tokens.add(new CompletionWord(false, tp.getToken()));
//			}
//		}
//		return tokens;
//	}

    /**
     * Shift the elements of a list once to the left and append the 
     * shifted element to the end.
     */
    private <T> List<T> shiftOnce(List<T> listToShift) {
        if(listToShift.isEmpty()){return listToShift;}
        // LinkedList should be better for this particular use case but since 
        // we also copy the list I'm not sure...
        List<T> shiftedList = new LinkedList<>(listToShift);
        T shiftedElem = shiftedList.remove(0);
        shiftedList.add(shiftedElem);
        return shiftedList;
    }

    private Map<String, Integer> getNumberOfFreePlaceholders(
            TreeResult templateLinearizationDoc,
            Map<String, List<NameResult>> namesInQuestion) {
        HashMap<String, Integer> placeHolderCount = new HashMap<String, Integer>();
        for(String nameType : namesInQuestion.keySet()){
            placeHolderCount.put(nameType, 
                    templateLinearizationDoc.getNameCounts().get(nameType+"_i")
                        - namesInQuestion.get(nameType).size());
        }
        return placeHolderCount;
    }

    private Map<String, List<NameResult>> cloneNamesMap(Map<String, List<NameResult>> namesInQuestion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private int numberOfPermutations(Map<String, Integer> placeholderCount) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
