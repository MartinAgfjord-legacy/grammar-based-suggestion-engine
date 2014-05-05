abstract SimpleGen = Symbol ** {
	
	flags startcat = Question ;

	cat
	  Sentence ;	  -- E.h. which developers know Java?"
	  Question ;      -- A question e.g. "which developers know Java"
	  Relation ;      -- A relation of a subject and an object. e.g. { "persons" ; "know" ; java" }
	  Object ;        -- An object that a person can have a relation to, e.g. "Solr"
	  Skill ;         -- A skill that a person can have e.g "Java"
	  Resource ;      -- E.g. "projects"
	  Location ;      -- E.g. "Gothenburg"
	  Internal ;	  -- E.g. "persons"
	  External ;      -- E.g. "customers"
	  Text_ ;

	fun
	  --SentenceQMark_S : Question -> Text_ ;
	  --SentenceDot_S : Question -> Text_ ;
	  -- Questions
	  -- Explicit present
	  -- ExplicitSgPres_Q : Relation -> Question ; -- "which person works in Gothenburg"
	  -- ExplicitPlPres_Q : Relation -> Question ; -- "which persons work in Gothenburg"
	  -- Explicit past
  	  --ExplicitSgPast_Q : Relation -> Question ; -- "which person has worked in Gothenburg"
	  --ExplicitPlPast_Q : Relation -> Question ; -- "which persons have worked in Gothenburg"
	  -- Implicit present
	  --ImplicitSgPres_Q : Relation -> Question ;  -- "person that/which knows java"
      ImplicitPlPres_Q : Relation -> Question ;  -- "persons that/which know java"
      -- Implicit past
      --ImplicitSgPast_Q : Relation -> Question ;  -- "person that/which knows java"
      --ImplicitPlPast_Q : Relation -> Question ;  -- "persons that/which know java"

	  -- General question
	  --General_Q : Relation -> Question ;

	  -- Subjects
	  --People_N : Internal ;
	  Person_N : Internal ;
	  Project_N : Resource ;
	  Customer_N : External ;
	  	  
	  -- Unknown names
	  MkSkill : String -> Skill ;
	  MkObject : String -> Object ;
	  MkLocation : String -> Location ;
--	  MkSkill : Symb -> Skill ;
--	  MkObject : Symb -> Object ;
--	  MkLocation : Symb -> Location ;
--	  MkSkill : Skill ;
--	  MkObject : Object ;
--	  MkLocation : Location ;
	  	  
	  -- Conjunctions
	  And_S : Skill -> Skill -> Skill ;
	  Or_S : Skill -> Skill -> Skill ;
	  -- Redudant, how use to use And : a -> a -> a ?
	  And_L : Location -> Location -> Location ;
	  Or_L : Location -> Location -> Location ;
	  
	  And_O : Object -> Object -> Object ;
	  Or_O : Object -> Object -> Object ;
	  
	  
	  -- Relations
	  Know_R : Internal -> Skill -> Relation ;
	  -- How to have a supercat of Internal and Resource?
	  UseExt_R  : External-> Object -> Relation ;
	  UseRes_R  : Resource-> Object -> Relation ;
	  WorkWith_R  : Internal-> Object -> Relation ;
	  WorkIn_R  : Internal -> Location -> Relation ;

}