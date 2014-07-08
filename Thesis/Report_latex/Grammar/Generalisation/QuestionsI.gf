incomplete concrete QuestionsI of Questions = open Syntax, LexQuestions in {
    lincat
      Question = NP ;
      Subject = N ;
      Relation = VP ;
      Object = NP ;

    lin
      MkQuestion subject relation = mkNP aPl_Det 
                                          (mkCN subject 
                                              (mkRS 
                                                  (mkRCl which_RP relation)
                                              )
                                          ) ;
      People = person_N ;
      Know object = mkVP know_V2 object ;
      Java = java_NP ;
}