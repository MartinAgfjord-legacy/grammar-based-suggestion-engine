package org.agfjord.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.core.NodeProxy;

public class DataImportNeo4j 
{
	private static enum RelTypes implements RelationshipType
	{
		KNOWS, WORKS_IN, WORKS_WITH, MEMBER_OF, USES
	};
	private GraphDatabaseService graphDb;
	public DataImportNeo4j(){
		initDatabase();
	}
	
	private void initDatabase(){
		File file = new File("data");
		boolean exists = file.list().length != 0;
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(file.getAbsolutePath());
		registerShutdownHook(graphDb);
		if(!exists){
			query("CREATE INDEX ON :Person(name)");
			query("CREATE INDEX ON :Location(name)");
			query("CREATE INDEX ON :Project(name)");
			query("CREATE INDEX ON :Expertise(name)");
		}
	}

	public List<Map<String,Object>> allWithLabel(String label, int start, int end){
		try(Transaction tx = graphDb.beginTx()){
			String query = "MATCH (n:" + label + ")-[]-() RETURN DISTINCT n SKIP {skip} LIMIT {limit}";
			ResourceIterator<Map<String,Object>> result = query(query.replace("{skip}", start+"").replace("{limit}", end-start+""));
			List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
			while(result.hasNext()){
				Map<String,Object> map = new HashMap<String,Object>();
				NodeProxy node = (NodeProxy) result.next().get("n");
				map.put("object_type", label);
				map.put("name", node.getProperty("name"));
				System.out.println(node.getRelationships().toString());
				for(Relationship relationship : node.getRelationships(Direction.OUTGOING)){
					if(map.get(relationship.getType().name()) == null){
						map.put(relationship.getType().name(), new ArrayList<String>());
						System.out.println(map);
					}
					ArrayList<String> list = (ArrayList<String>) map.get(relationship.getType().name());
					list.add((String) relationship.getEndNode().getProperty("name"));
				}
				nodes.add(map);
			}
			tx.success();
			return nodes;
		}
	}


	public List<Map<String,Object>> fetchAllLabelWithRelation(String label){
		try(Transaction tx = graphDb.beginTx()){
			ResourceIterator<Map<String,Object>> iterator = query("MATCH (n:" + label + ") - [] - () RETURN DISTINCT count(n.name), n.name");
			List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
			while(iterator.hasNext()){
				Map<String,Object> node = iterator.next();
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("name", node.get("n.name"));
				map.put("count", node.get("count(n.name)"));
				result.add(map);
			}
			tx.success();
			return result;
		}
	}

	public void deleteDatabase() throws IOException {
		graphDb.shutdown();
		File file = new File("data");
		delete(file);
		file.mkdir();
		initDatabase();
	}

	private void delete(File file)
			throws IOException{

		if(file.isDirectory()){
			//directory is empty, then delete it
			if(file.list().length==0){
				file.delete();

			}else {
				String files[] = file.list();
				for (String temp : files) {
					//construct the file structure
					File fileDelete = new File(file, temp);
					//recursive delete
					delete(fileDelete);
				}
				//check the directory again, if empty then delete it
				if(file.list().length==0){
					file.delete();
				}
			}
		} else{
			//if file, then delete it
			file.delete();
		}
	}

	public void createPersons(int count){
		try(Transaction tx = graphDb.beginTx()){
			// Generate random names from first names and last names
			for(String name : generateRandomNames(count)){
				// Create a person node
				Node person = graphDb.createNode(DynamicLabel.label("Person"));
				person.setProperty("name", name);
			}
			tx.success();
		}
		System.out.println("Imported Persons");
	}

	public void createPersonRelations(){
		int counter = 0;
		try(Transaction tx = graphDb.beginTx()){
			List<Node> persons = fetchAll("Person");
			for(Node person : persons){
				// Create relationships with 5 random expertises
				for(Node expertise : fetchRandom("Expertise", 5)){
					person.createRelationshipTo(expertise, RelTypes.KNOWS);
				}

				// Create relationship with 1 or 2 random locations
				int locationCount = Math.random() >= 0.7 ? 1 : 2;
				for(Node expertise : fetchRandom("Location", locationCount)){
					person.createRelationshipTo(expertise, RelTypes.WORKS_IN);
				}

				// Create relationships with 4 random organizations
				for(Node expertise : fetchRandom("Organization", 10)){
					person.createRelationshipTo(expertise, RelTypes.WORKS_WITH);
				}
				counter++;
				if(persons.size() >= 1000 && counter % 100 == 0){
					System.out.println("Imported " + (int) ((double) counter / persons.size() * 100) + "% of relations");
				}
			}
			tx.success();
		}
		System.out.println("Imported Person Relations");
		
	}

