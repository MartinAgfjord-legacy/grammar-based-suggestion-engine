package org.agfjord.graph;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.ParseError;

public class Main {
	
	static File file = new File("/tmp/Questions.pgf");
	static Grammar grammar = new Grammar(file);
	static Solr solr = new Solr();
	static DataImportNeo4j dataImport = new DataImportNeo4j();
	
	public static void main(String[] args) throws ParseError, IOException, SolrServerException {
		importDocumentsNeo4j();
		importNamesSolr();
		importQuestionsSolr();
	}
	
	public static void importDocumentsNeo4j() throws IOException{
		dataImport.importFromFile(new File("First_names.txt"), "Firstname", "name");
		dataImport.importFromFile(new File("Last_names.txt"), "Lastname", "name");
		dataImport.importFromFile(new File("Programming_languages.txt"), "Expertise", "name");
		dataImport.importFromFile(new File("Non-profit_organizations.txt"), "Project", "name");
		dataImport.importFromFile(new File("Locations.txt"), "Location", "name");
		dataImport.createRelations(100);
	}
	
	public static void importNamesSolr() throws SolrServerException, IOException{
//		solr.addNamesToSolr("Skill", solr.fetchSkillsFromFindwise());
//		solr.addNamesToSolr("Object", solr.fetchProjectNamesFromFindwise());
//		solr.addNamesToSolr("Location", solr.fetchLocationsFromFindwise());
		solr.deleteAllNames();
		solr.addNodesToSolr("Skill", dataImport.fetchAllLabelWithRelation("Expertise", "KNOWS"));
		solr.addNodesToSolr("Location", dataImport.fetchAllLabelWithRelation("Location", "WORK_IN"));
		solr.addNodesToSolr("Object", dataImport.fetchAllLabelWithRelation("Project", "MEMBER_OF"));
	}
	
	public static void importQuestionsSolr() throws IOException, SolrServerException, ParseError{
		List<String> asts = grammar.generateAbstractSyntaxTreesFromShell();
		List<List<String>> linearizations = grammar.generateLinearizations(asts);
		List<Question> questions = grammar.createQuestions(asts,linearizations);
		solr.addQuestionsToSolr(questions);
	}
}
