concrete QuestionsEng of Questions = {

	lincat
	  Question = {s : Str} ;
	  Object = {s : Str} ;
	  Location = {s : Str} ;
	  Expertise = {s : Str} ;
	  Relation = {s : Str} ;
	  
	lin
	  Question_Q Object relation = {s = Object.s ++ "which" ++ relation.s} ;
	  Work location = {s = "works in" ++ location.s} ;
	  Know expertise = {s = "knows" ++ expertise.s} ;
	  Person = {s = "person"} ;
	  Gothenburg = {s = "Gothenburg"} ;
	  Stockholm = {s = "Stockholm"} ;
	  Java = {s = "Java"} ;
}
