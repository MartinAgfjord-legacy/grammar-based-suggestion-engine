package org.agfjord.domain;

import java.util.List;

public class Linearlization {
	String ast;
	List<Query> result;
	
	public Linearlization(String ast, List<Query> result){
		this.ast = ast;
		this.result = result;
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
