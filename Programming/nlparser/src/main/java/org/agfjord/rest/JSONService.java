package org.agfjord.rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.agfjord.domain.Query;
import org.agfjord.domain.Result;
import org.agfjord.grammar.Parser;
import org.grammaticalframework.pgf.ParseError;

import com.google.gson.Gson;
import com.sun.jersey.api.json.JSONWithPadding;
@Path("/")
public class JSONService {

	@GET
	@Path("/parse")
	@Produces("application/javascript")
	public JSONWithPadding foo(@QueryParam("q") String question, @QueryParam("from") 
					  String from, @QueryParam("to") String to, @QueryParam("callback") String callback) throws ParseError {
		Parser p = new Parser();
		Gson gson = new Gson();
		String json =gson.toJson(p.parse(question, from, to));
		return new JSONWithPadding(json,callback);
	}
}