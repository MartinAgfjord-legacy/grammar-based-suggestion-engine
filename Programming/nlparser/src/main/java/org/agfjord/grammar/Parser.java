package org.agfjord.grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agfjord.domain.Query;
import org.agfjord.domain.Result;
import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.NercLiteralCallback;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.PGFError;
import org.grammaticalframework.pgf.ParseError;

public class Parser {
	
	private PGF gr;	
	private Map<String, Concr> concreteLangs;
	
	public Parser() {
		File pgf = new File("/tmp/Simple.pgf");
		try {
			gr = PGF.readPGF(pgf.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PGFError e) {
			e.printStackTrace();
		}
		concreteLangs = new HashMap<String, Concr>();
		concreteLangs.put("eng", gr.getLanguages().get("SimpleEng"));
		concreteLangs.get("eng").addLiteral("Symb", new NercLiteralCallback());
		concreteLangs.put("solr", gr.getLanguages().get("SimpleSolr"));
	}

	public Result parse(String question, String from, String to) throws ParseError{
		Concr fromLang = concreteLangs.get(from);
		Concr toLang = concreteLangs.get(to);
		Iterable<ExprProb> exprProbs = fromLang.parse(gr.getStartCat(), question);
		Result result = new Result();
		result.setQuery(new Query(question, from));
		List<Query> qs = new ArrayList<Query>();
		for(ExprProb exprProb : exprProbs) {
			qs.add(new Query(toLang.linearize(exprProb.getExpr()), to));
		}
		result.setResult(qs);
		
		return result;
	}
	
}
