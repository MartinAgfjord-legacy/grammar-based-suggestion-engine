concrete QuestionsSwe of Questions = {

	lincat
	  Question = {s : Str} ;
	  Object = {s : Str} ;
	  Location = {s : Str} ;
	  Expertise = {s : Str} ;
	  Relation = {s : Str} ;
	  
	lin
	  Question_Q object relation = {s = object.s ++ "vilken" ++ relation.s} ;
	  Work location = {s = "arbetar i" ++ location.s} ;
	  Know expertise = {s = "har kunskap inom" ++ expertise.s} ;
	  Person = {s = "person"} ;
	  Gothenburg = {s = "Göteborg"} ;
	  Stockholm = {s = "Stockholm"} ;
	  Java = {s = "Java"} ;
}
