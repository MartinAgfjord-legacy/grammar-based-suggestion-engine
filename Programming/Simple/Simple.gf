abstract Simple = Symbol ** {
	
	flags startcat = Question ;

	cat
	  Question ;      -- A question e.g. "which developers knows java"
	  Relation ;      -- A relation of a subject and an object. e.g. { "persons" ; "know" ; java" }
	  Object ;        -- An object that a person can have a relation to, e.g. "solr"
	  Skill ;         -- A skill that a person can have e.g "java"
	  Resource ;      -- E.g. "projects"
	  Location ;      -- E.g. "Gothenburg"
	  Internal ;	  -- E.g. "persons"
	  External ;      -- E.g. "customers"
	  
	fun
	  -- Questions
	  DirectSg_Q : Relation -> Question ; -- "which person knows java"
	  DirectPl_Q : Relation -> Question ; -- "which persons know java"
	  IndirectSg_Q : Relation -> Question ;  -- "person that/which knows java"
      IndirectPl_Q : Relation -> Question ;  -- "persons that/which know java"

	  -- Subjects
	  Person_N : Internal ;
	  Project_N : Resource ;
	  Customer_N : External ;
	  	  
	  -- Unknown names
	  MkSkill : Symb -> Skill ;
	  MkObject : Symb -> Object ;
	  MkLocation : Symb -> Location ;
	  
	  -- Conjunction
	  And_S : Skill -> Skill -> Skill ;
	  Or_S : Skill -> Skill -> Skill ;
	  
	  -- Relations
	  Know_R : Internal -> Skill -> Relation ;
	  -- How to have a supercat of Internal and Resource?
	  UseExt_R  : External-> Object -> Relation ;
	  UseRes_R  : Resource-> Object -> Relation ;
	  WorkWith_R  : Internal-> Object -> Relation ;
	  WorkIn_R  : Internal -> Location -> Relation ;

}