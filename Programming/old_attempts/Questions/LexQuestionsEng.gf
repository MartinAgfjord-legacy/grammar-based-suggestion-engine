instance LexQuestionsEng of LexQuestions = open SyntaxEng, ParadigmsEng in {
	oper
	  person_N = mkN "person" ("people" | "persons") ;
	  project_N = mkN "project" ;
	  customer_N = mkN "customer" ;
	  work_V = mkV "work" ;
	  know_V = mkV "know" ;
	  use_V = mkV "use" ;
	  
	  
}