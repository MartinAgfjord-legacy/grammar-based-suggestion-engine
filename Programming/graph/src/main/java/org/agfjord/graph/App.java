package org.agfjord.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.SolrServerException;
import org.neo4j.cypher.internal.compiler.v2_0.functions.Relationships;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

/**
 * Hello world!
 *
 */
public class App 
{
	private static enum RelTypes implements RelationshipType
	{
		KNOWS, WORK_IN, MEMBER_OF, USES
	};
	private GraphDatabaseService graphDb;
	private GraphDatabaseService namesDb;
	//	private RestCypherQueryEngine cypher;
	//	private RestAPI restAPI;
	private Node firstNode;
	private Node secondNode;
	private Relationship relationship;

	public App(){
		//		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/neo4j");
//		File namesDbDir = new File("data/names");
//		File graphDbDir = new File("data");
		//		if(namesDbDir.exists() && namesDbDir.list().length != 0){
		//			namesDb = new GraphDatabaseFactory().newEmbeddedDatabase("data/names");
		//		} else {
		//			namesDb = createNewDatabase("data/names");
		//		}
		//		if(graphDbDir.exists() && graphDbDir.list().length != 0){
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("data/");
		registerShutdownHook(graphDb);
		//		} else {
		//			graphDb = createNewDatabase("data/graph.db");
		//		}
		//		graphDb = new RestGraphDatabase("http://localhost:7474/db/data");
		//		restAPI = new RestAPIFacade("http://localhost:7474/db/data");
		//		cypher = new RestCypherQueryEngine(restAPI);
		//		registerShutdownHook(namesDb);
		//		registerShutdownHook(graphDb);
	}

	//	public void index(){
	//		IndexDefinition indexDefinition;
	//		try (Transaction tx = graphDb.beginTx())
	//		{
	//			Schema schema = graphDb.schema();
	//			indexDefinition = schema.indexFor(DynamicLabel.label("Expertise"))
	//					.on( "name" )
	//					.create();
	//			tx.success();
	//		}
	//		try ( Transaction tx = graphDb.beginTx() )
	//		{
	//			Schema schema = graphDb.schema();
	//			schema.awaitIndexOnline( indexDefinition, 10, TimeUnit.SECONDS );
	//		}
	//	}

