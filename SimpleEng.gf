concrete SimpleEng of Simple = open StringOper, ParadigmsEng, SyntaxEng, ResEng in {
	lincat
	  Question = SS ;
	  Subject = IP ;
	  Predicate = VP ;
	  Name = N ;
	  Object = N ;
	  
	lin
	  Question_Q subject predicate = ss (subject.s ! NPAcc).s ;
	  Which_Sg name = mkIP which_IDet name;
	  Which_Pl name = mkIP whichPl_IDet name ;

	  Developer = mkN "developer" ;

--	  Know object = mkVP (mkV2 (mkV "know")) (mkNP object);

	  Java = mkN "java" ;
}