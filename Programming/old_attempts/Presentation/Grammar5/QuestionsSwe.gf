concrete QuestionsSwe of Questions = open ParadigmsSwe, SyntaxSwe, ExtraSwe, SymbolSwe in {

	lincat
	  Question = Utt ;
	  Object = N ;
	  Location = N ;
	  Expertise = N ;
	  Relation = VP ;
	  Object = N ;
	  
	lin
	  Question_Q object relation = mkUtt 
	                                 (mkCN object 
	                                    (mkRS
	  								       (mkRCl which_RP relation)));
	  Work location = mkVP (mkV2 (mkV "arbeta") in_Prep) (mkNP location) ;
	  Know expertise = mkVP (mkV2 (mkV "ha")) (mkNP (mkCN (mkN2 (mkN "kunskap") (mkPrep "inom")) (mkNP expertise))) ;
	  Person = mkN "person" ;
	  Gothenburg = mkN "Gothenburg";
	  Stockholm = mkN "Stockholm" ;
	  Java = mkN "Java" ;
}