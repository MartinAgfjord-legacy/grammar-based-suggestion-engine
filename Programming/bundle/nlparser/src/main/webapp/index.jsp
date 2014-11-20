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
  <option value="InstrucsEngConcat">English (concat)</option>
  <option value="InstrucsEngRGL">English (RGL)</option>
  <option value="InstrucsSweRGL">Swedish (RGL)</option>
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
<a href="grammar/Instrucs.gf.txt">Instrucs.gf</a>
<a href="grammar/InstrucsI.gf.txt">InstrucsI.gf</a>
<a href="grammar/InstrucsEngRGL.gf.txt">InstrucsEngRGL.gf</a>
<a href="grammar/InstrucsEngConcat.gf.txt">InstrucsConcat.gf</a>
<a href="grammar/InstrucsSweRGL.gf.txt">InstrucsSweRGL.gf</a>
<a href="grammar/InstrucsSolr.gf.txt">InstrucsSolr.gf</a>
<a href="grammar/LexInstrucs.gf.txt">LexInstrucs.gf</a>
<a href="grammar/LexInstrucsEng.gf.txt">LexInstrucsEng.gf</a>
<a href="grammar/LexInstrucsSwe.gf.txt">LexInstrucsSwe.gf</a>
</body>
</html>
