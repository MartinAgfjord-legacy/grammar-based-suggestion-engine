<html>
<head>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script>

$(document).ready(function(){
	$('#submit').click(function(){
	var input = $('#input').val();
		$.ajax({
    url: "http://localhost:8080/nlparser/api/parse",
    jsonp: "callback",
    dataType: "jsonp",
    data: {
        q: input,
        from: "eng",
        to: "solr",
        format: "json"
    },
 
    // work with the response
    success: function( response ) {
        console.log( response ); // server response
        $('#foo').append($('<li>' + response.query.query + '</li>'));
        $(response.result).each(function(index){
	        $('#bar').append($('<li>' + response.result[index].query + '</li>'));        	
        });
    }
});	
	});
});
</script>
<body>
<input id="input"/>
<input id="submit" type="submit" value="submit"/>
<br/>Query
<ul id="foo"></ul>
<br/>
Result
<ul id="bar"></ul>
</body>
</html>