instance LexInstrucsEng of LexInstrucs = open SyntaxEng, ParadigmsEng in {
    oper
      person_N = mkN "person" "people" ;
      know_V2 = mkV2 (mkV "know") ;	  
      java_NP = mkNP (mkPN "Java");
}
