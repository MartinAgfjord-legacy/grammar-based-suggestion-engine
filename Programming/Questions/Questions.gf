abstract Questions = Symbol ** {
	
	flags startcat = Question ;

	cat
	  Question ;      -- A question e.g. "which developers know Java"
	  Relation ;      -- A relation of a subject and an object. e.g. { "persons" ; "know" ; java" }
	  Skill ;         -- A skill that a person can have e.g "Java"
	  Organization ;  -- An organization e.g. "Greenpeace"
	  Module ;        -- A (software) module e.g. "Grammatical Framework" or "Solr"
	  InternalRelation ;
 	  ExternalRelation ;
  	  ResourceRelation ;
	  Resource ;      -- E.g. "projects"
	  Location ;      -- E.g. "Gothenburg"
	  Internal ;	  -- E.g. "persons"
	  External ;      -- E.g. "customers"
	  Filter ;
	  Conjunction ;

	fun
      --Question_Q : Relation -> Question ;  -- "persons that/which know java"
	  Question_I : Internal -> InternalRelation -> Question ;
	  Question_E : External -> ExternalRelation -> Question ;
	  Question_R : Resource -> ResourceRelation -> Question ;
	  -- Subjects
	  Person_N : Internal ;
	  Project_N : Resource ;
	  Customer_N : External ;
	  	  
	  -- Unknown names
	  MkSkill : Symb -> Skill ;
	  MkOrganization : Symb -> Organization ;
	  MkModule: Symb -> Module ;
	  MkLocation : Symb -> Location ;
	 
	  -- Conjunctions
 	  And_I : InternalRelation -> InternalRelation -> InternalRelation ;
   	  Or_I : InternalRelation -> InternalRelation -> InternalRelation ;
   	  
   	  And_E : ExternalRelation -> ExternalRelation -> ExternalRelation ;
   	  Or_E : ExternalRelation -> ExternalRelation -> ExternalRelation ;
   	  
   	  And_Re : ResourceRelation -> ResourceRelation -> ResourceRelation ;
	  Or_Re : ResourceRelation -> ResourceRelation -> ResourceRelation ;
	  
	  And_S : Skill -> Skill -> Skill ;
	  Or_S : Skill -> Skill -> Skill ;
	  -- Redudant, how use to use And : a -> a -> a ?
	  And_L : Location -> Location -> Location ;
	  Or_L : Location -> Location -> Location ;
	  
	  And_O : Organization -> Organization -> Organization ;
	  Or_O : Organization -> Organization -> Organization ;
	  
  	  And_M : Module -> Module -> Module ;
	  Or_M : Module -> Module -> Module ;
	  
	  -- Relations
	  Know_R : Skill -> InternalRelation ;
	  -- How to have a supercat of Internal and Resource?
	  UseExt_R  : Module -> ExternalRelation ;
	  UseRes_R  : Module -> ResourceRelation ;
	  WorkWith_R  : Organization -> InternalRelation ;
	  WorkIn_R  : Location -> InternalRelation ;

}