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
	static DataImportSolr solr = new DataImportSolr();
	static DataImportNeo4j dataImport = new DataImportNeo4j();
	
	public static void main(String[] args) throws ParseError, IOException, SolrServerException {
//		importDocumentsNeo4j();
//		importNamesSolr();
		importQuestionsSolr();
//		dataImport.shutdown();
//		solr.importRelationsFromNeo4j();
	}
	
	public static void importDocumentsNeo4j() throws IOException{
		dataImport.deleteDatabase();
//		dataImport.importFromFile(new File("First_names.txt"), "Firstname", "name");
//		dataImport.importFromFile(new File("Last_names.txt"), "Lastname", "name");
//		dataImport.importFromFile(new File("Programming_languages.txt"), "Expertise", "name");
//		dataImport.importFromFile(new File("Modules.txt"), "Module", "name");
//		dataImport.importFromFile(new File("Locations.txt"), "Location", "name");
//		dataImport.importFromFile(new File("Charity_organizations.txt"), "Organization", "name");
		
		
		dataImport.createPersons(100);
		dataImport.createPersonRelations();
		dataImport.createOrganizationRelations();
	}
	
	public static void importNamesSolr() throws SolrServerException, IOException{
//		solr.addNamesToSolr("Skill", solr.fetchSkillsFromFindwise());
//		solr.addNamesToSolr("Object", solr.fetchProjectNamesFromFindwise());
//		solr.addNamesToSolr("Location", solr.fetchLocationsFromFindwise());
		solr.deleteAllNames();
		solr.importNames("Skill", dataImport.fetchAllLabelWithRelation("Expertise"));
		solr.importNames("Location", dataImport.fetchAllLabelWithRelation("Location"));
		solr.importNames("Organization", dataImport.fetchAllLabelWithRelation("Organization"));
		solr.importNames("Module", dataImport.fetchAllLabelWithRelation("Module"));
	}
	
	public static void importQuestionsSolr() throws IOException, SolrServerException, ParseError{
		List<String> asts = grammar.generateAbstractSyntaxTreesFromShell();
		solr.deleteAllQuestions();
		{
		List<List<String>> linearizations = grammar.generateLinearizations(asts, "QuestionsEng.gf");
		List<Question> questions = grammar.createQuestions(asts, linearizations, "QuestionsEng");
		solr.addQuestionsToSolr(questions);
		}
		{
		List<List<String>> linearizations = grammar.generateLinearizations(asts, "QuestionsSwe.gf");
		List<Question> questions = grammar.createQuestions(asts, linearizations, "QuestionsSwe");
		solr.addQuestionsToSolr(questions);
		}
	}
}
