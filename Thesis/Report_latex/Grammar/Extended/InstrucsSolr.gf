concrete InstrucsSolr of Instrucs = SymbolEng ** {
	lincat
      Instruction = Str ;
      Internal, External, Resource = Str ;
      InternalRelation, ExternalRelation, ResourceRelation = Str ;
      Skill, Organization, Location, Module = Str ;

	lin
	  -- Instructions
	  InstrucInternal internal relation = select internal relation ;
	  InstrucExternal external relation = select external relation;
	  InstrucResource resource' relation = select resource' relation;

	  -- Subjects
	  People = "Person" ;
	  Customer = "Organization" ;
	  Project = "Project" ;

	  -- Relations
	  Know obj = "KNOWS" ++ ":" ++ obj ;
  	  UseExt obj = "USES" ++ ":" ++ obj ;
  	  UseRes obj = "USES" ++ ":" ++ obj ;
	  WorkWith obj = "WORKS_WITH" ++ ":" ++ obj ;
	  WorkIn obj = "WORKS_IN" ++ ":" ++ obj ;

	  -- Boolean operators for relations
      InternalAnd s1 s2 = "(" ++ s1 ++ "AND" ++ s2 ++ ")";
      InternalOr s1 s2 = "(" ++ s1 ++ " OR " ++ s2 ++ ")";

      ExternalAnd s1 s2 = "(" ++ s1 ++ "AND" ++ s2 ++ ")";
      ExternalOr s1 s2 = "(" ++ s1 ++ " OR " ++ s2 ++ ")";

      ResourceAnd s1 s2 = "(" ++ s1 ++ "AND" ++ s2 ++ ")";
      ResourceOr s1 s2 = "(" ++ s1 ++ " OR " ++ s2 ++ ")";

   	  -- Unknown names
	  MkSkill s = "(" ++ s.s ++ ")" ;
	  MkOrganization s = "(" ++ s.s ++ ")" ;
	  MkLocation s = "(" ++ s.s ++ ")" ;
	  MkModule s = "(" ++ s.s ++ ")" ;

	  -- Boolean operators for names
	  And_S s1 s2 = "(" ++ s1 ++ " AND" ++ s2 ++ ")";
	  Or_S s1 s2 = "(" ++ s1 ++ " OR " ++ s2 ++ ")";
	  
  	  And_O s1 s2 = "(" ++ s1 ++ " AND" ++ s2 ++ ")";
	  Or_O s1 s2 = "(" ++ s1 ++ " OR " ++ s2 ++ ")";
	  
  	  And_L s1 s2 = "(" ++ s1 ++ " AND" ++ s2 ++ ")";
	  Or_L s1 s2 = "(" ++ s1 ++ " OR " ++ s2 ++ ")";
	  
	  And_M s1 s2 = "(" ++ s1 ++ " AND" ++ s2 ++ ")";
	  Or_M s1 s2 = "(" ++ s1 ++ " OR " ++ s2 ++ ")";

  oper
    select : Str -> Str -> Str = \subj,relation -> 
                    "select?q=*:*&wt=json&fq=" ++ "object_type :" 
                    ++ subj ++ "AND" ++ relation ;
}