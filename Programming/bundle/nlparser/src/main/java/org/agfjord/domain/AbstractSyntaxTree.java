package org.agfjord.domain;

import java.util.List;
import java.util.Set;

public class AbstractSyntaxTree {
	String ast;
	Set<Query> linearizations;
	
	public AbstractSyntaxTree(String ast, Set<Query> linearizations){
		this.ast = ast;
		this.linearizations = linearizations;
	}
	
	public String getAst() {
		return ast;
	}
	public void setAst(String ast) {
		this.ast = ast;
	}
	public Set<Query> getLinearizations() {
		return linearizations;
	}
	public void setLinearizations(Set<Query> linearizations) {
		this.linearizations = linearizations;
	}
	
}
