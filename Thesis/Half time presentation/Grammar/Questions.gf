abstract Questions = {
	
	flags startcat = Question;
	
	cat
      Question; -- A question
	  Subject;  -- The subject of a question
	  Relation; -- A verb phrase
	  Object;   -- an object

	fun
	  MkQuestion : Subject -> Relation -> Question ; -- "people who know Java"
	  People : Subject ;                             -- "people"
	  Know : Object -> Relation ;                    -- "know Java"
	  Java : Object ;                                -- "Java"
}