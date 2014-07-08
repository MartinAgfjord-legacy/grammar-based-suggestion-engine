abstract Questions = {
	
	flags startcat = Question;
	
	cat
	  Question ; -- A question
	  Subject ;  -- The subject of a question
	  Relation ; -- A verb phrase
	  Object ;   -- an object

	fun		
	  MkQuestion : Subject -> Relation -> Question ; --
	  People : Subject ;                             -- e.g. People                     "people"
	  Know : Object -> Relation ;                    -- e.g. Know Java                  "know Java"
	  Java : Object ;                                -- e.g. Java                       "Java"
}