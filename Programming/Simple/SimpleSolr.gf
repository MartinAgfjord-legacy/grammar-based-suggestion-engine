concrete SimpleSolr of Simple = SymbolEng ** open StringOper in {
	
	lincat
	  Sentence, Question = SS;
	  Relation = { subj : Str ; pred : Str ; obj : Str } ;
	  Internal, External, Resource = SS ;
	  Object, Skill, Location = SS ;

	lin
	
	  SentenceDot_S question = question ;
	  SentenceQMark_S question = question ;
	  -- Questions
	  -- Redundant functions, all the same
	  -- Implicit present
	  ImplicitSgPres_Q rel = select rel.subj rel.pred rel.obj ;
	  ImplicitPlPres_Q rel = select rel.subj rel.pred rel.obj ;
	  
	  -- Implicit past
	  ImplicitSgPast_Q rel = select rel.subj rel.pred rel.obj ;
	  ImplicitPlPast_Q rel = select rel.subj rel.pred rel.obj ;
	  
	  -- Explicit present
	  ExplicitSgPres_Q rel = select rel.subj rel.pred rel.obj ;
	  ExplicitPlPres_Q rel = select rel.subj rel.pred rel.obj ;
	  
	  -- Explicit past
	  ExplicitSgPast_Q rel = select rel.subj rel.pred rel.obj ;
	  ExplicitPlPast_Q rel = select rel.subj rel.pred rel.obj ;
	  
	  -- Subjects
	  Person_N = ss "object_type : person" ;
	  Customer_N = ss "customers" ;
	  Project_N = ss "projects" ;

	  -- Unknown names
	  MkSkill s = s ;
	  MkObject s = s ;
	  MkLocation s = s ;
	  
	  -- Conjunctions
	  And_S s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
	  Or_S s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
 	  -- Relations
	  Know_R name skill = mkR name "know" skill ;
	  -- Two redundant functions below
  	  UseExt_R res obj = mkR res "use" obj;
  	  UseRes_R res obj = mkR res "use" obj;
  	  WorkWith_R name obj = mkR name "work" obj ;
  	  WorkIn_R name loc = mkR name "location" loc ;
	  
	  oper
	    -- Make a relation
	    mkR : SS -> Str -> SS -> { subj : Str ; pred : Str ; obj : Str } = \s,p,o ->
	    			{ subj = s.s ; pred = p ; obj = o.s } ;
	    -- Select-function in solr
	    select : Str -> Str -> Str -> SS = \subj,pred,obj -> 
	           ss ("select?q=*:*&wt=json&fq=" ++ subj ++ " AND " ++ pred ++ ":" ++ obj) ;
}