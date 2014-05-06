import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

	private PGF gr;
	final private String[] nameFuns = new String[]{ "MkSkill", "MkLocation", "MkObject" };
	final private String[] nameCats = new String[]{ "Skill", "Location", "Object" };
	final private Properties prop = new Properties();

	public Grammar(File pgf) {
		try {
			gr = PGF.readPGF(pgf.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PGFError e) {
			e.printStackTrace();
		}
		try {
			prop.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Create questions from generated linearizations
	 */
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

	/*
	 * Generates abstract syntax trees from the GF-shell
	 * Each generated name gets the value "Foo", we change 
	 * this into the correct type + index.
	 * E.g. ImplicitPlPres_Q (Know_R Person_N (MkSkill (MkSymb "Foo"))) ==>
	 *      ImplicitPlPres_Q (Know_R Person_N (MkSkill (MkSymb "Skill0")))
	 */
	public List<String> generateAbstractSyntaxTreesFromShell() throws IOException{
		//Generate trees by using the GF-shell
		List<String> commands = new ArrayList<String>();
		commands.add("import Simple.gf");
		commands.add("gt -depth=5");
		List<String> asts = sendGfShellCommands(commands);
		for(int i=0; i < asts.size(); i++){
			for(int j=0; j < nameCats.length; j++){
				Scanner sc = new Scanner(asts.get(i));
				sc.useDelimiter((nameFuns[j]  + " \\(MkSymb \"Foo\"\\)"));
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
			System.out.println(asts.get(i));
		}
		return asts;
	}
	/*
	 * Generate linearizations from abstract syntax trees by using the GF-shell.
	 * Each ast can yield more than one linearization, so we have a list of 
	 * linearizations for each ast.
	 */
	public List<List<String>> generateLinearizations(List<String> asts) throws IOException{
		List<String> commands = new ArrayList<String>();
		commands.add("import " + prop.getProperty("linearize_grammar").toString());
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

	/*
	 * Method to interact with the gf-shell
	 */
	public List<String> sendGfShellCommands(List<String> commands) throws IOException{
		// Run 'gf --run' in the correct directory
		ProcessBuilder pb = new ProcessBuilder("gf", "--run");
		Map<String, String> env = pb.environment();
		pb.directory(new File(prop.get("grammar_dir").toString()));
		Process p = pb.start();
		
		// Send input to gf
		OutputStreamWriter osw = new OutputStreamWriter(p.getOutputStream());
		for(String command : commands){
			osw.write(command + "\n");
		}
		osw.close();
		
		// Read output from gf
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		List<String> result = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if(!line.isEmpty()){
				result.add(line);
			}
		}
		br.close();
		
		return result;
	}
}
