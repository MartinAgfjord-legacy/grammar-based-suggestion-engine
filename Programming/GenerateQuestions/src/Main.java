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
		File simple = new File("/tmp/Simple.pgf");
		Grammar grammar = new Grammar(simple);
		Solr solr = new Solr();
		solr.deleteAll();
//		solr.addNamesToSolr();
//		List<String> asts = grammar.generateAbstractSyntaxTreesFromShell();
//		List<List<String>> linearizations = grammar.generateLinearizations(asts);
//		List<Question> questions = grammar.createQuestions(linearizations);
//		solr.addQuestionsToSolr(questions);
//		solr.addNamesToSolr("Skill", solr.fetchSkillsFromFindwise());
//		solr.addNamesToSolr("Object", solr.fetchProjectNamesFromFindwise());
		solr.addNamesToSolr("Location", solr.fetchLocationsFromFindwise());
//		System.out.println("asdsa");
	}
}
