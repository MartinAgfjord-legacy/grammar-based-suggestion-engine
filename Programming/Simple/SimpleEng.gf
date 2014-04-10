concrete SimpleEng of Simple = SymbolEng ** open StringOper, ParadigmsEng, SyntaxEng, SymbolEng in {
	
	lincat
	  Question = Utt ;
	  Relation = { subj : N ; vp : VP } ;
	  Internal, External, Resource = N ;
	  Object, Skill, Location = NP ;

	lin
	  -- Questions
	  -- How to use 'that' instead of which/who?
	  IndirectSg_Q rel = mkUtt (mkCN rel.subj (mkRS' rel.vp)) ;
	  IndirectPl_Q rel = mkUtt (mkNP aPl_Det (mkCN rel.subj (mkRS' rel.vp))) ;
	  DirectSg_Q rel = mkUtt (mkQCl (mkIP which_IDet rel.subj) rel.vp) ;
	  DirectPl_Q rel = mkUtt (mkQCl (mkIP whichPl_IDet rel.subj) rel.vp) ;
		  
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
      
	  -- Relations
	  Know_R name skill= { subj = name ; vp = mkR "know" skill };
	  UseExt_R res obj= { subj = res ; vp = mkR "use" obj } ;
	  UseRes_R res obj= { subj = res ; vp = mkR "use" obj } ;
	  WorkWith_R name obj = { subj = name ; vp = mkVP' with_Prep "work" obj } ;
	  WorkIn_R name location = { subj = name ; vp = mkVP' in_Prep "work" location } ;
	  
	oper
	  -- Both anteriors
	  anteriors : Ant = anteriorAnt  | simultaneousAnt ;
	  -- Make a relation
	  mkR : Str -> NP -> VP = \pred, obj -> mkVP (mkV2 (mkV pred)) obj ;
	  -- Make a relative sentence with only present tense
	  mkRS' : VP -> RS = \vp -> mkRS presentTense anteriors (mkRCl which_RP vp) ;
	  -- Make a verp phrase
	  mkVP' : Prep -> Str -> NP -> VP = \prep,str,noun -> mkVP (mkV2 (mkV str) prep) noun ;
	  --tense : Tense = futureTense | pastTense | presentTense ; --| conditionalTense ;
	  --polarity : Pol = positivePol | negativePol ;
}