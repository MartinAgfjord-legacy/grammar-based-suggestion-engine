package org.agfjord.graph;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.ParseError;

public class Main {
	
	static File file = new File("/home/eidel/Documents/School/Exjobb/Programming/bundle/nlparser/src/main/resources/Instrucs.pgf");
	static Grammar grammar = new Grammar(file);
	static DataImportSolr solr = new DataImportSolr();
	static DataImportNeo4j dataImport = new DataImportNeo4j();
	
	public static void main(String[] args) throws ParseError, IOException, SolrServerException {
		// Generate random objects and then create persons who uses these values
//		importDocumentsNeo4j();
		// Add all names that have a relation to solr
//		importNamesSolr();
		// Generate instructions (suggestions) by using
		// the GF-shell and add them to solr
		importInstrucsSolr();
		
//		dataImport.shutdown();
		// Add all persons with its relations to solr
//		solr.importRelationsFromNeo4j();
	}
	
	public static void importDocumentsNeo4j() throws IOException{
		dataImport.deleteDatabase();
		dataImport.importFromFile(new File("First_names.txt"), "Firstname", "name");
		dataImport.importFromFile(new File("Last_names.txt"), "Lastname", "name");
		dataImport.importFromFile(new File("Programming_languages.txt"), "Expertise", "name");
		dataImport.importFromFile(new File("Modules.txt"), "Module", "name");
		dataImport.importFromFile(new File("Locations.txt"), "Location", "name");
		dataImport.importFromFile(new File("Charity_organizations.txt"), "Organization", "name");
		
		dataImport.createPersons(100);
		dataImport.createPersonRelations();
		dataImport.createOrganizationRelations();
	}
	
	public static void importNamesSolr() throws SolrServerException, IOException{
		solr.deleteAllNames();
		solr.importNames("Skill", dataImport.fetchAllLabelWithRelation("Expertise"));
		solr.importNames("Location", dataImport.fetchAllLabelWithRelation("Location"));
		solr.importNames("Organization", dataImport.fetchAllLabelWithRelation("Organization"));
		solr.importNames("Module", dataImport.fetchAllLabelWithRelation("Module"));
	}
	
	public static void importInstrucsSolr() throws IOException, SolrServerException, ParseError{
		Set<String> asts = grammar.generateAbstractSyntaxTreesFromShell();
		System.out.println("Successfully generated abstract syntax trees");
		solr.deleteAllInstrucs();
		{
		List<Set<String>> linearizations = grammar.generateLinearizations(asts, "InstrucsEng.gf", "InstrucsEng");
		System.out.println("Successfully generated English linearizations");
		List<Instruction> Instrucs = grammar.createInstrucs(asts, linearizations, "InstrucsEng");
		System.out.println("Successfully instantiated English linearizations");
		solr.addInstrucsToSolr(Instrucs);
		System.out.println("Successfully added English linearizations to solr");
		
		}
		{
		List<Set<String>> linearizations = grammar.generateLinearizations(asts, "InstrucsSwe.gf", "InstrucsSwe");
		System.out.println("Successfully generated Swedish linearizations");
		List<Instruction> Instrucs = grammar.createInstrucs(asts, linearizations, "InstrucsSwe");
		System.out.println("Successfully instantiated Swedish linearizations");
		solr.addInstrucsToSolr(Instrucs);
		System.out.println("Successfully added Swedish linearizations to solr");
		}
	}
}
