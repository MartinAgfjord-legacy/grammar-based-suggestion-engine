package org.agfjord.graph;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.grammaticalframework.pgf.ParseError;


public class DataImportSolr {
	private SolrServer treesServer = new HttpSolrServer("http://localhost:8080/solr-instrucs/trees");
	private SolrServer namesServer = new HttpSolrServer("http://localhost:8080/solr-instrucs/names");
	private SolrServer relationsServer = new HttpSolrServer("http://localhost:8080/solr-instrucs/relations");
	private int id = 0;
	private int InstrucsId = 0;
	public void deleteAllNames() throws SolrServerException, IOException{
		namesServer.deleteByQuery("*:*");
		namesServer.commit();
	}
	
	public void deleteAllRelations() throws SolrServerException, IOException{
		relationsServer.deleteByQuery("*:*");
		relationsServer.commit();
	}

	public void importNames(String type, List<Map<String,Object>> nodes) throws SolrServerException, IOException {
		for(Map<String,Object> node : nodes){
			namesServer.add(createNameDocument(type, (String) node.get("name"), Long.toString((long)node.get("count")), id++));		
		}
		namesServer.commit();
		System.out.println("Imported " + type + " to Solr");
	}
	
	void importRelationsFromNeo4j() throws SolrServerException, IOException{
		this.deleteAllRelations();
		DataImportNeo4j neo4j = new DataImportNeo4j();
		int start = 0;
		int end = 500;
		List<Map<String,Object>> nodes = null;
		do {
			nodes = neo4j.allWithLabel("Person", start, end);
			this.importRelations(nodes);
			start += 500;
			end += 500;
		} while(!nodes.isEmpty());
		start = 0;
		end = 500;
		do {
			nodes = neo4j.allWithLabel("Organization", start, end);
			this.importRelations(nodes);
			start += 500;
			end += 500;
		} while(!nodes.isEmpty());

	}
	
	public void importRelations(List<Map<String,Object>> nodes) throws SolrServerException, IOException{
		for(Map<String,Object> node : nodes){
			relationsServer.add(createSolrDocument(node, id++));
		}
		relationsServer.commit();
	}

	/*
	 * Add linearizations to the Solr tree core
	 */
	public void addInstrucsToSolr(List<Instruction> Instrucs) throws SolrServerException, IOException, ParseError{
		for(Instruction question : Instrucs){
			SolrInputDocument solrInputDoc = new SolrInputDocument();
			solrInputDoc.addField("id", InstrucsId++);
			solrInputDoc.addField("ast", question.getAst());
			solrInputDoc.addField("linearizations", question.getLinearizations());
			solrInputDoc.addField("length", question.getLinearizations()[0].length());
			solrInputDoc.addField("lang", question.getLang());
			Map<String,Integer> nameCounts = question.getNameCounts();
			for(String name : nameCounts.keySet()){
				solrInputDoc.addField(name + "_i", nameCounts.get(name));
			}
			treesServer.add(solrInputDoc);
		}
		treesServer.commit();
	}
	
	public void deleteAllInstrucs() throws SolrServerException, IOException{
		treesServer.deleteByQuery("*:*");
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
		Map<String,Object> fieldsAndValues = new HashMap<String,Object>();
		fieldsAndValues.put("type", type);
		fieldsAndValues.put("name", name);
		fieldsAndValues.put("count", count);
		fieldsAndValues.put("length", Integer.toString(name.length()));
		return createSolrDocument(fieldsAndValues, id);
	}


	private SolrInputDocument createSolrDocument(Map<String,Object> fieldsAndValues, int id){
		SolrInputDocument solrInputDoc = new SolrInputDocument();
		solrInputDoc.addField("id", id);
		for(String key : fieldsAndValues.keySet()){
			solrInputDoc.addField(key, fieldsAndValues.get(key));
		}
		return solrInputDoc;
	}
}
