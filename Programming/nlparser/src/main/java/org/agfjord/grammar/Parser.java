package org.agfjord.grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agfjord.domain.Linearlization;
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

	public Parser() {
		File pgf = new File("/tmp/Simple.pgf");
		try {
			gr = PGF.readPGF(pgf.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PGFError e) {
			e.printStackTrace();
		}
		gr.getLanguages().get("SimpleEng").addLiteral("Symb", new NercLiteralCallback());
	}

	public List<Linearlization> parse(String question, String parseLang) throws ParseError {
		Iterable<ExprProb> exprProbs;
		Result result = new Result();
		Map<String,List<Query>> astQuery = new HashMap<String,List<Query>>();
		exprProbs = gr.getLanguages().get(parseLang).parse(gr.getStartCat(), question);
		
		for(String key : gr.getLanguages().keySet()){
			Concr lang = gr.getLanguages().get(key);
			for(ExprProb exprProb : exprProbs) {
				List<Query> qs = astQuery.get(exprProb.getExpr().toString());
				if(qs == null){
					qs = new ArrayList<Query>();
					astQuery.put(exprProb.getExpr().toString(), qs);
				}
				qs.add(new Query(lang.linearize(exprProb.getExpr()), lang.getName()));	
			}
		}
		List<Linearlization> ls = new ArrayList<Linearlization>();
		for(String key : astQuery.keySet()){
			ls.add(new Linearlization(key, astQuery.get(key)));
		}
		return ls;
	}

}
