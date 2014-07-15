package org.agfjord.graph;


import org.apache.solr.client.solrj.beans.Field;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Instruction {
	
	@Field("id")
	int id;
	@Field("ast")
	String ast;
	@Field("linearizations")
	String[] linearizations;
	@Field("lang")
	String lang;

	Map<String,Integer> nameCounts;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public String[] getLinearizations() {
		return linearizations;
	}
	public void setLinearizations(String[] linearizations) {
		this.linearizations = linearizations;
	}
	public Map<String, Integer> getNameCounts() {
		return nameCounts;
	}
	public void setNameCounts(Map<String, Integer> nameCounts) {
		this.nameCounts = nameCounts;
	}
	
}
