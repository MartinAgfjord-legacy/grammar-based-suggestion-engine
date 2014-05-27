instance LexQuestionsSwe of LexQuestions = open SyntaxSwe, ParadigmsSwe  in {
	oper
	  person_N = mkN "person" "personer" ;
	  project_N = mkN "projekt" "projekt" ;
	  customer_N = mkN "kund" "kunder";
	  work_V = mkV "arbeta" ;
	  know_V = mkV "kunna" "kan" "kunna" "kunde" "kunnat" "kunna";
	  use_V = mkV "använda" "använder" "använda" "använde" "använt" "använda";
	  
	  
}