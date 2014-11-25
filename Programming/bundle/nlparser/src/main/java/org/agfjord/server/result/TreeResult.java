package org.agfjord.server.result;

import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.beans.Field;
/**
 *
 * @author per.fredelius
 */
public class TreeResult {

    @Field("linearizations")
    List<String> linearizations;

    
    @Field("length")
    Integer length;
    
    @Field("lang")
    String lang;
    
    @Field("*_i")
    private Map<String, Integer> nameCounts;
    
    /*@Field("Skill_i")
    Integer skill_count;
    @Field("Organization_i")
    Integer organization_count;
    @Field("Location_i")
    Integer location_count;
    @Field("Module_i")
    Integer module_count;*/
            
    
    public List<String> getLinearizations() {
        return linearizations;
    }

    public void setLinearizations(List<String> linearizations) {
        this.linearizations = linearizations;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Map<String, Integer> getNameCounts() {
        return nameCounts;
    }

    public void setNameCounts(Map<String, Integer> nameCounts) {
        this.nameCounts = nameCounts;
    }
}
