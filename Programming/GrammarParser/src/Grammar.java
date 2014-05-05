import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.agfjord.Question;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.grammaticalframework.pgf.Concr;
import org.grammaticalframework.pgf.Expr;
import org.grammaticalframework.pgf.ExprProb;
import org.grammaticalframework.pgf.NercLiteralCallback;
import org.grammaticalframework.pgf.PGF;
import org.grammaticalframework.pgf.PGFError;
import org.grammaticalframework.pgf.ParseError;


public class Grammar {

	public PGF gr;	
	private Map<String, Concr> concreteLangs;
	private SolrServer treesServer = new HttpSolrServer("http://localhost:8983/solr/trees");
	private SolrServer namesServer = new HttpSolrServer("http://localhost:8983/solr/names");
	private String[] nameFuns = new String[]{ "MkSkill", "MkLocation", "MkObject" };
	private String[] nameCats = new String[]{ "Skill", "Location", "Object" };
	

	public Grammar(File pgf) {
		try {
			gr = PGF.readPGF(pgf.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PGFError e) {
			e.printStackTrace();
		}
		//		concreteLangs = new HashMap<String, Concr>();
		//		concreteLangs.put("eng", gr.getLanguages().get("SimpleEng"));
		//		concreteLangs.put("solr", gr.getLanguages().get("SimpleSolr"));
		//		System.out.println(concreteLangs.get("eng").);
		//		concreteLangs.get("eng").addLiteral("Symb", new NercLiteralCallback());
	}

	//	public Iterable<ExprProb> parse(String lang, String question) throws ParseError {
	//
	//		Concr fromLang = concreteLangs.get(lang);
	//		return fromLang.parse(gr.getStartCat(), question);
	//	}
	//	
	//	public String linearlize(String lang, Expr expr) {
	//		Concr toLang = concreteLangs.get(lang);
	//		return toLang.linearize(expr);
	//	}

	public List<String> replaceAll(List<String> list, CharSequence target, CharSequence replacement){
		for(int i=0; i < list.size(); i++){
			list.set(i, list.get(i).replace(target, replacement));
		}
		return list;
	}
	public void addQuestionsToSolr(List<Question> questions) throws SolrServerException, IOException, ParseError{
		treesServer.deleteByQuery("*:*");
		int id = 0;
//		for(Question question : questions){
//			question.setId(id++);
//			treesServer.addBean(question);
//		}
		for(Question question : questions){
			SolrInputDocument solrInputDoc = new SolrInputDocument();
			solrInputDoc.addField("id", id++);
			solrInputDoc.addField("ast", question.getAst());
			solrInputDoc.addField("linearizations", question.getLinearizations());
			Map<String,Integer> nameCounts = question.getNameCounts();
			for(String name : nameCounts.keySet()){
				solrInputDoc.addField(name + "_i", nameCounts.get(name));
			}
			treesServer.add(solrInputDoc);
		}
		treesServer.commit();
	}

	public List<Question> createQuestions(List<List<String>> linearizations){
		List<Question> questions = new ArrayList<Question>();
		for(int i=0; i < linearizations.size(); i++){
			Question question = new Question();
			question.setLinearizations(linearizations.get(i));
			String linearization = linearizations.get(i).get(0);
			Map<String,Integer> nameCounts = new HashMap<String,Integer>();
			for(String nameCat : nameCats){
				int count = 0;
				while(linearization.contains((nameCat + count))){
					count++;
				}
				nameCounts.put(nameCat, count);
			}
			question.setNameCounts(nameCounts);
			questions.add(question);
		}
		return questions;
	}
	
	public void addNamesToSolr() throws SolrServerException, IOException{
		int id = 0;
		namesServer.deleteByQuery("*:*");
		namesServer.add(createNameDocument("Skill", "Java", id++));
		namesServer.add(createNameDocument("Skill", "C", id++));
		namesServer.add(createNameDocument("Object", "Solr", id++));
		namesServer.add(createNameDocument("Location", "Gothenburg", id++));
		namesServer.add(createNameDocument("Location", "Stockholm", id++));
		namesServer.add(createNameDocument("Location", "Malmo", id++));
		namesServer.commit();
	}
	
	private SolrInputDocument createNameDocument(String type, String name, int id){
		Map<String,String> fieldsAndValues = new HashMap<String,String>();
		fieldsAndValues.put("type", type);
		fieldsAndValues.put("name", name);
		return createSolrDocument(fieldsAndValues, id++);
	}


	private SolrInputDocument createSolrDocument(Map<String,String> fieldsAndValues, int id){
		SolrInputDocument solrInputDoc = new SolrInputDocument();
		solrInputDoc.addField("id", id);
		for(String key : fieldsAndValues.keySet()){
			solrInputDoc.addField(key, fieldsAndValues.get(key));
		}
		return solrInputDoc;
	}

	public Iterable<ExprProb> generateAll(){
		return gr.generateAll(gr.getStartCat());
	}

	public List<String> generateAbstractSyntaxTreesFromShell() throws IOException{
		//Generate trees by using the GF-shell
		List<String> commands = new ArrayList<String>();
		commands.add("import SimpleGen.gf");
		commands.add("gt -depth=5");
		List<String> asts = sendGfShellCommands(commands);
		for(int i=0; i < asts.size(); i++){
			for(int j=0; j < nameCats.length; j++){
				Scanner sc = new Scanner(asts.get(i));
				sc.useDelimiter(nameFuns[j] + " \"Foo\"");
				StringBuilder sb = new StringBuilder();
				int id = 0;
				while(sc.hasNext()){
					sb.append(sc.next());
					if(sc.hasNext()){
						sb.append(nameFuns[j] + " (MkSymb \"" + nameCats[j] + id++ + "\")");
					}
					
				}

				asts.set(i, sb.toString());
			}
		}

		return asts;
	}
	
	public List<List<String>> generateLinearizations(List<String> asts) throws IOException{
		List<String> commands = new ArrayList<String>();
		commands.add("import SimpleEng.gf");
		for(String ast : asts){
			commands.add("linearize -list " + ast);
		}
		List<String> lines = sendGfShellCommands(commands);
		List<List<String>> result = new ArrayList<List<String>>();
		for(String line : lines) {
			List<String> linearizations = new ArrayList<String>();
			Scanner sc = new Scanner(line);
			sc.useDelimiter(", ");
			while(sc.hasNext()){
				String linearization = sc.next();
				if(!linearization.isEmpty()){
					linearizations.add(linearization);
				}
			}
			result.add(linearizations);
		}
		return result;
	}
	
	public List<String> sendGfShellCommands(List<String> commands) throws IOException{
		ProcessBuilder pb = new ProcessBuilder("gf", "--run");
		Map<String, String> env = pb.environment();
		pb.directory(new File("/home/eidel/Documents/School/Exjobb/Programming/Simple"));
		Process p = pb.start();
		OutputStreamWriter osw = new OutputStreamWriter(p.getOutputStream());
		for(String command : commands){
			osw.write(command + "\n");
		}
		osw.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		List<String> result = new ArrayList<String>();
		while ((line = in.readLine()) != null) {
			if(!line.isEmpty()){
				result.add(line);
			}
		}
		return result;
	}

	
	public void Test(){
		Expr expr = new Expr("SentenceDot_S (ExplicitSgPres_Q (Know_R Person_N (MkSkill (MkSymb \"Java\"))))");
		String linearization = gr.getLanguages().get("SimpleEng").linearize(expr);
		System.out.println(expr);
	}
}