	public void createRelations(int count){
		try(Transaction tx = graphDb.beginTx()){
			
			// Delete all person nodes and their relationships
			query("MATCH (n:Person)-[r]-() DELETE n,r");
			// Generate random names from first names and last names
			int counter = 0;
			for(String name : randomNames(count)){
				// Create a person node
				Node person = graphDb.createNode(DynamicLabel.label("Person"));
				person.setProperty("name", name);
				
				// Create relationships with 5 random expertises
				for(Node expertise : fetchRandom("Expertise", 5)){
					person.createRelationshipTo(expertise, RelTypes.KNOWS);
				}
				
				// Create relationship with 1 or 2 random locations
				int locationCount = Math.random() >= 0.5 ? 1 : 2;
				for(Node expertise : fetchRandom("Location", locationCount)){
					person.createRelationshipTo(expertise, RelTypes.WORK_IN);
				}
				
				// Create relationships with 10 random expertises
				for(Node expertise : fetchRandom("Project", 10)){
					person.createRelationshipTo(expertise, RelTypes.MEMBER_OF);
				}
				counter++;
				if(counter % 100 == 0){
					System.out.println("Imported " + (int) ((double) counter / count * 100) + "% of relations");
				}
			}
			System.out.println("Imported relations");
			tx.success();
		}
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

	private List<String> randomNames(int count){
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

	public void find(){
		try(Transaction tx = graphDb.beginTx()){
		ResourceIterator<Map<String,Object>> iterator = query("MATCH (n:Person) RETURN id(n)");

			while(iterator.hasNext()){
				Node node = graphDb.getNodeById((long)iterator.next().get("id(n)"));
				Iterator<Relationship> relationships = node.getRelationships().iterator();
				while(relationships.hasNext()){
					Relationship relationship = relationships.next();
					System.out.print(node.getProperty("name"));
					System.out.print(" -> ");
					System.out.print(relationship.getType().name());
					System.out.print(" -> ");
					System.out.println(relationship.getNodes()[1].getProperty("name"));
				}
				//			tx.success();
			}
			tx.success();
		}
		//		try(Transaction tx = graphDb.beginTx()){
		//			Node node = graphDb.getNodeById(328871);
		//			for(Relationship relationship : node.getRelationships()){
		//				System.out.println(relationship.getType());
		//			}
		//			ExecutionEngine engine = new ExecutionEngine(graphDb);
		//			engine.execute("", new HashMap<String,Object>())
}

public ResourceIterator<Map<String,Object>> query(String query){
	ExecutionEngine engine = new ExecutionEngine(graphDb);
	ExecutionResult result;
	return engine.execute(query).iterator();
}

//	public Object firstObject(ExecutionResult result, String title){
//		Map<String,Object> map = result.iterator().next();
////		return result.iterator().next().get(title);
//		return map.get(title);
//	}

//	public long count(GraphDatabaseService db, String label){
//		ExecutionResult result = query(db, "MATCH (n:" + label + ") RETURN count(n);");
//		return (long) firstObject(result, "count(n)");
//	}

public void importSolrNames() throws SolrServerException{
	DataImport dataImport = new DataImport();
	try ( Transaction tx = graphDb.beginTx() )
	{
		for (String expertise : dataImport.fetchName("Skill"))
		{
			//			Map<String,Object> values = new HashMap<String,Object>();
			//				values.put("label", "Expertise");
			//			values.put("name", expertise);
			//			cypher.query("CREATE (n:Expertise { name : {name} })", values);
			Node userNode = graphDb.createNode(DynamicLabel.label("Expertise"));
			userNode.setProperty("name", expertise);
		}
		System.out.println("Imported expertises");
		for (String location : dataImport.fetchName("Location"))
		{
			//			Map<String,Object> values = new HashMap<String,Object>();
			//				values.put("label", "Location");
			//			values.put("name", location);
			//			cypher.query("CREATE (n:Location { name : {name} })", values);
			//							query("CREATE (n:" + "Location" + " { name : '" + location + "' })");
			Node userNode = graphDb.createNode(DynamicLabel.label("Location"));
			userNode.setProperty("name", location);
		}
		System.out.println("Imported locations");
		for (String object : dataImport.fetchName("Object"))
		{
			//			Map<String,Object> values = new HashMap<String,Object>();
			//				values.put("label", "Object");
			//			values.put("name", object);
			//			cypher.query("CREATE (n:Object { name : {name} })", values);
			//				query(graphDb, "CREATE (n:" + "Location" + " { name : '" + object+ "' })");
			Node userNode = graphDb.createNode(DynamicLabel.label("Project"));
			userNode.setProperty("name", object);
		}
		System.out.println("Imported objects");
		tx.success();

	}
}

//	public RestNode createNode(String label, String params){
//		QueryResult<Map<String,Object>> result = cypher.query("CREATE (n:" + label + " " + params + ") RETURN id(n)", null);
//		int id = (int) result.iterator().next().get("id(n)");
//		return restAPI.getNodeById(id);
//	}

public void importFromFile(File file, String label, String propName) throws IOException{
	try ( Transaction tx = graphDb.beginTx() )
	{
		// Delete all nodes of the specified label
		query("MATCH (n:" + label + ")-[r]-() DELETE n,r");
//		FileInputStream fis = new FileInputStream("CSV_Database_of_First_Names.csv");
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
		String line;
		while ((line = br.readLine()) != null) {
			//			RestNode firstName = createNode("Firstname", "{ name : '" + line + "'}");
//			graphDb.createNode(DynamicLabel.label("Firstname")).setProperty("name", line);
			graphDb.createNode(DynamicLabel.label(label)).setProperty(propName, line);
		}
		System.out.println("Imported " + label + "s");
//		fis = new FileInputStream("CSV_Database_of_Last_Names.csv");
//		br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
//		while ((line = br.readLine()) != null) {
//			graphDb.createNode(DynamicLabel.label("Lastname")).setProperty("name", line);
//		}
//		System.out.println("Imported lastnames");
		tx.success();
	}
}

public static void main( String[] args ) throws SolrServerException, IOException
{
	App app = new App();
//	app.importNames();
//	app.importSolrNames();
	app.importFromFile(new File("First_names.txt"), "Firstname", "name");
	app.importFromFile(new File("Last_names.txt"), "Lastname", "name");
	app.importFromFile(new File("Programming_languages.txt"), "Expertise", "name");
	app.importFromFile(new File("Non-profit_organizations.txt"), "Project", "name");
	app.importFromFile(new File("Locations.txt"), "Location", "name");
	app.createRelations(1000);
	app.find();
	//		app.findUser();
}

private GraphDatabaseService createNewDatabase(String path){
	GraphDatabaseService db = new GraphDatabaseFactory()
	.newEmbeddedDatabaseBuilder(path)
	.setConfig( GraphDatabaseSettings.nodestore_mapped_memory_size, "10M" )
	.setConfig( GraphDatabaseSettings.string_block_size, "60" )
	.setConfig( GraphDatabaseSettings.array_block_size, "300" )
	.newGraphDatabase();
	return db;
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
