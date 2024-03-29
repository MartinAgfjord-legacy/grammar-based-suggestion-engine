<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta charset="utf-8"> 
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="URI.js"></script>
<script src="jquery-ui-1.10.4.custom/js/jquery-ui-1.10.4.custom.min.js"></script>
<script src="script.js"></script>

<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="jquery-ui-1.10.4.custom/css/smoothness/jquery-ui-1.10.4.custom.css">
<link rel="stylesheet" type="text/css" href="style.css">
<body>
<h2>Natural language parser</h2>
<input id="input" type="text" class="input-large" placeholder="Type your question.."></input>
<select id="language">
  <option value="QuestionsEng">English</option>
  <option value="QuestionsSwe">Swedish</option>
</select>
<!--
<div class="btn-group group">
  <button type="button" class="btn btn-success question">which persons know Java ?</button>
</div>
<div class="btn-group group">
  <button type="button" class="btn btn-warning question">project which uses Solr .</button>
</div>
<div class="btn-group group">
  <button type="button" class="btn btn-success question">persons who have worked in Gothenburg .</button>
</div>
<div class="btn-group group">
    <button type="button" class="btn btn-warning question">person which has worked with Solr .</button>
</div>
<div class="btn-group group">
    <button type="button" class="btn btn-warning question">which customers use Our Generic Gui ?</button>
</div>
<div class="btn-group group">
    <button type="button" class="btn btn-success question">which person knows Python and JavaScript ?</button>
</div>
<div class="btn-group group">
    <button type="button" class="btn btn-success question">persons who have worked in Gothenburg or Stockholm and Malmo .</button>
</div>
<div class="btn-group group">
    <button type="button" class="btn btn-success question">which projects are using Solr ?</button>
</div>
-->
<pre id="ambiguous_result">Ambiguous question, we found the following interpretations:</pre>
<pre id="grammar_result"></pre>
<pre id="search_result"></pre>
<a href="grammar/Questions.gf.txt">Questions.gf</a>
<a href="grammar/QuestionsI.gf.txt">QuestionsI.gf</a>
<a href="grammar/QuestionsEng.gf.txt">QuestionsEng.gf</a>
<a href="grammar/QuestionsEng.gf.txt">QuestionsSwe.gf</a>
<a href="grammar/QuestionsSolr.gf.txt">QuestionsSolr.gf</a>
<a href="grammar/QuestionsAmbig.gf.txt">QuestionsAmbig.gf</a>
<a href="grammar/LexQuestions.gf.txt">LexQuestions.gf</a>
<a href="grammar/LexQuestionsEng.gf.txt">LexQuestionsEng.gf</a>
<a href="grammar/LexQuestionsSwe.gf.txt">LexQuestionsSwe.gf</a>
</body>
</html>