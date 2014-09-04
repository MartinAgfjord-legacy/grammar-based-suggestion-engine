incomplete concrete QuestionsI of Questions = open Syntax, Extra, Symbol, LexQuestions in {

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
                                                  (mkRCl which_RP 
                                                     relation)));
                                                                    
      Person = person_N ;
	  Work location = mkVP (mkV2 work_V in_Prep) (mkNP location) ;
  	  Gothenburg = gothenburg_N ;
  	  Stockholm = stockholm_N ;
  	  Java = java_N ;
}