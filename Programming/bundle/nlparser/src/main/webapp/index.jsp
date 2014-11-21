<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta charset="utf-8"> 
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="URI.js"></script>
<script src="jquery-ui-1.10.4.custom/js/jquery-ui-1.10.4.custom.min.js"></script>
<script src="underscore.js"></script>
<script src="handlebars-v2.0.0.js"></script>
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
<div id="tech_info">
    <pre id="ambiguous_result">Ambiguous question, we found the following interpretations:</pre>
    <pre id="grammar_result"></pre>
    <pre id="search_result_json"></pre>
    <a href="grammar/Instrucs.gf.txt">Instrucs.gf</a>
    <a href="grammar/InstrucsI.gf.txt">InstrucsI.gf</a>
    <a href="grammar/InstrucsEngRGL.gf.txt">InstrucsEngRGL.gf</a>
    <a href="grammar/InstrucsEngConcat.gf.txt">InstrucsConcat.gf</a>
    <a href="grammar/InstrucsSweRGL.gf.txt">InstrucsSweRGL.gf</a>
    <a href="grammar/InstrucsSolr.gf.txt">InstrucsSolr.gf</a>
    <a href="grammar/LexInstrucs.gf.txt">LexInstrucs.gf</a>
    <a href="grammar/LexInstrucsEng.gf.txt">LexInstrucsEng.gf</a>
    <a href="grammar/LexInstrucsSwe.gf.txt">LexInstrucsSwe.gf</a>
</div>
<ul id="search_result">
    
</ul>

<script id="doc_template" type="text/x-handlebars-template">
    <li class="document" class="media">
        <h2 class="title">{{name}}</h2>
        <div class="media-body">
            {{#if WORKS_IN}}<div><em>Plats:</em> {{WORKS_IN}}</div>{{/if}}
            {{#if KNOWS}}<div><em>Kunskap:</em> {{KNOWS}}</div>{{/if}}
            {{#if WORKS_WITH}}<div><em>Kunder:</em> {{WORKS_WITH}}</div>{{/if}}
            {{#if USES}}<div><em>Använder:</em> {{USES}}</div>{{/if}}
        </div>
    </li>
</script>
<!--

<script id="entry-template" type="text/x-handlebars-template">
  template content
</script>
/*$('<li></li>').addClass('media').append(
                $('<div></div>').addClass('media-body')
                .append([
                    $('<span></span>').text(doc.name),
                    $('<span></span>').text("KNOWS: "+doc.KNOWS),
                    $('<span></span>').text("KNOWS: "+doc.KNOWS),
                ]));*/
-->
</body>
</html>
