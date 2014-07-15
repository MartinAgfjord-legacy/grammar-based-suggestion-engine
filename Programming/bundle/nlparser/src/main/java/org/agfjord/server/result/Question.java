package org.agfjord.server.result;

import org.apache.solr.client.solrj.beans.Field;
import java.util.List;

public class Question {
	
	@Field("ast")
	String ast;
	String lang;
	@Field("linearizations")
	List<String> linearizations;
	@Field("names")
	List<String> names;
	
	@Field("nameCount")
	List<Integer> nameCount;
	
	public String getAst() {
		return ast;
	}
	public void setAst(String ast) {
		this.ast = ast;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public List<String> getLinearizations() {
		return linearizations;
	}
	public void setLinearizations(List<String> linearizations) {
		this.linearizations = linearizations;
	}
	public List<String> getNames() {
		return names;
	}
	public void setNames(List<String> names) {
		this.names = names;
	}
	public List<Integer> getNameCount() {
		return nameCount;
	}
	public void setNameCount(List<Integer> nameCount) {
		this.nameCount = nameCount;
	}

	

}
