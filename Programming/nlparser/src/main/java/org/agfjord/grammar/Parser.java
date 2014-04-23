package org.agfjord.grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.agfjord.domain.AbstractSyntaxTree;
import org.agfjord.domain.CompletionWord;
import org.agfjord.domain.Query;
import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.NercLiteralCallback;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.PGFError;
import org.grammaticalframework.pgf.ParseError;
import org.grammaticalframework.pgf.TokenProb;

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

	public List<AbstractSyntaxTree> parse(String question, String parseLang) throws ParseError {
		Iterable<ExprProb> exprProbs;
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
		List<AbstractSyntaxTree> asts = new ArrayList<AbstractSyntaxTree>();
		for(String key : astQuery.keySet()){
			asts.add(new AbstractSyntaxTree(key, astQuery.get(key)));
		}
		return asts;
	}
	
	public Set<CompletionWord> completeQuery(String question, String parseLang) throws ParseError {
		Concr lang = gr.getLanguages().get(parseLang);
			String toComplete = "";
			String[] words = question.split("\\s+");
			//If last char is not whitespace
			if(question.length() > 0 && question.charAt(question.length()-1) != ' '){ 
				//Split words on whitespace
				StringBuilder partialQuestion = new StringBuilder();
				for(int i=0; i < words.length-1; i++){
					partialQuestion.append(words[i] + " ");
				}
				question = partialQuestion.toString();
				toComplete = words[words.length-1];
			}
			
			Set<CompletionWord> tokens = new HashSet<CompletionWord>();
			for (TokenProb tp : lang.complete(gr.getStartCat(), question, toComplete)) {
				boolean partial = false;
				if(!toComplete.equals("")){
					partial = tp.getToken().contains(toComplete);
					//Omit to suggest the word to complete
					if(tp.getToken().equals(toComplete)){
						continue ;
					}
				}
				tokens.add(new CompletionWord(partial, tp.getToken()));
			}
			//If list only contains the last word or is empty
			if(tokens.size() <= 1){
				//Also predict next word
				for (TokenProb tp : lang.complete(gr.getStartCat(), question + toComplete + " ", "")) {
					tokens.add(new CompletionWord(false, tp.getToken()));
				}
			}
			return tokens;
	}

}
