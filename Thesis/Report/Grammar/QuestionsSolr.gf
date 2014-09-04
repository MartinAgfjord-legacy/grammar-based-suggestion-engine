concrete QuestionsSolr of Questions = {
	lincat
	  Question = Str ;
	  Subject = Str ;
	  Relation = Str ;
	  Object = Str ;

	lin
	  MkQuestion subject relation = "q=" ++ subject ++ "AND" ++ relation ;
	  People = "object_type : Person" ;
	  Know object = "expertise : " ++ object ;
	  Java = "Java" ;

}