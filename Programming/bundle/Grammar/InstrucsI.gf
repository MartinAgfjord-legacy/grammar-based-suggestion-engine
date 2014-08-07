incomplete concrete InstrucsI of Instrucs = open Syntax, LexInstrucs in {
    lincat
      Instruction = Utt ;
      Internal, External, Resource = N ;
      InternalRelation, ExternalRelation, ResourceRelation = RS ;
      Skill, Organization, Location, Module = NP ;

    lin
      -- Instructions
      InstrucInternal internal relation = mkI internal relation ;
      InstrucExternal external relation = mkI external relation ;
      InstrucResource resource' relation = mkI resource' relation ;

      -- Subjects
      People = person_N ;
      Customer = customer_N ;
      Project = project_N ;

      -- Relations
      Know_R object = mkRS' (mkVP know_V2 object) ;
      UseExt_R object = mkRS' (mkVP use_V2 object) ;
      UseRes_R object = mkRS' (mkVP use_V2 object) ;
      WorkIn_R object = mkRS' (mkVP (mkV2 work_V in_Prep) object) ;
      WorkWith_R object = mkRS' (mkVP (mkV2 work_V with_Prep) object) ;
      
      -- Boolean operators for relations
      InternalAnd rs1 rs2 = mkRS and_Conj rs1 rs2 ;
      InternalOr rs1 rs2 = mkRS or_Conj rs1 rs2 ;
    
      ExternalAnd rs1 rs2 = mkRS and_Conj rs1 rs2 ;
      ExternalOr rs1 rs2 = mkRS or_Conj rs1 rs2 ;
    
      ResourceAnd rs1 rs2 = mkRS and_Conj rs1 rs2 ;
      ResourceOr rs1 rs2 = mkRS or_Conj rs1 rs2 ;

      -- Unknown names
      MkSkill s = mkNP (SymbPN s) ;
      MkOrganization s = mkNP (SymbPN s) ;
      MkModule s = mkNP (SymbPN s) ;
      MkLocation s = mkNP (SymbPN s) ;

      -- Boolean operators for names
      And_S s1 s2 = mkNP and_Conj s1 s2 ;
      Or_S s1 s2 = mkNP or_Conj s1 s2 ;
    
      And_L s1 s2 = mkNP and_Conj s1 s2 ;
      Or_L s1 s2 = mkNP or_Conj s1 s2 ;
    
      And_O s1 s2 = mkNP and_Conj s1 s2 ;
      Or_O s1 s2 = mkNP or_Conj s1 s2 ;
    
      And_M s1 s2 = mkNP and_Conj s1 s2 ;
      Or_M s1 s2 = mkNP or_Conj s1 s2 ;

    oper
      -- Make an instruction
      mkI : N -> RS -> Utt = \noun_N,rs_RS -> mkUtt (mkNP (aPl_Det | aSg_Det) 
                             (mkCN noun_N rs_RS)) ;

      -- Make a relative sentence 
--      mkRS' : VP -> RS = \vp -> mkRS (mkRCl which_RP vp) ;
      mkRS' : VP -> RS = \vp -> mkRS (mkRCl (who_RP) (vp | progressiveVP vp)) ;
}
