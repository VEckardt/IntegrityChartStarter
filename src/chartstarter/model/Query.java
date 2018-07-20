/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartstarter.model;

/**
 *
 * @author veckardt
 */
public class Query {
    int id;
    String name="";
    String description="";
    String queryDefinition="";
    
    public Query() {
        
    }

    public Query(int id) {
        this.id=id;
    }

    public Query (int id, String name, String description, String queryDefinition) {
        this.id= id;
        this.name= name;
        this.description = description;
        this.queryDefinition=queryDefinition;
    }
    public int getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }

    public String getQueryDefinition() {
        return this.queryDefinition;
    }
    //
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String text) {
        this.name = text;
    }
    public void setDescription(String text) {
        this.description = text;
    }
}