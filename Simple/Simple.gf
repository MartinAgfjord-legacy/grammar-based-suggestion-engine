abstract Simple = {
	flags startcat = Question ;

	cat
	  Question ;      -- A question e.g. "which developers knows java"
	  Subject ;       -- A subject, usually one or more persons e.g. "which developers"
	  Predicate ;     -- Linking a name with an object e.g. "knows java"
	  Object ;        -- The object which a predicate refers to e.g. "java"
	  Name ;          -- Name of a subject e.g. "developers"
	  Resource ;      -- E.g. "projects"
	  
	  Person ;
	  
	fun
	  -- Questions
	  Direct_Q : Subject -> Predicate -> Question ; -- "which persons know java"
	  Indirect_Q : Name -> Predicate -> Question ;  -- "persons that/which knows java"

	  -- Direct subjects / names
	  Which_Sg : Name -> Subject ;
	  Which_Pl : Name -> Subject ;
	-- all people that have worked with <person>
	  -- Indirect subjects / names
	  Developer_N : Name ;
	  Person_N : Name ;
	  Customer_N : Name ;
	  Project_N : Resource ; 
	  
	  MkPerson : String -> Person ;

	  -- Resources / objects
	  Java_O : Object ;
	  Solr_O : Object ;
	  
	  -- Predicates
	--  Name_P : NamePredicate -> Predicate ;
	--  Res_P  : ResPredicate -> Predicate ;

	  -- Sub-predicates
	  Know_P : Object -> Predicate ;
	  Use_P  : Object -> Predicate ;
	  WorkWith_P  : Person -> Predicate ;
	  WorkIn_P  : Person -> Predicate ;

}

	  -- Deprecated
 	  --ListPredicate ; -- Two or more predicates e.g. "knows java and have experience in solr"
	  --NamePredicate ; -- Links a name to an object e.g. "knows java"
	  --ResPredicate ;  -- Links a resource to an object "uses our generic ui"
	  --And : Predicate -> Predicate -> ListPredicate ;
	  --Or : Predicate -> Predicate -> ListPredicate ;
	  --Conj_Q    : Name -> ListPredicate -> Question ; -- persons which knows java and have experience in solr" (for now only two predicates)	  	  