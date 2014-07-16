package org.agfjord.domain;

public class Query {

	String query;
	String language;
	String result;
	
	public Query(String query, String language) {
		this.query = query;
		this.language = language;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	@Override
	public int hashCode() {
		return (query + language).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this.getClass() == obj.getClass()){
			Query query = (Query) obj;
			return (this.query + this.language).equals(query.query + query.language);
		} else return false;
	}
	
	
	
}
