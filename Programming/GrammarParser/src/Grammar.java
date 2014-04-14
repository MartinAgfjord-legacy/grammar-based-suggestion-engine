import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.NercLiteralCallback;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.PGFError;
import org.grammaticalframework.pgf.ParseError;


public class Grammar {

	private PGF gr;	
	private Map<String, Concr> concreteLangs;
	
	public Grammar(File pgf) {
		try {
			gr = PGF.readPGF(pgf.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PGFError e) {
			e.printStackTrace();
		}
		concreteLangs = new HashMap<String, Concr>();
		concreteLangs.put("eng", gr.getLanguages().get("SimpleEng"));
		concreteLangs.put("solr", gr.getLanguages().get("SimpleSolr"));
//		System.out.println(concreteLangs.get("eng").);
		concreteLangs.get("eng").addLiteral("Symb", new NercLiteralCallback());
	}
	
	public Iterable<ExprProb> parse(String lang, String question) throws ParseError {

		Concr fromLang = concreteLangs.get(lang);
		return fromLang.parse(gr.getStartCat(), question);
	}
	
	public String linearlize(String lang, Expr expr) {
		Concr toLang = concreteLangs.get(lang);
		return toLang.linearize(expr);
	}
}
