instance LexInstrucsSwe of LexInstrucs = open SyntaxSwe, ParadigmsSwe in {
	oper
	  person_N = mkN "person" "personer" ;
	  know_V2 = mkV2 (mkV "kunna" "kan" "kunna" "kunde" "kunnat" "kunna") ;	  
	  java_NP = mkNP (mkN "Java");
}
