concrete QueryEng of Query = open StringOper in {

	lincat 
      Question, IQuant, Quant, VerbPhrase = SS;
      TwoVerb, NounPhrase = SS; -- When is the type different?
      IPronoun, Noun = SS ;
	lin
	  -- Questions
	  TwoVerb_Q iPronoun verbPhrase = cc iPronoun verbPhrase ;
      -- Conjunction questions
      VerbPhrase_C question verbPhrase = infix "and" question verbPhrase ;
      Question_C question1 question2 = infix "and" question1 question2 ;

	  -- Verb phrases
	  TwoVerb_VP twoVerb nounPhrase = cc twoVerb nounPhrase ;

      --- Interrogative pronouns
      IPronoun_IP iQuant noun = cc iQuant noun ;
      Who_IP = ss "who" ;

	  -- Interrogative quantifiers
	  Which = ss "which" ;

      -- Quantifiers
      The = ss "the" ;

	  -- Two-verbs
	  Know = ss "knows"	;
	  
	  -- Noun phrases
	  Noun_NP noun = noun ;
	  Quant_NP quant noun = cc quant noun ;

	  -- Nouns
	  People = ss "people" ;
	  Java = ss "java" ;
	  Solr = ss "solr" ;
	  Wiki = ss "wiki" ;
	  Person = ss "person" ;

--	  And noun1 noun2 = infix "and" noun1 noun2 ;
}