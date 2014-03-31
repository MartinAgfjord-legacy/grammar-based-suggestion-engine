-- ## To do: "People that have worked with Solr" What part of the sentence is 'that have' and 'with'?
--           "People who know java and have worked on the SKF project" What part of the sentence is 'on'.
abstract Query = {

  flags startcat = Question ;

  cat
    Question ; IPronoun ; IQuant ; Quant ; VerbPhrase ; TwoVerb ; NounPhrase  ; Noun  ;

  fun

    MkName : String -> Name ; -- parse string in run time programmign language
    -- Questions
    TwoVerb_Q : IPronoun -> VerbPhrase -> Question ;

    -- Conjunction questions
    VerbPhrase_C : Question -> VerbPhrase -> Question ;
    Question_C   : Question -> Question -> Question ;

    -- Verb phrases
    TwoVerb_VP : TwoVerb -> NounPhrase -> VerbPhrase ;

    -- Interrogative pronouns
    IPronoun_IP : IQuant -> Noun -> IPronoun ;
    Who_IP      : IPronoun ;
    
    -- Interrogative quantifiers
    Which   : IQuant ;

    -- Quantifiers
    The : Quant ;

    -- Two-verbs
    Know : TwoVerb ;
    Work : TwoVerb ;

    -- Noun phrases
    Noun_NP   : Noun -> NounPhrase ;
    Quant_NP  : Quant -> Noun -> NounPhrase ;

    -- Nouns
    Solr    : Noun ; -- split into skill and more
    Java    : Noun ;
    People  : Noun ;
    Wiki    : Noun ;
    Person  : Noun ;
    -- Temporary solution
    -- I assume that a special type 'Conjunction' should be used instead
    And     : Noun -> Noun -> Noun ;
}

-- relative sentence rs, "that  ..."

-- library only inherited in concrete syntax, abstact is stand-alone

-- people worked solr ==> partial parsing

-- misspellings, maybe two concrete syntaxes, one with variances of misspelled words and one with the correct ones

-- define skills in abstract syntaxt and then define them in concrete syntax using the resource library

--QSkill : Skill -> Question
--Java : Skill