concrete QuestionsSolr of Questions = {

	lincat
	  Question = {s : Str} ;
	  ObjectType = {s : Str} ;
	  Property = {s : Str} ;
	  
	lin
	  Work objectType property = {s = "select" + "?q=" + "object_type" ++ ":" ++ objectType.s ++ "AND" ++ "location" ++ ":" ++ property.s} ;
	  Person = {s = "person"} ;
	  Gothenburg = {s = "Gothenburg OR Göteborg"} ;
}
