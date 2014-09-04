concrete QuestionsEng of Questions = open SyntaxEng, ParadigmsEng in {
	lincat
	  Question = RCl ;
	  Subject = N ;
	  Relation = VP ;
	  Object = NP ;

	lin
	  MkQuestion subject relation = mkRCl which_RP relation ;
	  People = mkN "person" "people" ;
	  Know object = mkVP (mkV2 (mkV "know")) object ;
	  Java = mkNP (mkN "Java") ;
}