	public void createOrganizationRelations(){
		try(Transaction tx = graphDb.beginTx()){
			for(Node organization : fetchAll("Organization")){
				int limit = randInt(1, 10);
				for(Node module : fetchRandom("Module", limit)) {
					organization.createRelationshipTo(module, RelTypes.USES);
				}
			}
			tx.success();
		}
		System.out.println("Imported Organization Relations");
	}
	
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}

	private List<Node> fetchRandom(String label, int count){
		List<Node> nodes = new ArrayList<Node>();
		ResourceIterator<Map<String,Object>> result = query("MATCH (n:" + label + ") WITH n, rand() AS r ORDER BY r LIMIT " + count + " RETURN id(n),n.name");
		while(result.hasNext()){
			long id = (long) result.next().get("id(n)");
			nodes.add(graphDb.getNodeById(id));
		}
		return nodes;
	}

	private List<Node> fetchAll(String label){
		List<Node> nodes = new ArrayList<Node>();
		ResourceIterator<Map<String,Object>> result = query("MATCH (n:" + label + ") RETURN id(n),n.name");
		while(result.hasNext()){
			long id = (long) result.next().get("id(n)");
			nodes.add(graphDb.getNodeById(id));
		}
		return nodes;
	}

	private List<String> generateRandomNames(int count){
		List<String> names = new ArrayList<String>();
		try ( Transaction tx = graphDb.beginTx() )
		{
			Map<String,Object> values = new HashMap<String,Object>();
			values.put("count", count);
			ResourceIterator<Map<String,Object>> firstNames = query("MATCH (n:Firstname) WITH n, rand() AS r ORDER BY r LIMIT " + count + " RETURN id(n),n.name");
			ResourceIterator<Map<String,Object>> lastNames = query("MATCH (n:Lastname) WITH n, rand() AS r ORDER BY r LIMIT " + count + " RETURN id(n),n.name");
			while(firstNames.hasNext()){
				names.add((String) firstNames.next().get("n.name") + " " + lastNames.next().get("n.name"));
			}
			tx.success();
		}
		return names;
	}

	public List<Node> fullQuery(String query){
		try(Transaction tx = graphDb.beginTx()){
			ResourceIterator<Map<String,Object>> iterator = query(query);
			List<Node> nodes = new ArrayList<Node>();
			while(iterator.hasNext()){
				Node node = graphDb.getNodeById((long)iterator.next().get("id(n)"));
				nodes.add(node);
				/*				Iterator<Relationship> relationships = node.getRelationships().iterator();
				while(relationships.hasNext()){
					Relationship relationship = relationships.next();
					System.out.print(node.getProperty("name"));
					System.out.print(" -> ");
					System.out.print(relationship.getType().name());
					System.out.print(" -> ");
					System.out.println(relationship.getNodes()[1].getProperty("name"));
				}*/
			}
			tx.success();
			return nodes;
		}
	}

	public ResourceIterator<Map<String,Object>> query(String query){
		ExecutionEngine engine = new ExecutionEngine(graphDb);
		ExecutionResult result = engine.execute(query);
		return result.iterator();
	}

	public void importFromFile(File file, String label, String propName) throws IOException {
		try ( Transaction tx = graphDb.beginTx() )
		{
			// Delete all nodes of the specified label
			query("MATCH (n:" + label + ")-[r]-() DELETE n,r");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			String line;
			while ((line = br.readLine()) != null) {
				if(line.length() > 3){
					graphDb.createNode(DynamicLabel.label(label)).setProperty(propName, line);				
				}
			}
			br.close();
			System.out.println("Imported " + label + "s");
			tx.success();
		}
	}



	public void shutdown(){
		graphDb.shutdown();
	}

	private void registerShutdownHook( final GraphDatabaseService graphDb )
	{
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );
	}
}
