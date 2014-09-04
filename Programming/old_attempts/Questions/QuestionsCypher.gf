concrete QuestionsCypher of Questions = SymbolEng ** open StringOper, Predef in {
	
	param OperationParam = And | Or | Single ;
	
    oper
	  Operation : Type = {foo : Str ; bar: Str; op : OperationParam} ;
 	  Rec : Type = { foo : (Str -> Str) } ;
	
{--
   Implement and/or by using partial application,
   
   Given the AST Know_R (MkSkill "Java")
   create an operation foo : Name -> (Verb -> Str) = \name,fun' -> 
	
   Given the AST Know_R ((And_I (MkSkill "Java") (MkSkill "Python))),
   The interpreter will first try to linearize (MkSkill


--}
	lincat
	  Question = SS;
	  InternalRelation, ExternalRelation, ResourceRelation = SS ;
	  Internal, External, Resource = SS ;
	  Object, Skill, Location = Rec ;
	  Foo = SS -> SS ;

	lin
	  Question_I internal relation = select internal relation;
--	  Question_D foo = ss(foo "butThis") ; 
--	  Question_E external relation = select external relation;
--	  Question_R resource' relation = select resource' relation;
	  
	  -- Subjects
	  Person_N = ss "Person" ;
	  Customer_N = ss "Customer" ;
	  Project_N = ss "Project" ;

	  -- Unknown names
	  MkSkill s' = { foo = bax s'.s } ;
--	  MkObject s = ss ("(" ++ s.s ++ ")") ;
--	  MkLocation s = ss ("(" ++ s.s  ++ ")") ;
	  
--	  And_I s1 s2 = {foo = s1.s ; bar = (bar s2) ; op = And };
--	  Or_I s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
--  	  And_E s1 s2 = ss ("(" ++ s1.s ++ "AND" ++ s2.s ++ ")");
--	  Or_E s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
--  	  And_R s1 s2 = ss ("(" ++ s1.s ++ "AND" ++ s2.s ++ ")");
--	  Or_R s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
	  -- Conjunctions
--	  And_S s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
--	  Or_S s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
--  	  And_O s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
--	  Or_O s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
--  	  And_L s1 s2 = ss ("(" ++ s1.s ++ " AND" ++ s2.s ++ ")");
--	  Or_L s1 s2 = ss ("(" ++ s1.s ++ " OR " ++ s2.s ++ ")");
	  
 	  -- Relations
	  Know_R skill = ss(skill.foo "KNOWS") ;
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
	    select : SS -> SS -> SS = \subj,relation -> 
	           ss ("MATCH (n:Person)," ++ relation.s ++ "RETURN n") ;
	    bar : Operation -> Str -> Str = \o,p -> case o.op of {
	    	 						       And => "(n)" ++ "-" ++ "[:" + p + "]" ++ "->" ++ "(:Expertise { name : '" ++ o.foo ++ "'})" ++ "," ++
	    	 						              "(n)" ++ "-" ++ "[:" + p + "]" ++ "->" ++ "(:Expertise { name : '" ++ o.bar ++ "'})" ;
	    	 						       Or => "" ;
	    	 						       Single => "(n)" ++ "-" ++ "[:" + p + "]" ++ "->" ++ "(:Expertise { name : '" ++ o.foo ++ "'})"
	    							   };
        bax : Str -> (Str -> Str) = \o,p -> "(n)" ++ "-" ++ "[:" + p + "]" ++ "->" ++ "(:Expertise { name : '" ++ o ++ "'})";
 
}