# Master thesis 2014
## Current to do
* Elaborate more on results
* Finish mail to Krasimir
* Send draft to Krasimir
* Upload application to makasd
* add '$' to all shell examples
* (Unsure) Write about how we use absolute value when searching with Solr
* Maybe add actual Solr results in the Result chapter (ask Krasimir)
* Maybe write about how the actual parsing and linearization works with GF (in related work section)

## Old to do
* Graph database! Mock data
* Filter names by using valid already existing sub query. Hard to do with current data structure
* Would be nice to support parentheses in QuestionsEng
* Samma namn föreslås flera gånger
* För att välja mellan tvetydig fråga så säg att man väljer en och sedan:
construct new concrete language to explain which abstract syntax tree to choose
send sample of swedish letter with grammar

## Unsure
* Use phonetic algorithm to find which word(s) that matches a question. The idea is to store each name with its phonetic representation. When we want to find which words that are names in a question, we encode all words in the question into its phonetic representation and matches against names. We can then use indexOf(Str) to find where the name occurs. However, this method does not support partial word completion which is a major drawback, we do not want to wait until the user has typed the whole name.

## Old
1. Suggest sentence fungerar inte ultimat, den föreslår samma namn flera gånger. Föreslå enbart ett namn en gång
Ta bort förslag som föreslår samma verb flera gånger, t.ex. 'people who know java and who know c'. Det ska istället vara 'people who know java and c'
2. Koppla frågorna till queries som går att utföra (hämta namn från riktiga servern)
