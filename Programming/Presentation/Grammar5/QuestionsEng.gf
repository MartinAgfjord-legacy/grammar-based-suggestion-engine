concrete QuestionsEng of Questions = open ParadigmsEng, SyntaxEng, ExtraEng, SymbolEng in {

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
	  Work location = mkVP (mkV2 (mkV "work") in_Prep) (mkNP location) ;
	  Know expertise = mkVP (mkV2 (mkV "know")) (mkNP expertise) ;
	  Person = mkN "person" ;
	  Gothenburg = mkN "Gothenburg";
	  Stockholm = mkN "Stockholm" ;
	  Java = mkN "Java" ;
}