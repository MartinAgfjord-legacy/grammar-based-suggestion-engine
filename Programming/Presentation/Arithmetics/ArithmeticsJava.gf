-- Auto-generated template
concrete ArithmeticsJava of Arithmetics = {

	lincat
	  Expr = {s : Str} ;
	lin
	  Plus e1 e2 = {s = e1.s ++ " + " ++ e2.s} ;
	  Number s = s ; 
}
