abstract Simple = {
	flags startcat = Question ;

	cat
	  Question ;  -- A question e.g. "which developers knows java"
	  Subject ;   -- A subject, usually one or more persons e.g. "which developers"
	  Predicate ; -- a linking part of a sentence e.g. "that knows java"
	  Object ;    -- The object which a predicate refers to e.g. "java"
	  Name ;      -- Name of a subject e.g. "developers" ;
	fun
--	  Question_Tmp : Subject -> Question ;
	  Question_Q : Subject -> Predicate -> Question ;
	  Developer : Name ;
	  Which_Sg : Name -> Subject ;
	  Which_Pl : Name -> Subject ;
	  Know : Object -> Question ;
	  Java : Object ;
}