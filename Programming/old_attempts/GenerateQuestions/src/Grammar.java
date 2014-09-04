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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public List<Question> createQuestions(List<String> asts, List<List<String>> linearizations){
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
			question.setAst(asts.get(i));
			question.setNameCounts(nameCounts);
			questions.add(question);
		}
		return questions;
	}

	/*
	 * Generates abstract syntax trees from the GF-shell
	 */
	public List<String> generateAbstractSyntaxTreesFromShell() throws IOException{
		//Generate trees by using the GF-shell
		List<String> commands = new ArrayList<String>();
		commands.add("import " + prop.getProperty("abstract_grammar"));
		commands.add("gt -depth=6");
		List<String> asts = sendGfShellCommands(commands);
		for(int i=0; i < asts.size(); i++){
			if(hasDuplicateRelations(asts.get(i))){
				asts.set(i, processNameTypes(asts.get(i)));
			}else{
				// Remove from list and decrement index once
				asts.remove(i--);
			}
		}
		return asts;
	}
	/*
	 * Modifies an abstract syntax tree. All names are by default named "Foo" by the gf-shell.
	 * We change each name into its type + index.
	 * E.g. Question_I Person_N (Know_R (MkSkill (MkSymb "Foo"))) ==>
	 *      Question_I Person_N (Know_R (MkSkill (MkSymb "Skill0")))
	 */
	private String processNameTypes(String ast){
		for(int j=0; j < nameCats.length; j++){
			Scanner sc = new Scanner(ast);
			sc.useDelimiter((nameFuns[j]  + " \\(MkSymb \"Foo\"\\)"));
			StringBuilder sb = new StringBuilder();
			int id = 0;
			while(sc.hasNext()){
				sb.append(sc.next());
				if(sc.hasNext()){
					sb.append(nameFuns[j] + " (MkSymb \"" + nameCats[j] + id++ + "\")");
				}
			}
			ast = sb.toString();
		}
		return ast;
	}
	
	/*
	 * Determines if an abstract syntax tree has the same relation more than once
	 * E.g. Question_I Person_N (And_I (Know_R (MkSkill (MkSymb "Skill0"))) (Know_R (MkSkill (MkSymb "Skill1"))))
	 * contains Know_R two times. 
	 */
	private boolean hasDuplicateRelations(String ast){
		Set<String> relations = new HashSet<String>();
        Pattern regex = Pattern.compile("\\w+\\_R ");
        Matcher m = regex.matcher(ast);
        while(m.find()){
        	if(relations.contains(m.group())){
        		return false;
        	}else {
        		relations.add(m.group());
        	}
        } return true;
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
