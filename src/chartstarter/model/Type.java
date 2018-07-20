/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartstarter.model;

/**
 *
 * @author veckardt
 */
public class Type {
    int id;
    String name="";
    String description="";
    String documentClass="";
    
    public Type() {
        
    }

    public Type(int id) {
        this.id=id;
    }

    public Type (int id, String name, String description, String documentClass) {
        this.id= id;
        this.name= name;
        this.description = description;
        this.documentClass=documentClass;
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

    public String getDocumentClass() {
        return this.documentClass;
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