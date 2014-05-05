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
		Grammar g = new Grammar(simple);
//		Iterable<ExprProb> exprProbs = g.parse("eng", "person which has worked with Foo");
		Iterable<ExprProb> exprProbs = g.generateAll();
		String dir = "/home/eidel/Documents/School/Exjobb/Programming/Simple/";
//		List<Question> questions = g.readQuestionsFromFile(dir + "/asts.txt", dir + "/linearizations.txt");
//		g.addQuestionsToSolr(questions);
		g.addNamesToSolr();
//		g.Test();
		List<String> asts = g.generateAbstractSyntaxTreesFromShell();
		List<List<String>> linearizations = g.generateLinearizations(asts);
		List<Question> questions = g.createQuestions(linearizations);
		g.addQuestionsToSolr(questions);
	}
}
