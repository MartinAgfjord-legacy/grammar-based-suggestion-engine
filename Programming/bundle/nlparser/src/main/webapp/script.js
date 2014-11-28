
var AUTOCOMPLETE_MAXLENGTH = 10;

function syntaxHighlight(json) {
    if (typeof json != 'string') {
         json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&amp;');//.replace(/</g, '&lt;').replace(/>/g, '&gt;');
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
    initExampleInstrucs();
    initHashChange();
    initWordCompletion();
});

function initHashChange(){
    $(window).on('hashchange', function () {
        parseFromURL();
    });
}

/*
 * Activate word completion by using jQuery UI
 */
function initWordCompletion(){
    $("#input")
      // don't navigate away from the field on tab when selecting an item
      .bind( "keydown", function( event ) {
          $('#input').removeClass('grammar-search');
        if ( event.keyCode === $.ui.keyCode.TAB &&
            $( this ).data( "ui-autocomplete" ).menu.active ) {
          event.preventDefault();
        }
      })
      .autocomplete({
        autoSelect: true,
        minLength: 0,
        autoFocus: true,
        source: function( request, response ) {
          var succFun = function(data) {
              var suggestions = $.map( data, function(item) {
              return {
                 label: item.linearizations[0]
              };
            })
            // 
            response( suggestions.slice(0,AUTOCOMPLETE_MAXLENGTH) );
          };
          ajaxRequest('completeSentence', $('#input').val(), $('#language').val(), succFun, function(){});
        },
        // Always search for completions
        search: function(){
            return true;
        },
        // Do not complete words when selected in the list
        focus: function() {
          return false;
        },
        /* Add the selected word to the query
         * There exists 3 cases for completing the word w:
         * (1) 'w_i'  ==> 'ws '
         * (2) 'w'  ==> 'w z '
         * (3) 'w ' ==> 'w z '
         * where ws and z are completion words.
         */
        select: function( event, ui ) {
            console.log(ui);
                this.value = ui.item.value;
            // Always append whitespace after a completion word
            return false;
        },
      });
      $("#input").css('width','700px').css('display','block');
}

/*
 * Reads a query from the URL in the browser and adds it to the input selector
 */
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

/*
 * Adds the query to the URL in the browser
 */
function addQueryToURL(query){
    var uri = new URI(window.location.href);
    uri.hash('?q=' + query.replace(/ /g, '+'));
    window.location.href = uri.toString();
}

/*
 * Add an event-listener to perform a query
 * when enter key is pushed.
 */
function addEventListener(){
    $(document).keypress(function(e) {
        if(e.which == 13) {
            var query = $('#input').val();
            //If last character is whitespace, remove it
            if(query[query.length-1] == ' '){
                query = query.slice(0,-1);
            }
            addQueryToURL(query);
            parse(query);
        }
    });
}

/*
 * Add onClick-events to the example buttons.
 */
function initExampleInstrucs(){
    $('.question').each(function(){
        var query = $(this).text();
        $(this).click(function(){
            addQueryToURL(query);
            parseFromURL();
        });
    });
}

/*
 * Parse a question formulated in english into one or more abstract syntax trees and their linearizations.
 */
