concrete QuestionsSolr of Questions = SymbolEng ** open StringOper in {
	
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
	  Person_N = ss "Person" ;
	  Customer_N = ss "Organization" ;
	  Project_N = ss "Project" ;

	  -- Unknown names
	  MkSkill s = ss ("(" ++ s.s  ++ ")") ;
	  MkOrganization s = ss ("(" ++ s.s ++ ")") ;
	  MkLocation s = ss ("(" ++ s.s  ++ ")") ;
	  MkModule s = ss ("(" ++ s.s  ++ ")") ;
	  
	  And_I s1 s2 = ss ("(" ++ s1.s ++ "AND" ++ s2.s ++ ")");
	  Or_I s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
  	  And_E s1 s2 = ss ("(" ++ s1.s ++ "AND" ++ s2.s ++ ")");
	  Or_E s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
  	  And_R s1 s2 = ss ("(" ++ s1.s ++ "AND" ++ s2.s ++ ")");
	  Or_R s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
	  -- Conjunctions
	  And_S s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
	  Or_S s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
  	  And_O s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
	  Or_O s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
  	  And_L s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
	  Or_L s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
	  And_M s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
	  Or_M s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
 	  -- Relations
	  Know_R skill = {s = "KNOWS" ++ ":" ++ skill.s} ;
	  -- Two redundant functions below
  	  UseExt_R obj = {s = "USES" ++ ":" ++ obj.s};
  	  UseRes_R obj = {s = "USES" ++ ":" ++ obj.s};
	  WorkWith_R obj = {s = "WORKS_WITH" ++ ":" ++ obj.s} ;
	  WorkIn_R loc = {s = "WORKS_IN" ++ ":" ++ loc.s} ;
	  
	  oper
	    -- Make a relation
	    mkR : Str -> SS -> { relation : Str ; obj : Str } = \r,o ->
	    			{ relation = r ; obj = o.s } ;
	    -- Select-function in solr
	    select : SS -> SS -> SS = \subj,relation -> 
	           ss ("select?q=*:*&wt=json&fq=" ++ "object_type :" ++ subj.s ++ " AND " ++ relation.s) ;
}