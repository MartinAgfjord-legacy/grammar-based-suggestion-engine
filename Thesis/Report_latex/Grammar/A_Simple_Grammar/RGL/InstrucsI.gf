incomplete concrete InstrucsI of Instrucs = open Syntax, LexInstrucs in {
  lincat
    Instruction = NP ;
    Subject = N ;
    Relation = RS ;
    Object = NP ;

  lin
    MkInstruction subject relation = mkNP aPl_Det (mkCN subject relation) ;
    People = person_N ;
    Know object = mkRS' (mkVP know_V2 object) ;
    Java = java_NP ;
    
   oper
      mkRS' : VP -> RS = \vp -> mkRS (mkRCl which_RP vp) ;
}
