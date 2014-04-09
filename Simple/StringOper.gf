    resource StringOper = {
      param Number = Sg | Pl ;
      oper
        SS : Type = {s : Str} ;
        ss : Str -> SS = \x -> {s = x} ;
        cc : SS -> SS -> SS = \x,y -> ss (x.s ++ y.s) ;
        prefix : Str -> SS -> SS = \p,x -> ss (p ++ x.s) ;
        infix : Str -> SS -> SS -> SS = \i,x,y -> ss (x.s ++ i ++ y.s) ;
        postfix : Str -> SS -> SS = \p,x -> ss (x.s ++ p);
    }