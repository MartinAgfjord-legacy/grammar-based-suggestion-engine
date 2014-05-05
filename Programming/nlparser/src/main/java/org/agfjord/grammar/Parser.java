package org.agfjord.grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.agfjord.domain.AbstractSyntaxTree;
import org.agfjord.domain.CompletionWord;
import org.agfjord.domain.Query;
import org.agfjord.server.result.NameResult;
import org.agfjord.server.result.Question;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
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
import org.grammaticalframework.pgf.TokenProb;

public class Parser {

	private PGF gr;
	private SolrServer treesServer = new HttpSolrServer("http://localhost:8983/solr/trees");
	private SolrServer namesServer = new HttpSolrServer("http://localhost:8983/solr/names");
	Map<String,List<NameResult>> names = new HashMap<String,List<NameResult>>();

	static Logger log = Logger.getLogger(
			Parser.class.getName());

	public Parser() throws SolrServerException {
		File pgf = new File("/tmp/Simple.pgf");
		try {
			gr = PGF.readPGF(pgf.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PGFError e) {
			e.printStackTrace();
		}
		gr.getLanguages().get("SimpleEng").addLiteral("Symb", new NercLiteralCallback());
		initialize();
	}

	/*
	 * Fetch all name types in advance
	 */
	public void initialize() throws SolrServerException{
		SolrQuery query = new SolrQuery();
		query.setFacet(true);
		query.addFacetField("type");
		query.setRows(0);
		QueryResponse rsp = namesServer.query(query);
		for(Count nameType : rsp.getFacetField("type").getValues()){
			names.put(nameType.getName(), new ArrayList<NameResult>());
		}
	}

	public List<AbstractSyntaxTree> parse(String question, String parseLang) throws ParseError {
		Iterable<ExprProb> exprProbs;
		Map<String,List<Query>> astQuery = new HashMap<String,List<Query>>();
		exprProbs = gr.getLanguages().get(parseLang).parse(gr.getStartCat(), question);

		for(String key : gr.getLanguages().keySet()){
			Concr lang = gr.getLanguages().get(key);
			for(ExprProb exprProb : exprProbs) {
				List<Query> qs = astQuery.get(exprProb.getExpr().toString());
				if(qs == null){
					qs = new ArrayList<Query>();
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
		SolrQuery query = new SolrQuery();
		query.addSort("score", ORDER.desc);
		query.setRows(5);
		// Map all types of names with a list of words of the specific type

		// In order to know which words that are names, we split the question
		// into words and make a query to the name-core, if there is a match for
		// a word then we can determine the type of this word. In addition, we also
		// retrieve the correct spelling of this word
		// E.g. 'java' is of type Skill
		String[] words = nlQuestion.split("\\s+");
		nlQuestion = "";
		for(String word : words){
			// Accept similar words
			word += "~0.7";
			query.setQuery(word);
			QueryResponse rsp = namesServer.query(query);
			List<NameResult> nameResults = rsp.getBeans(NameResult.class);
			// Assume a word can only be represented by one name in the index
			// i.e. we only care about the first match
			if(!nameResults.isEmpty()){
				NameResult nameResult = nameResults.get(0);
				nameResult.setOriginalName(word);
				names.get(nameResult.getType()).add(nameResult);
			}
			nlQuestion += word + " ";
		}
		// Iterate through all kind of names
		for(String nameType : names.keySet()){
			List<NameResult> nameResults = names.get(nameType);
			// Replace all names into their type
			// E.g. 'which persons know java ?' ==> 'which persons know Skill0 ?'
			// The resulting string give a precise match in Solr
			for(int i=0; i < nameResults.size(); i++){
				NameResult nameResult = nameResults.get(i);
				nlQuestion = nlQuestion.toString().replace(nameResult.getOriginalName(), nameResult.getType() + i);
			}
		}
		query.setQuery(nlQuestion);
		QueryResponse rsp = treesServer.query(query);
		SolrDocumentList docs = rsp.getResults();
		//		List<Question> questions = rsp.getBeans(Question.class);
		List<Question> questions = new ArrayList<Question>();
		// Change back name types into their actual name
		// E.g. 'which persons know Skill0 ?' ==> 'which persons know Java ?'
		outer: for(SolrDocument doc : docs) {
			Question question = new Question();
			question.setLinearizations((List<String>) doc.get("linearizations"));
			for(int j=0; j < question.getLinearizations().size(); j++){
				String linearization = question.getLinearizations().get(j);
				for(String nameType : names.keySet()){
					List<NameResult> nameResults = names.get(nameType);
					String notQuery = "";
					for(int i=0; i < nameResults.size(); i++){
						NameResult nameResult = nameResults.get(i);
						linearization = linearization.replace(nameResult.getType()+i, nameResult.getName());
						notQuery += " -name:" + nameResult.getName();
					}
					notQuery += " type:" + nameType;
					int nameCount = (int) doc.get(nameType + "_i");
					// If the query want more names than what we've got
					// We fetch more names to suggest
					if(nameResults.size() < nameCount){
						query.setRows(nameCount - nameResults.size());
						query.setQuery("*:*");
						query.setFilterQueries(notQuery);
						rsp = namesServer.query(query);
						Iterator<NameResult> suggestionNames = rsp.getBeans(NameResult.class).iterator();
						for(int i=nameResults.size(); i < nameCount; i++){
							if(suggestionNames.hasNext()){
								NameResult suggestionName = suggestionNames.next();
								linearization = linearization.replace(nameType+i, suggestionName.getName());
							} else{
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

	public Set<CompletionWord> completeQuery(String question, String parseLang) throws ParseError {
		Concr lang = gr.getLanguages().get(parseLang);
		String toComplete = "";
		String[] words = question.split("\\s+");
		//If last char is not whitespace
		if(question.length() > 0 && question.charAt(question.length()-1) != ' '){ 
			//Split words on whitespace
			StringBuilder partialQuestion = new StringBuilder();
			for(int i=0; i < words.length-1; i++){
				partialQuestion.append(words[i] + " ");
			}
			question = partialQuestion.toString();
			toComplete = words[words.length-1];
		}

		Set<CompletionWord> tokens = new HashSet<CompletionWord>();
		for (TokenProb tp : lang.complete(gr.getStartCat(), question, toComplete)) {
			boolean partial = false;
			if(!toComplete.equals("")){
				partial = tp.getToken().contains(toComplete);
				//Omit to suggest the word to complete
				if(tp.getToken().equals(toComplete)){
					continue ;
				}
			}
			tokens.add(new CompletionWord(partial, tp.getToken()));
		}
		//If list only contains the last word or is empty
		if(tokens.size() <= 1){
			//Also predict next word
			for (TokenProb tp : lang.complete(gr.getStartCat(), question + toComplete + " ", "")) {
				tokens.add(new CompletionWord(false, tp.getToken()));
			}
		}
		return tokens;
	}

}