function parse(query){
    var successFun = function(response) {
            var html;
            var fetchedResult = false;
            if(typeof response.err != 'undefined'){
               html = '<span class="error">An error occurred near the word </span>' + '<span class="error-word">' + response.err + '</span>';
               $('#input').removeClass('grammar-search');
                // If not parseable by the grammar,
                // fallback to regular search
                fetchResult("select?&defType=edismax&qf=name^2%20WORKS_WITH%20WORKS_IN%20KNOWS&wt=json&q="+query);
            }
            else{
                /*
                 * For all linearizations where the language is 'SimpleSolr', link the query to the corresponding solr-query (also ' ' ===> '+').
                 */
                var firstAst = true;
                i = 0;
                if($(response).size() > 1 && false){
                    $("#ambiguous_result").css('display','block');
                } else{
                    $("#ambiguous_result").css('display','none');
                }
                $("[id^=ast]").remove();
                $(response).each(function(){
                    $(this.linearizations).each(function(){
                        var host = getHost();
                        if(this.language == 'InstrucsAmbig'){
                            $("#ambiguous_result").append('<p id="' + 'ast' + i + '">' + this.query + '</p>');
                        }
                        if(this.language == 'InstrucsSolr'){
                            solrQuery = this.query.replace(/ /g,'+');
                            this.query = '<a href="' + '/solr-instrucs/relations/' + solrQuery + '">' + this.query + '</a>';
                            if(!fetchedResult){
                                $("#ast" + i).css('font-weight','bold');
                                $("#ast" + i).append(' (this was executed)')
                                fetchResult(solrQuery);
                                $('#input').addClass('grammar-search');
                                fetchedResult = true;
                            }
                        }
                    });
                    i++;
                });
                var str = JSON.stringify(response, undefined, 4);
                //Replace e.g. "ast" : "Direct_Q (MkSymb \"Java\")" with 'ast' : 'Direct_Q (MkSymb "Java")'
                str = str.replace(/\\"/g, '\\');
                str = str.replace(/\"/g, "'");
                str = str.replace(/\\/g, '"');
                html = syntaxHighlight(str);
            }
            $('#grammar_result').empty().append(html);
        };
    var errFun = function(request, status, error) {
        $('#grammar_result').empty().append(status);
            
    };
    ajaxRequest('parse', query, $('#language').val(),  successFun, errFun);
}

function fetchResult(solrQuery){
    var successFun = function(response){
        renderSolrResult(response);
        var str = JSON.stringify(response, undefined, 4);
        //Replace e.g. "ast" : "Direct_Q (MkSymb \"Java\")" with 'ast' : 'Direct_Q (MkSymb "Java")'
        str = str.replace(/\\"/g, '\\');
        str = str.replace(/\"/g, "'");
        str = str.replace(/\\/g, '"');
        html = syntaxHighlight(str);
        console.log(html);
        console.log(response);
        $("#search_result_json").empty().append(html);
    }
    var errFun = function(request, status, error) {
        console.log("Solr request error:");
        console.log(error);
    };
    
    solrAjaxRequest('/' + solrQuery, successFun, errFun);
}


var DocumentTemplate;
$(function () {
    // Compile the document template using underscorejs
    DocumentTemplate = Handlebars.compile($('#doc_template').html());
    
    
    // Debug testing of a document!
    /*$("#search_result").empty()
            .append(DocumentTemplate({
                "id": "66",
                "object_type": "Person",
                "WORKS_WITH": [
                    "BRIS",
                    "Emmaus",
                    "Röda Korset",
                    "Djurens Rätt",
                    "Amnesty",
                    "Musikhjälpen",
                    "Radiohjälpen",
                    "Naturskyddsföreningen",
                    "Erikshjälpen",
                    "Unicef"
                ],
                "WORKS_IN": [
                    "Gothenburg",
                    "Stockholm"
                ],
                "name": "Test Person",
                "KNOWS": [
                    "Erlang",
                    "MATLAB",
                    "Mathematica",
                    "Lisp",
                    "ColdFusion"
                ],
                "_version_": 1485197488527769600
            }));*/
    
});

function renderSolrResult (response) {
    var docs = response.response.docs;
    $("#search_result").empty().append($.map(docs,function (doc, ix) {
        
        return DocumentTemplate(doc);
    }));
}

/*
 * Helper function to fetch current hostname and port
 */
function getHost(){
    var uri = new URI(window.location.href);
    if(uri.protocol() == 'file'){
        return 'localhost:8080';
    }else {
        return uri.hostname() + ':' + uri.port();
    }
}

/*
 * Helper function to make ajax requests
 */
function ajaxRequest(path, query, language, successFun, errFun){
    var host = getHost();
    $.ajax({
        url: 'http://' + host + '/nlparser/api/' + path,
        jsonp: "callback",
        dataType: "jsonp",
        data: {
            q: query,
            lang: language,
            format: "jsonp"
        },
        success: successFun,
        error: errFun
    });
}

function solrAjaxRequest(path, successFun, errFun){
    $.ajax({
        url: '/solr-instrucs/relations' + path,
        success: successFun,
        dataType:'json',
        error: errFun
    });
}