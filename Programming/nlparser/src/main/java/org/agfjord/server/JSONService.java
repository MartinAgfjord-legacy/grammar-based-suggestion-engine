package org.agfjord.server;

import java.io.IOException;
import java.util.List;





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
import org.agfjord.domain.Query;
import org.agfjord.grammar.Parser;
import org.apache.solr.client.solrj.SolrServerException;
import org.eclipse.persistence.oxm.JSONWithPadding;
import org.grammaticalframework.pgf.ParseError;

import com.google.gson.Gson;

@Path("/")
public class JSONService {
	
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getIt() {
//        return "Got it!";
//    }
	
	final SolrConnector solrConnector = new SolrConnector();

	@GET
	@Path("/parse")
	@Produces(MediaType.APPLICATION_JSON)
	public String parseQuestion(@QueryParam("q") String question, @QueryParam("callback") String callback) throws IOException {
		Parser p = new Parser();
		Gson gson = new Gson();
		String json = null;
		List<AbstractSyntaxTree> asts = null;
		try{
			asts = p.parse(question, "SimpleEng");
			json = gson.toJson(asts);
		}catch(ParseError e){
			json = "{ \"err\" : \"" + e.getToken() + "\" }";
		}
		return callback + "(" + new Gson().toJson(asts) + ")" ;	
	}
	
	@GET
	@Path("/solr/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public String solrQuery(@Context HttpServletRequest httpRequest) {
		httpRequest.getRequestURL();
		return "select" + "?" + httpRequest.getQueryString();
	}

	
	@GET
	@Path("/complete")
	@Produces(MediaType.APPLICATION_JSON)
	public String completeWord(@QueryParam("q") String question, 
			@QueryParam("callback") String callback) throws SolrServerException, IOException, ParseError {
		Parser p = new Parser();
		Gson gson = new Gson();
		return  gson.toJson(p.completeQuery(question, "SimpleEng"));
	}
}