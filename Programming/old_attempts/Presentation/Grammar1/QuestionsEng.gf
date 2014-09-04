concrete QuestionsEng of Questions = {

	lincat
	  Question = {s : Str} ;
	  ObjectType = {s : Str} ;
	  Property = {s : Str} ;
	  
	lin
	  Work objectType property = {s = objectType.s ++ "which" ++ "works in" ++ property.s} ;
	  Person = {s = "person"} ;
	  Gothenburg = {s = "Gothenburg"} ;
}
