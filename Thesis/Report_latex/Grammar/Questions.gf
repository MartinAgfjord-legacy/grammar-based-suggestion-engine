abstract Instrucs = {
	
	flags startcat = Instruction;
	
	cat
	  Instruction ;
	  Internal, External, Resource ;
	  InternalRelation, ExternalRelation, ResourceRelation ;
	  Object ;

	fun
	  InstrucInternal : Internal -> InternalRelation -> Instruction ;
	  InstrucExternal : External -> ExternalRelation -> Instruction ;
	  InstrucResource : Resource -> ResourceRelation -> Instruction ;

	  People : Internal ;
	  Customer : External ;
	  Project : Resource ;

	  MkObject : Symb -> Object ;

	  Know : Object -> InternalRelation ;
	  UseExt : Object -> ExternalRelation ;
	  UseRes : Object -> ResourceRelation ;
	  WorkIn : Object -> InternalRelation ;
	  WorkWith : Object -> InternalRelation ;
}