abstract Questions = {
	
	flags startcat = Question ;
	
	cat
	  Question ;
	  Object ;
	  Location ;
	  Expertise ;
	  Relation ; 
	fun
	  Question_Q : Object -> Relation -> Question ;
	  Work : Location -> Relation ;
	  Know : Expertise -> Relation ;
	  Person : Object ;
	  Gothenburg : Location ;
	  Stockholm : Location ;
	  Java : Expertise ;
}
