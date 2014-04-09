concrete SimpleSQL of Simple = open StringOper in {
	
	lincat
	  Question = SS ;
	  Subject = SS;
	  Predicate = {p : Str ; o : Str} ;
	  
	  Name = SS;
	  Object = SS ;
	  Resource = SS ;

	lin
	  -- Questions
	  Indirect_Q name predicate = select name predicate ;
	  Direct_Q subject predicate = select subject predicate ;
	  -- Names
	  Person_N = ss "persons" ;
	  Developer_N = ss "developers" ;
	  Project_N = ss "projects" ;
	  Customer_N = ss "customers" ;
	  
	  Which_Sg name = name ;
	  Which_Pl name = name ;
	  
	  -- Predicates
	  Know_P object = {p = "know" ; o = object.s} ;
  	  Use_P  object = {p = "use" ; o = object.s} ;
  	  WorkWith_P  person = {p = "work" ; o = person.s } ;
  	  WorkIn_P person = {p = "location" ; o = person.s } ;
  	  
  	  MkPerson s = s ;

	  -- Objects
	  Java_O = ss "java" ;
	  Solr_O = ss "solr" ;
	  
	  oper
	    select : SS -> {p : Str ; o : Str} -> SS = \sub,pred -> 
	           ss ("SELECT * FROM " ++ sub.s ++ " WHERE " ++ pred.p ++ " = " ++ pred.o) ;
}