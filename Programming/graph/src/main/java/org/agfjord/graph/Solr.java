package org.agfjord.graph;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.grammaticalframework.pgf.ParseError;


public class Solr {
	private SolrServer treesServer = new HttpSolrServer("http://localhost:8983/solr/trees");
	private SolrServer namesServer = new HttpSolrServer("http://localhost:8983/solr/names");
	private SolrServer findwiseServer = new HttpSolrServer("http://clouddiscoverprod1.corp.findwise.net:8080/solr/main");
	private int id = 0;
	public void deleteAllNames() throws SolrServerException, IOException{
		namesServer.deleteByQuery("*:*");
		namesServer.commit();
	}

	public void addNodesToSolr(String type, List<Map<String,Object>> nodes) throws SolrServerException, IOException {
		for(Map<String,Object> node : nodes){
			namesServer.add(createNameDocument(type, (String) node.get("name"), Long.toString((long)node.get("count")), id++));
			namesServer.commit();			
		}
	}

	public Set<String> fetchSkillsFromFindwise() throws SolrServerException{
		Set<String> skills = new HashSet<String>();
		for(String expertises : fetchNamesFromFindwise("object_type : person", "expertise")){
			if(expertises.contains("*")){
				for(String expertise : expertises.split("\\n")){
					if(expertise.contains("*")){
						skills.add(normalizeName(expertise));
					}
				}
			} else{
				continue;
			}
		}
		return skills;
	}
	
	public Set<String> fetchLocationsFromFindwise() throws SolrServerException {
		List<String> names = fetchNamesFromFindwise("object_type:person", "location");
		Set<String> locations = new HashSet<String>();
		for(String name : names){
			locations.add(normalizeName(name));
		}
		return locations;
	}

	public Set<String> fetchProjectNamesFromFindwise() throws SolrServerException {
		List<String> names = fetchNamesFromFindwise("object_type:project", "project_name");
		Set<String> projectNames = new HashSet<String>();
		for(String name : names){
			projectNames.add(normalizeName(name));
		}
		return projectNames;
	}

	private List<String> fetchNamesFromFindwise(String query, String field) throws SolrServerException{
		int start = 0;
		int rows = 10000;
		int end = rows;
		long numFound = Integer.MAX_VALUE;
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		solrQuery.set("fl", field);
		List<String> names = new ArrayList<String>();
		do{
			solrQuery.setStart(start);
			solrQuery.setRows(rows);
			QueryResponse rsp = findwiseServer.query(solrQuery);
			numFound = rsp.getResults().getNumFound();
			SolrDocumentList docs = rsp.getResults();
			for(SolrDocument doc : docs) {
				if(doc.get(field) != null){
					String name = (String) doc.get(field);
					names.add(name);
				}
			}
			start += rows;
			end += rows;
		} while(end < numFound);
		return names;
	}

	private String normalizeName(String name){
		name = name.replace("\\n", "");
		name = name.replace(":", "");
		name = name.replace("*", "");
		name = name.replace("-", " ");
		name = name.replaceAll("[^A-z0-9 åäöÅÄÖ]", "");
		name = name.toLowerCase();
		name = capitalizeEachFirstLetter(name);
		return name;
	}

	private String capitalizeEachFirstLetter(String words){
		String[] wordArr = words.split("\\s+");
		for(int i=0; i < wordArr.length; i++){
			String word = wordArr[i];
			if(word.isEmpty()){
				wordArr[i] = null;
				continue;
			}
			// Capitalize first letter
			char first = Character.toUpperCase(word.charAt(0));
			String tmp = Character.toString(first);
			// If word is more than one character
			if(word.length() > 1){
				// Add the rest
				tmp += word.substring(1, word.length());
			}
			wordArr[i] = tmp;
		}
		words = "";
		// Concatenate wordArr with whitespace in between each word
		for(int i=0; i< wordArr.length-1; i++){
			if(wordArr[i] != null){
				words += wordArr[i] + " ";
			}
		}
		// Add the last word
		if(wordArr[wordArr.length-1] != null){
			words = words + wordArr[wordArr.length-1];
		}
		return words;
	}

	/*
	 * Add linearizations to the Solr tree core
	 */
	public void addQuestionsToSolr(List<Question> questions) throws SolrServerException, IOException, ParseError{
		treesServer.deleteByQuery("*:*");
		int id = 0;
		for(Question question : questions){
			SolrInputDocument solrInputDoc = new SolrInputDocument();
			solrInputDoc.addField("id", id++);
			solrInputDoc.addField("ast", question.getAst());
			solrInputDoc.addField("linearizations", question.getLinearizations());
			solrInputDoc.addField("length", question.getLinearizations().get(0).length());
			
			Map<String,Integer> nameCounts = question.getNameCounts();
			for(String name : nameCounts.keySet()){
				solrInputDoc.addField(name + "_i", nameCounts.get(name));
			}
			treesServer.add(solrInputDoc);
		}
		treesServer.commit();
	}
	/*
	 * Add names to the Solr name core
	 */
//	public void addNamesToSolr() throws SolrServerException, IOException {
//		namesServer.add(createNameDocument("Skill", "Java", id++));
//		namesServer.add(createNameDocument("Skill", "C", id++));
//		namesServer.add(createNameDocument("Skill", "Python", id++));
//		namesServer.add(createNameDocument("Object", "Solr", id++));
//		namesServer.add(createNameDocument("Location", "Gothenburg", id++));
//		namesServer.add(createNameDocument("Location", "Stockholm", id++));
//		namesServer.add(createNameDocument("Location", "Malmo", id++));
//		namesServer.commit();
//	}


	/*
	 * Private methods used by other methods in this class
	 */
	private SolrInputDocument createNameDocument(String type, String name, String count, int id){
		Map<String,String> fieldsAndValues = new HashMap<String,String>();
		fieldsAndValues.put("type", type);
		fieldsAndValues.put("name", name);
		fieldsAndValues.put("count", count);
		fieldsAndValues.put("length", Integer.toString(name.length()));
		
		return createSolrDocument(fieldsAndValues, id);
	}


	private SolrInputDocument createSolrDocument(Map<String,String> fieldsAndValues, int id){
		SolrInputDocument solrInputDoc = new SolrInputDocument();
		solrInputDoc.addField("id", id);
		for(String key : fieldsAndValues.keySet()){
			solrInputDoc.addField(key, fieldsAndValues.get(key));
		}
		System.out.println(solrInputDoc.toString());
		return solrInputDoc;
	}
}
