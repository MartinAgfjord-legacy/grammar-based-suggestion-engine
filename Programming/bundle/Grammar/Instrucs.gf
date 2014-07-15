abstract Instrucs = Symbol ** {
	
	flags startcat = Instruction;
	
	cat
	  Instruction ;
	  Internal ;
	  External ; Resource ;
	  InternalRelation ; ExternalRelation ; ResourceRelation ;
	  Skill ; Organization ; Location ; Module ;

	fun
	  -- Instructions
	  InstrucInternal : Internal -> InternalRelation -> Instruction ;
	  InstrucExternal : External -> ExternalRelation -> Instruction ;
	  InstrucResource : Resource -> ResourceRelation -> Instruction ;

	  -- Subjects
	  People : Internal ;
	  Customer : External ;
	  Project : Resource ;

      -- Relations
	  Know_R : Skill -> InternalRelation ;
	  UseExt_R : Module -> ExternalRelation ;
	  UseRes_R : Module -> ResourceRelation ;
	  WorkIn_R : Location -> InternalRelation ;
	  WorkWith_R : Organization -> InternalRelation ;

	  -- Boolean operators for relations
	  InternalAnd : InternalRelation -> InternalRelation -> InternalRelation ;
	  InternalOr : InternalRelation -> InternalRelation -> InternalRelation ;

	  ExternalAnd : ExternalRelation -> ExternalRelation -> ExternalRelation ;
  	  ExternalOr : ExternalRelation -> ExternalRelation -> ExternalRelation ;
 	  
  	  ResourceAnd : ResourceRelation -> ResourceRelation -> ResourceRelation ;
  	  ResourceOr : ResourceRelation -> ResourceRelation -> ResourceRelation ;

  	  -- Unknown names
	  MkSkill : Symb -> Skill ;
	  MkOrganization : Symb -> Organization ;
	  MkModule: Symb -> Module ;
	  MkLocation : Symb -> Location ;

	  -- Boolean operators for Organizations
      And_S : Skill -> Skill -> Skill ;
      Or_S : Skill -> Skill -> Skill ;

      And_O : Organization -> Organization -> Organization ;
      Or_O : Organization -> Organization -> Organization ;

      And_L : Location -> Location -> Location ;
      Or_L : Location -> Location -> Location ;

      And_M : Module -> Module -> Module ;
      Or_M : Module -> Module -> Module ;
}
