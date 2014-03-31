    resource StringOper = {
      param Number = Sg | Pl ;
      oper
        SS : Type = {s : Str} ;
        ss : Str -> SS = \x -> {s = x} ;
        cc : SS -> SS -> SS = \x,y -> ss (x.s ++ y.s) ;
        prefix : Str -> SS -> SS = \p,x -> ss (p ++ x.s) ;
        infix : Str -> SS -> SS -> SS = \i,x,y -> ss (x.s ++ i ++ y.s) ;
        postfix : Str -> SS -> SS = \p,x -> ss (x.s ++ p);
        Noun : Type = {s : Number => Str} ;
        regNoun : Str -> Noun = \w ->
          let
            ws : Str = case w of {
              _ + ("a" | "e" | "i" | "o") + "o" => w + "s" ;  -- bamboo
              _ + ("s" | "x" | "sh" | "o")      => w + "es" ; -- bus, hero
              _ + "z"                           => w + "zes" ;-- quiz
              _ + ("a" | "e" | "o" | "u") + "y" => w + "s" ;  -- boy
              x + "y"                           => x + "ies" ;-- fly
              _                                 => w + "s"    -- car
            }
          in
          mkNoun w ws ;

        mkNoun : Str -> Str -> Noun = \x,y -> {
          s = table {
            Sg => x ;
            Pl => y
            }
          } ;

        foo : SS -> Noun -> Noun = \x,y -> 
          { s = table { n => x.s ++ y.s ! n } } ;
        bar : Noun -> SS -> Noun = \x,y -> 
          { s = table { n => x.s ! n ++ y.s } } ;
    }