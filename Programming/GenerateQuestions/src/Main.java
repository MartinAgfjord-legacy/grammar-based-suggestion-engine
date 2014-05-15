import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.agfjord.Question;
import org.apache.solr.client.solrj.SolrServerException;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.ParseError;

public class Main {
	
	public static void main(String[] args) throws ParseError, IOException, SolrServerException {
		File file = new File("/tmp/Questions.pgf");
		Grammar grammar = new Grammar(file);
		Solr solr = new Solr();
		solr.deleteAllNames();
//		solr.addNamesToSolr();
//		List<String> asts = grammar.generateAbstractSyntaxTreesFromShell();
//		List<List<String>> linearizations = grammar.generateLinearizations(asts);
//		List<Question> questions = grammar.createQuestions(asts,linearizations);
//		solr.addQuestionsToSolr(questions);
		solr.addNamesToSolr("Skill", solr.fetchSkillsFromFindwise());
		solr.addNamesToSolr("Object", solr.fetchProjectNamesFromFindwise());
		solr.addNamesToSolr("Location", solr.fetchLocationsFromFindwise());
//		System.out.println("asdsa");
	}
}
