package org.agfjord.graph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	final private String[] nameFuns = new String[]{ "MkSkill", "MkLocation", "MkOrganization", "MkModule" };
	final private String[] nameCats = new String[]{ "Skill", "Location", "Organization", "Module" };
	final private Properties prop = new Properties();

	public Grammar(InputStream pgf) {
		try {
			gr = PGF.readPGF(pgf);
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
	 * Create Instrucs from generated linearizations
	 */
	public List<Instruction> createInstrucs(Set<String> asts, List<Set<String>> linearizations, String lang){
		List<Instruction> Instrucs = new ArrayList<Instruction>();
		Iterator<String> it = asts.iterator();
		for(int i=0; i < linearizations.size(); i++){
			Instruction question = new Instruction();
			question.setLinearizations(linearizations.get(i).toArray(new String[linearizations.get(i).size()]));
			String linearization = linearizations.get(i).iterator().next();
			Map<String,Integer> nameCounts = new HashMap<String,Integer>();
			for(String nameCat : nameCats){
				int count = 0;
				while(linearization.contains((nameCat + count))){
					count++;
				}
				nameCounts.put(nameCat, count);
			}
			question.setAst(it.next());
			question.setNameCounts(nameCounts);
			question.setLang(lang);
			Instrucs.add(question);
		}
		return Instrucs;
	}

	/*
	 * Generates abstract syntax trees from the GF-shell
	 */
	public Set<String> generateAbstractSyntaxTreesFromShell() throws IOException{
		//Generate trees by using the GF-shell
		List<String> commands = new ArrayList<String>();
		commands.add("import " + prop.getProperty("abstract_grammar"));
		commands.add("gt -depth=6");
		Set<String> asts = sendGfShellCommands(commands);
		Set<String> result = new LinkedHashSet<String>();
		Iterator<String> it = asts.iterator();
		while(it.hasNext()){
			String ast = it.next();
			if(!hasDuplicateRelations(ast)){
				result.add(processNameTypes(ast));
			}
		}
		return result;
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
			sc.close();
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
        		return true;
        	}else {
        		relations.add(m.group());
        	}
        } return false;
	}
	
	/*
	 * Generate linearizations from abstract syntax trees by using the GF-shell.
	 * Each ast can yield more than one linearization, so we have a list of 
	 * linearizations for each ast.
	 * 
	 * We do the linearization multiple intervals, GF wont be able to process
	 * all data otherwise.
	 */
	public List<Set<String>> generateLinearizations(Set<String> asts, String gfFile, String concreteSyntax) throws IOException{
		List<String> commands = new ArrayList<String>();
		for(String ast : asts){
			commands.add("linearize -lang=" + concreteSyntax + " -list " + ast);
		}
		int increment = 100;
		int from = 0, to = increment < asts.size() ? increment : asts.size();
		List<Set<String>> result = new ArrayList<Set<String>>();
		while(from < asts.size()){
			List<String> subCommands = commands.subList(from, to);
			subCommands.add(0, "import " + gfFile);
			Set<String> lines = sendGfShellCommands(subCommands);
			for(String line : lines) {
				Set<String> linearizations = new LinkedHashSet<String>();
				Scanner sc = new Scanner(line);
				sc.useDelimiter(", ");
				while(sc.hasNext()){
					String linearization = sc.next();
					if(!linearization.isEmpty()){
						linearizations.add(linearization);
					}
				}
				sc.close();
				result.add(linearizations);
			}
			System.out.println("Added " + from + " to " + to); 
			to = to + increment < asts.size() ? to + increment : asts.size();
			from += increment ;

		}
		return result;
	}

	/*
	 * Method to interact with the gf-shell
	 */
	public Set<String> sendGfShellCommands(List<String> commands) throws IOException{
		// Run 'gf --run' in the correct directory
		ProcessBuilder pb = new ProcessBuilder("gf", "--run");
		Map<String, String> env = pb.environment();
		pb.directory(new File(prop.get("grammar_dir").toString()));
		Process p = pb.start();
		
		// Send input to gf
		OutputStreamWriter osw = new OutputStreamWriter(p.getOutputStream());
		int id= 0;
		for(String command : commands){
			osw.write(command + "\n");
		}
		osw.close();
		// Read output from gf
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		Set<String> result = new LinkedHashSet<String>();
		while ((line = br.readLine()) != null) {
			if(!line.isEmpty()){
				result.add(line);
			}
		}
		br.close();
		
		return result;
	}
}