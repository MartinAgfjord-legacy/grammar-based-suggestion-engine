-- Auto-generated template
concrete ArithmeticsEnglish of Arithmetics = {

	lincat
	  Expr = {s : Str} ;
	lin
	  Plus e1 e2 = {s = "the sum of " ++ e1.s ++ " plus " ++ e2.s} ;
	  Number s = s ;
}
