package org.agfjord.domain;

import java.util.List;

public class AbstractSyntaxTree {
	String ast;
	List<Query> linearizations;
	
	public AbstractSyntaxTree(String ast, List<Query> linearizations){
		this.ast = ast;
		this.linearizations = linearizations;
	}
	
	public String getAst() {
		return ast;
	}
	public void setAst(String ast) {
		this.ast = ast;
	}
	public List<Query> getLinearizations() {
		return linearizations;
	}
	public void setLinearizations(List<Query> linearizations) {
		this.linearizations = linearizations;
	}
	
}
