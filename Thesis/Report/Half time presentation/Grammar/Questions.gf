abstract Questions = {
	
	flags startcat = Question;
	
	cat
      Question;
	  Subject;
	  Relation;
	  Object;

	fun
	  MkQuestion : Subject -> Relation -> Question ;
	  People : Subject ;
	  Know : Object -> Relation ;
	  Java : Object ;
}