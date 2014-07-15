instance LexInstrucsEng of LexInstrucs = open SyntaxEng, ParadigmsEng in {
	oper
	  person_N = mkN "person" "people" ;
	  java_NP = mkNP (mkPN "Java");
	  project_N = mkN "project" ;
	  customer_N = mkN "customer" ;
	  work_V = mkV "work" ;
	  know_V2 = mkV2 (mkV "know") ;
	  use_V2 = mkV2 (mkV "use") ;
}
