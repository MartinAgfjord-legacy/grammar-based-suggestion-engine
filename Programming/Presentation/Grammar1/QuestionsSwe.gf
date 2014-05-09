concrete QuestionsSwe of Questions = {

	lincat
	  Question = {s : Str} ;
	  ObjectType = {s : Str} ;
	  Property = {s : Str} ;
	  
	lin
	  Work objectType property = {s = objectType.s ++ "vilken" ++ "arbetar i" ++ property.s} ;
	  Person = {s = "person"} ;
	  Gothenburg = {s = "Göteborg"} ;
}
