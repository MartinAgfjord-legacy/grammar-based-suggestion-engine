#!/bin/bash
gf -src --gfo-dir=bin -make  -literal=Symb QuestionsEng.gf QuestionsSwe.gf QuestionsSolr.gf QuestionsAmbig.gf  ; mv Questions.pgf /tmp
#gf -src --gfo-dir=bin -make QuestionsEng.gf QuestionsSolr.gf
