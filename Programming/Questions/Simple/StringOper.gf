    resource StringOper = {
      oper
        SS : Type = {s : Str} ;
        ss : Str -> SS = \x -> {s = x} ;
    }