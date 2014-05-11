incomplete concrete QuestionsI of Questions = open Syntax, Extra, LexQuestions in {
	
	lincat
	  Question = Utt ;
	  Relation = { subj : N ; rs: RS } ;
	  Internal, External, Resource = N ;
	  Object, Skill, Location = NP ;
	  InternalRelation = RS ;
	  ExternalRelation = RS ;
	  ResourceRelation = RS ;
	  Conjunction = Conj ;

	lin
	  -- Subjects
	  Person_N = person_N ;
	  Project_N = project_N ;
	  Customer_N = customer_N ;
      
  	  -- Unknown names
	  MkSkill s = mkNP (SymbPN (MkSymb s)) ;
	  MkObject s = mkNP (SymbPN (MkSymb s)) ;
	  MkLocation s = mkNP (SymbPN (MkSymb s)) ;
--	  MkSkill =  mkNP (mkN "skill");
--	  MkObject = mkNP (mkN "object") ;
--	  MkLocation = mkNP (mkN "location") ;
	  
	  Question_I internal filter = mkQ internal filter ;
	  Question_E external filter = mkQ external filter ;
	  Question_R resource' filter = mkQ resource' filter ;
	  
	  -- Conjunctions
	  And_I rs1 rs2 = mkRS and_Conj rs1 rs2 ;
	  Or_I rs1 rs2 = mkRS or_Conj rs1 rs2 ;
	  
	  And_E rs1 rs2 = mkRS and_Conj rs1 rs2 ;
	  Or_E rs1 rs2 = mkRS or_Conj rs1 rs2 ;
	  
	  And_R rs1 rs2 = mkRS and_Conj rs1 rs2 ;
	  Or_R rs1 rs2 = mkRS or_Conj rs1 rs2 ;

	  
	  And_S s1 s2 = mkNP and_Conj s1 s2 ;
	  Or_S s1 s2 = mkNP or_Conj s1 s2 ;
	  -- Redudant, how use to use And : a -> a -> a ?
  	  And_L s1 s2 = mkNP and_Conj s1 s2 ;
	  Or_L s1 s2 = mkNP or_Conj s1 s2 ;
	  
	  And_O s1 s2 = mkNP and_Conj s1 s2 ;
	  Or_O s1 s2 = mkNP or_Conj s1 s2 ;
      
	  -- Relations
	  Know_R skill = mkRS (mkRCl which_RP (mkVP' know_V skill)) ;
	  UseExt_R obj = mkRS (mkRCl which_RP (mkVP' use_V obj)) ;
	  UseRes_R obj = mkRS (mkRCl which_RP (mkVP' use_V obj)) ;
	  WorkWith_R obj = mkRS (mkRCl which_RP (mkVP' work_V with_Prep obj)) ;
	  WorkIn_R location = mkRS (mkRCl which_RP (mkVP' work_V in_Prep location)) ;
	  
--	  Know_R name skill= { subj = name ; vp = mkVP' know_V skill };
--	  UseExt_R res obj= { subj = res ; vp = mkVP' use_V obj } ;
--	  UseRes_R res obj= { subj = res ; vp = mkVP' use_V obj } ;
--	  WorkWith_R name obj = { subj = name ; vp = mkVP' work_V with_Prep obj } ;
--	  WorkIn_R name location = { subj = name ; vp = mkVP' work_V in_Prep location } ;
	  
	  oper
	    -- mkVP' creates a verb phrase from either
	    -- (1) a verb and a noun phrase or 
	    -- (2) a verb, a preposition and noun phrase.
	    -- Returns a variance of a verb phrase and its progressive form
	    mkVP' = overload {
          mkVP' : V -> NP -> VP = \verb_V,noun_NP -> mkVP'' (mkV2 verb_V) noun_NP ;
          mkVP' : V -> Prep -> NP -> VP = \verb_V,prep_P,noun_NP -> mkVP'' (mkV2 verb_V prep_P) noun_NP ;
	    } ;
	    mkVP'' : V2 -> NP -> VP = \verb_V2,noun_NP -> let vp_VP = mkVP verb_V2 noun_NP in
	    												 vp_VP | progressiveVP vp_VP ;
        mkQ : N -> RS -> Utt = \noun_N,rs_RS -> mkUtt 
	                     (mkNP (aPl_Det | aSg_Det) 
	                        (mkCN noun_N 
	                           rs_RS
	                        )
	                     ) ;
}