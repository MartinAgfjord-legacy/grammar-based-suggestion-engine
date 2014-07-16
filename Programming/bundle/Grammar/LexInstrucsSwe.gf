instance LexInstrucsSwe of LexInstrucs = open SyntaxSwe, ParadigmsSwe in {
	oper
          person_N = mkN "person" "personer" ;
          project_N = mkN "projekt" "projekt" ;
          customer_N = mkN "kund" "kunder" ;
          work_V = mkV "arbeta" ;
          know_V2 = mkV2 (mkV "kunna" "kan" "kunna" "kunde" "kunnat" "kunna");
          use_V2 = mkV2 (mkV "använda" "använder" "använda" "använde" "använt" "använda");
}
