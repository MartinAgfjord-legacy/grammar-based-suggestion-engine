abstract Arithmetics = {
	
	flags startcat = Expr ;
	
	cat
	  Expr ;
	fun
	  Plus : Expr -> Expr -> Expr ;
	  Number : String -> Expr ;
}
