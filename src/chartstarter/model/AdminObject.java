/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartstarter.model;

import com.mks.api.response.WorkItem;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author veckardt
 */
public class AdminObject {

    int id;
    String name = "";
    String description = "";
    String query = "";
    String sharedGroups = "";
    Boolean isAdmin;

    public AdminObject() {

    }

    public AdminObject(int id) {
        this.id = id;
    }

    public AdminObject(int id, WorkItem wi) {
        this.id = id;
        this.name = wi.getField("name").getValueAsString();
        try {
            this.query = wi.getField("query").getValueAsString();
        } catch (Exception ex) {
            this.query = "";
        }
        this.description = wi.getField("description").getValueAsString();
        this.isAdmin = wi.getField("isAdmin").getBoolean();
    }

    public AdminObject(int id, String name, String description, Boolean isAdmin, String query, String sharedGroups) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.query = query;
        this.sharedGroups = sharedGroups;
        this.isAdmin = isAdmin;
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

    public String getIsAdmin() {
        return this.isAdmin ? "   X" : "";
    }

    public String getQuery() {
        return this.query;
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

    public void setIsAdmin(Boolean text) {
        this.isAdmin = text;
    }

    /**
     * Allows to sort a hash map by using LinkedHashMap
     *
     * @param unsortMap
     * @return the sorted map
     */
    public static Map<String, AdminObject> sortByComparator(Map<String, AdminObject> unsortMap) {

        List<Map.Entry<String, AdminObject>> list = new LinkedList<Map.Entry<String, AdminObject>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, AdminObject>>() {
            @Override
            public int compare(Map.Entry<String, AdminObject> o1,
                    Map.Entry<String, AdminObject> o2) {
                return o1.getValue().getName().compareTo(o2.getValue().getName());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, AdminObject> sortedMap = new LinkedHashMap<String, AdminObject>();
        for (Map.Entry<String, AdminObject> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
