package org.agfjord.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class SolrConnector {
	String url = "http://clouddiscoverprod1.corp.findwise.net:8080/solr/main";
	SolrServer server;

	public SolrConnector() {
		server = new HttpSolrServer(url);
	}

	public SolrDocumentList query(String query) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery(query);
		QueryResponse queryResp = server.query(solrQuery);
		SolrDocumentList docs = queryResp.getResults();
		return docs;
	}

	public String queryDebug(String query) throws SolrServerException, IOException {		
		query = query.replace(" ", "+").replace("+*", "*").replace("*+", "*");
		query = query.replace("AND", "+AND+");
		query = query.replace("OR", "+OR+");
		return getFile(url + "/" + query);
	}
	
	public String getFile(String url) throws IOException {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();

        return response.toString();
    }
}
