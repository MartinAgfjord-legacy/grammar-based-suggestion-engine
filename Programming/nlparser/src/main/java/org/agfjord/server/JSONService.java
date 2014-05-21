package org.agfjord.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;












import java.util.Map;

import javax.servlet.http.HttpServletRequest;
//
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.agfjord.domain.AbstractSyntaxTree;
import org.agfjord.domain.CustomNode;
import org.agfjord.domain.Query;
import org.agfjord.grammar.Parser;
import org.apache.solr.client.solrj.SolrServerException;
import org.eclipse.persistence.oxm.JSONWithPadding;
import org.grammaticalframework.pgf.ParseError;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.core.NodeProxy;

import com.google.gson.Gson;

@Path("/")
public class JSONService {
	
	final SolrConnector solrConnector = new SolrConnector();

	@GET
	@Path("/parse")
	@Produces(MediaType.APPLICATION_JSON)
	public String parseQuestion(@QueryParam("q") String question, @QueryParam("callback") String callback) throws IOException, SolrServerException {
		Parser p = new Parser();
		Gson gson = new Gson();
		String json = null;
		List<AbstractSyntaxTree> asts = null;
		try{
			asts = p.parse(question, "QuestionsEng");
			json = gson.toJson(asts);
		}catch(ParseError e){
			json = "{ \"err\" : \"" + e.getToken() + "\" }";
		}
		return callback + "(" + json + ")" ;	
	}
	
	@GET
	@Path("/solr/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public String solrQuery(@Context HttpServletRequest httpRequest) throws SolrServerException, IOException {
		String callback = httpRequest.getParameter("callback");
		httpRequest.getRequestURL();
		return callback + "(" + solrConnector.queryDebug("select" + "?" + httpRequest.getQueryString()) + ")";
	}

	
	@GET
	@Path("/complete")
	@Produces(MediaType.APPLICATION_JSON)
	public String completeWord(@QueryParam("q") String question, 
			@QueryParam("callback") String callback) throws SolrServerException, IOException, ParseError {
		Parser p = new Parser();
		Gson gson = new Gson();
		return  callback + "(" + gson.toJson(p.completeQuery(question, "QuestionsEng")) + ")";
	}
	
	@GET
	@Path("/completeSentence")
	@Produces(MediaType.APPLICATION_JSON)
	public String completeSentence(@QueryParam("q") String question, 
			@QueryParam("callback") String callback) throws SolrServerException, IOException, ParseError {
		Parser p = new Parser();
		Gson gson = new Gson();
		return  callback + "(" + gson.toJson(p.completeSentence(question, "QuestionsEng")) + ")";
	}
	
	@POST
	@Path("/neo4j")
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	public String neo4j(@FormParam("q") String question, 
			@QueryParam("callback") String callback) throws SolrServerException, IOException, ParseError {
		Parser p = new Parser();
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("/home/eidel/Documents/School/Exjobb/Programming/graph/data");
		Gson gson = new Gson();
		String json;
		
		try(Transaction tx = graphDb.beginTx()){
			ExecutionEngine engine = new ExecutionEngine(graphDb);
			System.out.println("######" + question);
			ExecutionResult result = engine.execute(question);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			while(result.iterator().hasNext()){
				Map<String,Object> map = result.iterator().next();
				Map<String,Object> foo = new HashMap<String,Object>();
				for(String key : map.keySet()){
					System.out.println(map.get(key).getClass() + "");
					if(map.get(key) instanceof NodeProxy){
						System.out.println("INSIDE");
						CustomNode node = new CustomNode(((NodeProxy)map.get(key)));
						foo.put(key, node);
					}else {
						foo.put(key, map.get(key));
					}
				}
				list.add(foo);
			}
			json = gson.toJson(list);
			tx.success();
			tx.close();
		}finally{
			graphDb.shutdown();			
		}

		return  callback + "(" + json + ")";
	}
}