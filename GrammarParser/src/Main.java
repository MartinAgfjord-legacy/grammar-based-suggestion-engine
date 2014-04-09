import java.io.File;

import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.ParseError;

public class Main {
	
	public static void main(String[] args) throws ParseError {
		File simple = new File("Simple.pgf");
		Grammar g = new Grammar(simple);
		Iterable<ExprProb> exprProbs = g.parse("eng", "person which worked in Gothenburg");
		for(ExprProb ep : exprProbs){
			System.out.println(g.linearlize("sql", ep.getExpr()));
		}
	}
}
