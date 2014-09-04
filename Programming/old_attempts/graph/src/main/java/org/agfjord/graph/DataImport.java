package org.agfjord.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

public class DataImport {

	private SolrServer treesServer = new HttpSolrServer("http://localhost:8983/solr/trees");
	private SolrServer namesServer = new HttpSolrServer("http://localhost:8983/solr/names");
	private int rows = 100 ;
	
	public List<String> fetchName(String type) throws SolrServerException{
		SolrQuery query = new SolrQuery();
		query.setQuery("type:" + type);
		query.setRows(rows);
		int start = 0;
		QueryResponse rsp;
		List<String> expertises;
		do{
			query.setStart(start);
			rsp = namesServer.query(query);
			expertises = new ArrayList<String>();
			for(SolrDocument doc : rsp.getResults()){
				String name = (String)doc.get("name");
				if(name.length() < 30){
					expertises.add(name);					
				}
			}
			start += rows;
		} while(start <= rsp.getResults().getNumFound());
		return expertises;
	}
	
}
