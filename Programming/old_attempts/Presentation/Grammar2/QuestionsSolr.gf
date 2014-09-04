concrete QuestionsSolr of Questions = {

	lincat
	  Question = {s : Str} ;
	  Object = {s : Str} ;
	  Location = {s : Str} ;
	  Expertise = {s : Str} ;
	  Relation = {s : Str} ;
	  
	lin
	  Question_Q object relation = {s = "select" + "?q=" + "object_type" ++ ":" ++ object.s ++ "AND" ++ relation.s} ;
	  Work location = {s = "location" ++ ":" ++ location.s} ;
	  Know expertise = {s = "expertise" ++ ":" ++ expertise.s} ;
	  Person = {s = "person"} ;
	  Gothenburg = {s = "Gothenburg OR Göteborg"} ;
	  Stockholm = {s = "Stockholm"} ;
	  Java = {s = "Java"} ;
}
