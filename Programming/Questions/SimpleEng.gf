concrete SimpleEng of Simple = SymbolEng ** open StringOper, ParadigmsEng, SyntaxEng, ExtraEng in {
	
	lincat
	  Sentence = Text ;
	  Question = Utt ;
	  Relation = { subj : N ; vp : VP } ;
	  Internal, External, Resource = N ;
	  Object, Skill, Location = NP ;

	lin
	  -- Sentence
	  --SentenceQMark_S question = mkText (mkPhr question) questMarkPunct ;
	  --SentenceDot_S question = mkText question ;

	  -- Questions
 	  -- How to use 'that' instead of which/who?	  
	  -- Implicit present
	  
	  --ImplicitSgPres_Q rel = mkUtt (mkCN rel.subj (mkRS' simultaneousAnt  rel.vp)) ;
	  ImplicitPlPres_Q rel = mkUtt (mkNP aPl_Det (mkCN rel.subj (mkRS' simultaneousAnt rel.vp))) ;
	  -- Implicit past
  	  --ImplicitSgPast_Q rel = mkUtt (mkCN rel.subj (mkRS' anteriorAnt rel.vp)) ;
	  --ImplicitPlPast_Q rel = mkUtt (mkNP aPl_Det (mkCN rel.subj (mkRS' anteriorAnt rel.vp))) ;
	  -- Explicit present
	  --ExplicitSgPres_Q rel = mkUtt (mkQS presentTense (mkQCl (mkIP which_IDet rel.subj) rel.vp)) ;
	  ExplicitPlPres_Q rel = mkUtt (mkQS presentTense (mkQCl (mkIP whichPl_IDet rel.subj) rel.vp)) ;
	  -- Explicit past
	  --ExplicitSgPast_Q rel = mkUtt (mkQS pastTense (mkQCl (mkIP which_IDet rel.subj) rel.vp)) ;
	  --ExplicitPlPast_Q rel = mkUtt (mkQS pastTense (mkQCl (mkIP whichPl_IDet rel.subj) rel.vp)) ;
	  
	  -- Question generalized
	  --General_Q rel = mkUtt (mkQS (presentTense | pastTense) (mkQCl (mkIP (which_IDet | whichPl_IDet) rel.subj) rel.vp)) 
	  --               | let cn = mkCN rel.subj (mkRS' (simultaneousAnt | anteriorAnt) rel.vp)
	  --                 in mkUtt cn | mkUtt (mkNP (aSg_Det | aPl_Det) cn) ;
	  --General_Q rel = mkUtt (mkQS (presentTense | pastTense) (mkQCl (mkIP (which_IDet | whichPl_IDet) rel.subj) rel.vp))
      --         | let cn = mkCN (mkCN rel.subj) (mkRS' (simultaneousAnt | anteriorAnt) rel.vp)
      --           in mkUtt cn | mkUtt (mkNP (aSg_Det | aPl_Det) cn) ;               
      
	  -- Subjects
	  Person_N = mkN "person" ("people" | "persons") ;
--	  Person_N = mkN "person" ;
	  Project_N = mkN "project" ;
	  Customer_N = mkN "customer" ;
      
  	  -- Unknown names
	  MkSkill s = mkNP (SymbPN (MkSymb s)) ;
	  MkObject s = mkNP (SymbPN (MkSymb s)) ;
	  MkLocation s = mkNP (SymbPN (MkSymb s)) ;
--	  MkSkill =  mkNP (mkN "skill");
--	  MkObject = mkNP (mkN "object") ;
--	  MkLocation = mkNP (mkN "location") ;
	  
	  -- Conjunctions
	  And_S s1 s2 = mkNP and_Conj s1 s2 ;
	  Or_S s1 s2 = mkNP or_Conj s1 s2 ;
	  -- Redudant, how use to use And : a -> a -> a ?
  	  And_L s1 s2 = mkNP and_Conj s1 s2 ;
	  Or_L s1 s2 = mkNP or_Conj s1 s2 ;
	  
	  And_O s1 s2 = mkNP and_Conj s1 s2 ;
	  Or_O s1 s2 = mkNP or_Conj s1 s2 ;
      
	  -- Relations
	  Know_R name skill= { subj = name ; vp = mkR "know" skill };
	  UseExt_R res obj= { subj = res ; vp = mkR "use" obj } ;
	  UseRes_R res obj= { subj = res ; vp = mkR "use" obj } ;
	  WorkWith_R name obj = { subj = name ; vp = mkVP' with_Prep "work" obj } ;
	  WorkIn_R name location = { subj = name ; vp = mkVP' in_Prep "work" location } ;
	  
	oper
	  -- Both anteriors
--	  anteriors : Ant = anteriorAnt | simultaneousAnt ;
	  anteriors : Ant = simultaneousAnt ;
	  -- Make a relation
	  mkR : Str -> NP -> VP = \pred, obj -> mkVP' (mkV2 (mkV pred)) obj ;
	  -- Make a relative sentence with only present tense
	  mkRS' : Ant -> VP -> RS = \anterior,vp -> mkRS presentTense anterior (mkRCl which_RP vp) ;
	  -- Make a verb phrase
	  mkVP' = overload {
	  	mkVP' : Prep -> Str -> NP -> VP = \prep,str,noun -> mkVP'' (mkVP (mkV2 (mkV str) prep) noun) ;
	    mkVP' : V2 -> NP -> VP = \v2,np -> mkVP'' (mkVP v2 np) ;
	  };
	  -- Progressive = "she is drinking wine"
	  -- Non-progressive = "she drinks wine"
	  --mkVP'' : VP -> VP = \vp -> (progressiveVP vp) | vp ;
	  mkVP'' : VP -> VP = \vp -> vp ;
}