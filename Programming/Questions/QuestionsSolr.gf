concrete QuestionsSolr of Questions = SymbolEng ** open StringOper in {
	
	lincat
	  Question = SS;
	  Relation = { subj : Str ; pred : Str ; obj : Str } ;
	  Internal, External, Resource = SS ;
	  Object, Skill, Location = SS ;

	lin
	  Question_Q rel = select rel.subj rel.pred rel.obj ;
	  
	  -- Subjects
	  Person_N = ss "object_type : person" ;
	  Customer_N = ss "customers" ;
	  Project_N = ss "projects" ;

	  -- Unknown names
	  MkSkill s = ss ("*" ++ s.s  ++ "*") ;
	  MkObject s = ss ("*" ++ s.s ++ "*") ;
	  MkLocation s = ss ("*" ++ s.s  ++ "*") ;
	  
	  -- Conjunctions
	  And_S s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
	  Or_S s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
  	  And_O s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
	  Or_O s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
  	  And_L s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
	  Or_L s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
 	  -- Relations
	  Know_R name skill = mkR name "expertise" skill ;
	  -- Two redundant functions below
  	  UseExt_R res obj = mkR res "use" obj;
  	  UseRes_R res obj = mkR res "use" obj;
  	  WorkWith_R name obj = mkR name "expertise" obj ;
  	  WorkIn_R name loc = mkR name "location" loc ;
	  
	  oper
	    -- Make a relation
	    mkR : SS -> Str -> SS -> { subj : Str ; pred : Str ; obj : Str } = \s,p,o ->
	    			{ subj = s.s ; pred = p ; obj = o.s } ;
	    -- Select-function in solr
	    select : Str -> Str -> Str -> SS = \subj,pred,obj -> 
	           ss ("select?q=*:*&wt=json&fq=" ++ subj ++ " AND " ++ pred ++ ":" ++ obj) ;
}