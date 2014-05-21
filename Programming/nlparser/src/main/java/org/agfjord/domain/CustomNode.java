package org.agfjord.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

public class CustomNode {
	
	
	
	long id;
	Map<String,String> properties = new HashMap<String,String>();
	List<Relationship> relationships = new ArrayList<Relationship>();
	
	
	public CustomNode(Node node){
		this.id = node.getId();
		for(String key : node.getPropertyKeys()){
			this.properties.put(key, "" + node.getProperty(key));
		}
		for(org.neo4j.graphdb.Relationship relationship : node.getRelationships(Direction.OUTGOING)){
			this.relationships.add(new Relationship(relationship.getType().name(), (String) relationship.getEndNode().getProperty("name")));
		}
		
	}
	
	class Relationship {
		String name;
		String object;
		
		Relationship(String name, String object){
			this.name = name;
			this.object = object;
		}
	}
}
