concrete SimpleEng of Simple = SymbolEng ** open StringOper, ParadigmsEng, SyntaxEng, SymbolEng in {
	
	lincat
	  Sentence = Text ;
	  Question = Utt ;
	  Relation = { subj : N ; vp : VP } ;
	  Internal, External, Resource = N ;
	  Object, Skill, Location = NP ;

	lin
	  -- Sentence
	  SentenceQMark_S question = mkText (mkPhr question) questMarkPunct ;
	  SentenceDot_S question = mkText question ;

	  -- Questions
 	  -- How to use 'that' instead of which/who?	  
	  -- Implicit present
	  ImplicitSgPres_Q rel = mkUtt (mkCN rel.subj (mkRS' simultaneousAnt rel.vp)) ;
--	  ImplicitSgPres_Q rel = mkUtt (mkCN rel.subj mkQS (mkQCl (mkCl (mk))))
	  ImplicitPlPres_Q rel = mkUtt (mkNP aPl_Det (mkCN rel.subj (mkRS' simultaneousAnt rel.vp))) ;
	  -- Implicit past
  	  ImplicitSgPast_Q rel = mkUtt (mkCN rel.subj (mkRS' anteriorAnt rel.vp)) ;
	  ImplicitPlPast_Q rel = mkUtt (mkNP aPl_Det (mkCN rel.subj (mkRS' anteriorAnt rel.vp))) ;
	  -- Explicit present
	  ExplicitSgPres_Q rel = mkUtt (mkQS presentTense (mkQCl (mkIP which_IDet rel.subj) rel.vp)) ;
	  ExplicitPlPres_Q rel = mkUtt (mkQS presentTense (mkQCl (mkIP whichPl_IDet rel.subj) rel.vp)) ;
	  -- Explicit past
	  ExplicitSgPast_Q rel = mkUtt (mkQS pastTense (mkQCl (mkIP which_IDet rel.subj) rel.vp)) ;
	  ExplicitPlPast_Q rel = mkUtt (mkQS pastTense (mkQCl (mkIP whichPl_IDet rel.subj) rel.vp)) ;
		  
	  -- Subjects
	  Person_N = mkN "person" ;
	  Project_N = mkN "project" ;
	  Customer_N = mkN "customer" ;
      
  	  -- Unknown names
	  MkSkill s = mkNP (SymbPN (MkSymb s)) ;
	  MkObject s = mkNP (SymbPN (MkSymb s)) ;
	  MkLocation s = mkNP (SymbPN (MkSymb s)) ;
	  
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
	  anteriors : Ant = anteriorAnt | simultaneousAnt ;
	  -- Make a relation
	  mkR : Str -> NP -> VP = \pred, obj -> mkVP (mkV2 (mkV pred)) obj ;
	  -- Make a relative sentence with only present tense
	  mkRS' : Ant -> VP -> RS = \anterior,vp -> mkRS presentTense anterior (mkRCl which_RP vp) ;
	  -- Make a verp phrase
	  mkVP' : Prep -> Str -> NP -> VP = \prep,str,noun -> mkVP (mkV2 (mkV str) prep) noun ;
}