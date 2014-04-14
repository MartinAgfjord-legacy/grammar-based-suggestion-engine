package org.grammaticalframework.pgf;

import java.io.*;
import java.util.*;

import org.agfjord.ClassScope;

public class PGF {
	public static native PGF readPGF(String path) throws FileNotFoundException;
	
	public static native PGF readPGF(InputStream stream);

	public native String getAbstractName();

	public native Map<String,Concr> getLanguages();

	public native List<String> getCategories();
	
	public native String getStartCat();
	
	public native List<String> getFunctions();
	
	public native List<String> getFunctionsByCat(String cat);
	
	public native Type getFunctionType(String fun);

	public Iterable<ExprProb> generateAll(String startCat) {
		return new Generator(this, startCat);
	}

	public native Expr compute(Expr expr);

	//////////////////////////////////////////////////////////////////
	// private stuff
	private Pool pool;
	private long ref;

	private PGF(long pool, long ref) {
		this.pool = new Pool(pool);
		this.ref  = ref;
	}
	
	static {
        boolean loaded = false;
 		try {
			final String[] libraries = ClassScope.getLoadedLibraries(ClassLoader.getSystemClassLoader());
			for(String s : libraries){
				if(s.contains("libjpgf.so")) loaded = true;
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		if(!loaded) {
 			System.load("/usr/lib/libpgf.so");
 			System.load("/usr/lib/libjpgf.so");
 			System.loadLibrary("jpgf");
 		}
    }
}
