concrete InstrucsEngConcat of Instrucs = SymbolEng ** {
 lincat
  Instruction = Str ;
  Internal, External, Resource = Str ;
  InternalRelation, ExternalRelation, ResourceRelation = Str ;
  Skill, Organization, Location, Module = Str ;

 lin
  -- Instructions
  InstrucInternal internal relation = internal ++ 
                                   ("who" | "which" | "that") ++ relation ;
  InstrucExternal external relation = external ++ 
                                   ("who" | "which" | "that") ++ relation ;
  InstrucResource resource' relation = resource' ++ 
                                   ("which" | "that") ++ relation ;

  -- Subjects
  People = "people" | "persons" | "person" ;
  Customer = "customers" | "customer" ;
  Project = "projects" | "project" ;

  -- Relations
  Know_R obj = ("know" | "knows") ++ obj;
  UseExt_R obj = ("use" | "uses") ++ obj ;
  UseRes_R obj = ("use" | "uses") ++ obj ;
  WorkWith_R obj = ("work with" | "works with") ++ obj ;
  WorkIn_R obj = ("work in" | "works in") ++ obj ;

  -- Boolean operators for relations
  InternalAnd s1 s2 = s1 ++ "and" ++ s2 ;
  InternalOr s1 s2 = s1 ++ "or" ++ s2 ;

  ExternalAnd s1 s2 = s1 ++ "and" ++ s2 ;
  ExternalOr s1 s2 = s1 ++ "or" ++ s2 ;

  ResourceAnd s1 s2 = s1 ++ "and" ++ s2 ;
  ResourceOr s1 s2 = s1 ++ "or" ++ s2 ; 

  -- Unknown names
  MkSkill s = s.s ;
  MkOrganization s = s.s ;
  MkLocation s = s.s ;
  MkModule s = s.s ;

  -- Boolean operators for names
  And_S s1 s2 = s1 ++ "and" ++ s2 ;
  Or_S s1 s2 = s1 ++ "or" ++ s2 ;
	  
  And_O s1 s2 = s1 ++ "and" ++ s2 ;
  Or_O s1 s2 = s1 ++ "or" ++ s2 ;
	  
  And_L s1 s2 = s1 ++ "and" ++ s2 ;
  Or_L s1 s2 = s1 ++ "or" ++ s2 ;
	  
  And_M s1 s2 = s1 ++ "and" ++ s2 ;
  Or_M s1 s2 = s1 ++ "or" ++ s2 ;
}
