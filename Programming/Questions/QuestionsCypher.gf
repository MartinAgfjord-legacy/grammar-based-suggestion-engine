concrete QuestionsCypher of Questions = SymbolEng ** open StringOper, Predef in {
	
	lincat
	  Question = SS;
	  InternalRelation, ExternalRelation, ResourceRelation = {s : Str ; wh : Str} ;
	  Internal, External, Resource = SS ;
	  Object, Skill, Location = {var : Str ; wh: Str ; count : Int} ;

	lin
	  Question_I internal relation = select internal relation;
--	  Question_E external relation = select external relation;
--	  Question_R resource' relation = select resource' relation;
	  
	  -- Subjects
	  Person_N = ss "Person" ;
	  Customer_N = ss "Customer" ;
	  Project_N = ss "Project" ;

	  -- Unknown names
	  MkSkill s' = { var = s'.s ; wh = s'.s ++ ".name" ++ "=" ++ s'.s ; count = 1} ;
--	  MkObject s = ss ("(" ++ s.s ++ ")") ;
--	  MkLocation s = ss ("(" ++ s.s  ++ ")") ;
	  
--	  And_I s1 s2 = ss ("(" ++ s1.s ++ "AND" ++ s2.s ++ ")");
--	  Or_I s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
--  	  And_E s1 s2 = ss ("(" ++ s1.s ++ "AND" ++ s2.s ++ ")");
--	  Or_E s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
--  	  And_R s1 s2 = ss ("(" ++ s1.s ++ "AND" ++ s2.s ++ ")");
--	  Or_R s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
	  -- Conjunctions
	  And_S s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
--	  Or_S s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
--  	  And_O s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
--	  Or_O s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
--  	  And_L s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
--	  Or_L s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
 	  -- Relations
	  Know_R skill = {s = "(n)" ++ "-" ++ "[:" ++ "KNOWS]" ++ "-" ++ "(" ++ skill.var ++ ":Expertise)" ; wh = skill.wh } ;
	  -- Two redundant functions below
--  	  UseExt_R obj = {s = "use" ++ ":" ++ obj.s};
--  	  UseExt_R obj = {s = "use" ++ ":" ++ obj.s};
--	  WorkWith_R obj = {s = "expertise" ++ ":" ++ obj.s} ;
--	  WorkIn_R loc = {s = "[" ++ ":" ++ loc.s} ;
	  
	  oper
	    -- Make a relation
	    mkR : Str -> SS -> { relation : Str ; obj : Str } = \r,o ->
	    			{ relation = r ; obj = o.s } ;
	    -- Select-function in solr
	    select : SS -> {s : Str ; wh : Str} -> SS = \subj,relation -> 
	           ss ("MATCH" ++ relation.s ++ "WHERE" ++ relation.wh ++ "RETURN n") ;
	    foo : Int -> Str -> Str = \int,str -> drop int str  ;
}