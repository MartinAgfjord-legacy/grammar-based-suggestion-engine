concrete QuestionsAmbig of Questions = SymbolEng ** open StringOper in {
	
	lincat
	  Question = SS;
	  InternalRelation, ExternalRelation, ResourceRelation = SS ;
	  Internal, External, Resource = SS ;
	  Object, Skill, Location = SS ;

	lin
	  Question_I internal relation = select internal relation ;
	  Question_E external relation = select external relation;
	  Question_R resource' relation = select resource' relation;
	  
	  -- Subjects
	  Person_N = ss "people" ;
	  Customer_N = ss "customers" ;
	  Project_N = ss "projects" ;

	  -- Unknown names
	  MkSkill s = s ;
	  MkObject s = s ;
	  MkLocation s = s ;
	  
	  And_I s1 s2 = ss ("(" ++ s1.s ++ "and" ++ s2.s ++ ")");
	  Or_I s1 s2 = ss ("(" ++ s1.s ++ "or" ++ s2.s ++ ")");
	  
  	  And_E s1 s2 = ss ("(" ++ s1.s ++ "and" ++ s2.s ++ ")");
	  Or_E s1 s2 = ss ("(" ++ s1.s ++ "or" ++ s2.s ++ ")");
	  
  	  And_R s1 s2 = ss ("(" ++ s1.s ++ "and" ++ s2.s ++ ")");
	  Or_R s1 s2 = ss ("(" ++ s1.s ++ "or" ++ s2.s ++ ")");
	  
	  -- Conjunctions
	  And_S s1 s2 = ss ("(" ++ s1.s ++ "and" ++ s2.s ++ ")");
	  Or_S s1 s2 = ss ("(" ++ s1.s ++ "or" ++ s2.s ++ ")");
	  
  	  And_O s1 s2 = ss ("(" ++ s1.s ++ "and" ++ s2.s ++ ")");
	  Or_O s1 s2 = ss ("(" ++ s1.s ++ "or" ++ s2.s ++ ")");
	  
  	  And_L s1 s2 = ss ("(" ++ s1.s ++ "and" ++ s2.s ++ ")");
	  Or_L s1 s2 = ss ("(" ++ s1.s ++ "or" ++ s2.s ++ ")");
	  
 	  -- Relations
	  Know_R skill = {s = "who know" ++ skill.s} ;
	  -- Two redundant functions below
  	  UseExt_R obj = {s = "which use" ++ obj.s};
  	  UseExt_R obj = {s = "which use" ++ obj.s};
	  WorkWith_R obj = {s = "who work with" ++ obj.s} ;
	  WorkIn_R loc = {s = "who works in" ++ loc.s} ;
	  
	  oper
	    -- Make a relation
	    mkR : Str -> SS -> { relation : Str ; obj : Str } = \r,o ->
	    			{ relation = r ; obj = o.s } ;
	    -- Select-function in solr
	    select : SS -> SS -> SS = \subj,relation -> 
	           ss (subj.s ++ relation.s) ;
}