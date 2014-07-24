package org.agfjord.grammar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.agfjord.domain.AbstractSyntaxTree;
import org.agfjord.domain.CompletionWord;
import org.agfjord.domain.Query;
import org.agfjord.server.result.NameResult;
import org.agfjord.server.result.Question;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.NercLiteralCallback;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.PGFError;
import org.grammaticalframework.pgf.ParseError;

import com.google.gson.Gson;

public class Parser {

	private PGF gr;
	private SolrServer treesServer = new HttpSolrServer("http://localhost:8080/solr-instrucs/trees");
	private SolrServer namesServer = new HttpSolrServer("http://localhost:8080/solr-instrucs/names");
	final private Properties prop = new Properties();


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
			sb.append("sub(" + nameType + "_i" + "," + names.get(nameType).size() + ")");
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
		treesQuery.setRows(5);
		
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

		Map<String,List<NameResult>> names = parseQuestionIntoNameResults(nlQuestion);
		nlQuestion = replaceNames(nlQuestion, names, "types");
		treesQuery.setQuery("linearizations:" + nlQuestion);
		treesQuery.addFilterQuery("lang:" + parseLang);
		String sorting = getSort(names);
		if(sorting != null){
			treesQuery.addSort(SortClause.asc(sorting));			
		}
		treesQuery.addSort(SortClause.desc("score"));
		treesQuery.addSort(SortClause.asc("length"));
		//		treesQuery.addSort(SortClause.asc("length"));
		System.out.println(treesQuery.toString());
		QueryResponse rsp = treesServer.query(treesQuery);
		SolrDocumentList docs = rsp.getResults();
		System.out.println(docs);
		List<Question> questions = new ArrayList<Question>();
		// Change back name types into their actual name
		// E.g. 'which persons know Skill0 ?' ==> 'which persons know Java ?'
		outer: for(SolrDocument doc : docs) {
			Question question = new Question();
			question.setLinearizations((List<String>) doc.get("linearizations"));

			for(String nameType : names.keySet()){
				// Store all names we have so far in notQuery
				// This will be used to find more names (and not those we already have)
				String notQuery = "type:" + nameType;
				List<NameResult> nameResults = names.get(nameType);
				for(int i=0; i < nameResults.size(); i++){
					NameResult nameResult = nameResults.get(i);
					notQuery += " -name:" + nameResult.getName();
				}
				// Iterate all linearizations to replace types
				for(int j=0; j < question.getLinearizations().size(); j++){
					String linearization = question.getLinearizations().get(j);
					for(int i=0; i < nameResults.size(); i++){
						NameResult nameResult = nameResults.get(i);
						linearization = linearization.replace(nameResult.getType()+i, nameResult.getName());
					}
					int nameCount = (int) doc.get(nameType + "_i");
					// If the query want more names than what we've got
					// We fetch more names to suggest
					if(nameResults.size() < nameCount){
						namesQuery.setRows(nameCount - nameResults.size());
						namesQuery.setQuery("*:*");
						namesQuery.setFilterQueries(notQuery);
						rsp = namesServer.query(namesQuery);
						Iterator<NameResult> suggestionNames = rsp.getBeans(NameResult.class).iterator();
						// Replace name types with new names
						for(int i=nameResults.size(); i < nameCount; i++){
							if(suggestionNames.hasNext()){
								NameResult suggestionName = suggestionNames.next();
								linearization = linearization.replace(nameType+i, suggestionName.getName());
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

}
