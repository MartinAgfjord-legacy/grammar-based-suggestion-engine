package org.agfjord.domain;

import java.util.List;

public class Result {

	Query query;
	String ast;
	List<Query> result;

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query= query;
	}


	public String getAst() {
		return ast;
	}

	public void setAst(String ast) {
		this.ast = ast;
	}

	public List<Query> getResult() {
		return result;
	}

	public void setResult(List<Query> result) {
		this.result = result;
	}

}
