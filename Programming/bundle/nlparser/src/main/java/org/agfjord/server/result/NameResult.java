package org.agfjord.server.result;


import org.apache.solr.client.solrj.beans.Field;

public class NameResult {
	
	@Field("type")
	String type;
	@Field("name")
	String name;
	String originalName;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
}
