<html>
<head>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="URI.js"></script>
<script src="script.js"></script>
<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="style.css">
<body>
<h2>Natural language parser</h2>
<input id="input" type="text" class="input-large" placeholder="Type your question.."></input>

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
    <button type="button" class="btn btn-success question">which person knows Sharepoint and JavaScript ?</button>
</div>
<div class="btn-group group">
    <button type="button" class="btn btn-success question">persons who have worked in Gothenburg or Stockholm and Malmo .</button>
</div>

<pre id="baz"></pre>
<a href="grammar/Simple.gf.txt">Simple.gf</a>
<a href="grammar/SimpleEng.gf.txt">SimpleEng.gf</a>
<a href="grammar/SimpleSolr.gf.txt">SimpleSolr.gf</a>
</body>
</html>