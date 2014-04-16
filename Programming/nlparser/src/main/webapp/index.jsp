<html>
<head>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="URI.js"></script>
<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="style.css">
<script>

function syntaxHighlight(json) {
    if (typeof json != 'string') {
         json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/('(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\'])*'(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        var cls = 'number';
        if (/^'/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
}

$(document).ready(function(){
    parseFromURL();
    addEventListener();
    initExampleQuestions();
$(window).on('hashchange', function () {
        console.log('asd');
        parseFromURL();
    });
});

function parseFromURL(){
    var uri = new URI(window.location.href);
    uri = new URI(uri.filename() + uri.fragment());
    if(uri.search(true).hasOwnProperty('q')){
        parse(uri.search(true).q);
        $('#input').val(uri.search(true).q);
    } else{
        $('#input').focus();
    }
}

function addQueryToURL(query){
    var uri = new URI(window.location.href);
    uri.hash('?q=' + query.replace(/ /g, '+'));
    window.location.href = uri.toString();
}

function addEventListener(){
    $(document).keypress(function(e) {
        if(e.which == 13) {
            addQueryToURL($('#input').val());
            parse($('#input').val());
        }
    });
}

function initExampleQuestions(){
    $('.question').each(function(){
        var query = $(this).text();
        $(this).click(function(){
            addQueryToURL(query);
            parseFromURL();
        });
    });
}

function parse(query){
    var uri = new URI(window.location.href);
    if(uri.protocol() == 'file'){
        var host = 'localhost:8080';
    }else {
        var host = uri.hostname() + ':' + uri.port();
    }
    $.ajax({
        url: 'http://' + host + '/nlparser/api/parse',
        jsonp: "callback",
        dataType: "jsonp",
        data: {
            q: query,
            format: "jsonp"
        },
        success: function(response) {
            if(typeof response.err != 'undefined'){
               $('#baz').empty().append('<span class="error">Expected another word than </span>' + '<span class="error-word">' + response.err + '</span>');
            }
            else{
                var str = JSON.stringify(response, undefined, 4);
                //Replace e.g. "ast" : "Direct_Q (MkSymb \"Java\")" with 'ast' : 'Direct_Q (MkSymb "Java")'
                str = str.replace(/\\"/g, '\\');
                str = str.replace(/\"/g, "'");
                str = str.replace(/\\/g, '"');
                $('#baz').empty().append(syntaxHighlight(str));
            }
        },
        error: function(request, status, error) {
            $('#baz').empty().append(status);
        }
    });
}
</script>
<body>
<h2>Natural language parser</h2>
<input id="input" type="text" class="input-large" placeholder="Type your question.."></input>
<div class="btn-group">
  <button type="button" class="btn btn-default question">which persons know Java ?</button>
  <button type="button" class="btn btn-default question">project which uses Solr .</button>
  <button type="button" class="btn btn-default question">persons who have worked in Gothenburg .</button>
</div>
<div class="btn-group">
    <button type="button" class="btn btn-default question">person which has worked with Solr .</button>
    <button type="button" class="btn btn-default question">which customers use Our Generic Gui ?</button>
    <button type="button" class="btn btn-default question">which person knows Sharepoint and JavaScript ?</button>
</div>

<div class="btn-group">
    <button type="button" class="btn btn-default question">persons who have worked in Gothenburg or Stockholm and Malmo .</button>
</div>

<pre id="baz"></pre>
<a href="grammar/Simple.gf.txt">Simple.gf</a>
<a href="grammar/SimpleEng.gf.txt">SimpleEng.gf</a>
<a href="grammar/SimpleSolr.gf.txt">SimpleSolr.gf</a>
</body>
</html>