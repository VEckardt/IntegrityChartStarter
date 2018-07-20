/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartstarter.api;

import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.ptc.services.common.api.Command;
import com.ptc.services.common.api.IntegrityAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import chartstarter.ChartStarterController;
import chartstarter.model.AdminObject;
import chartstarter.model.Query;
import chartstarter.model.Type;

/**
 *
 * @author veckardt
 */
public class IntegrityCommands extends IntegrityAPI {

   private boolean debugFlag = true;
   static Map<String, String> env = System.getenv();
   public ArrayList<AdminObject> reportList = new ArrayList();
   // public ArrayList<Query> queryList = new ArrayList( );
   public HashMap<String, Query> queryMap = new HashMap<>();
   public HashMap<String, Type> typeMap = new HashMap<>();
   public String standardItemReportName = "Standard Item Report";
   public String standardItemReportDesc = "Integrity Standard Item Report";
   String objectType;

   public IntegrityCommands() {
      super(env, "IntegrityChartStarter");
   }

   /**
    * Returns the document class for a specific itemType
    *
    * @param itemType
    * @return the doc class
    */
   @Override
   public String getDocClass(String itemType) {
      return typeMap.get(itemType).getDocumentClass();
   }

   public void setObjectType(String objectType) {
      this.objectType = objectType;
   }

   /**
    * Creates a Field List from the current installation with four columns:
    * id,name,displayname,type
    * @param objectType
    */
   public void initAdminObjectList(String objectType) {

      this.objectType = objectType;
      Command cmd = new Command(Command.IM, objectType);
      cmd.addOption(new Option("fields", "id,name,description,query,sharedGroups,isAdmin"));
      WorkItem wi;
      try {
         Response response = this.executeCmd(cmd);
         // ResponseUtil.printResponse(response,1,System.out);
         WorkItemIterator wii = response.getWorkItems();
         while (wii.hasNext()) {
            wi = wii.next();
            AdminObject report = new AdminObject(
                    wi.getField("id").getInteger(),
                    wi.getField("name").getString(),
                    wi.getField("description").getString(),
                    wi.getField("isAdmin").getBoolean(),
                    wi.getField("query").getValueAsString(),
                    wi.getField("sharedGroups").getValueAsString());

            reportList.add(report);
            // log (wi.getField("displayname").getString());
         }
         debug("Rows in reportList: " + reportList.size());
      } catch (APIException ex) {
         Logger.getLogger(ChartStarterController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   /**
    * Creates a Field List from the current installation with four columns:
    * id,name,displayname,type
    * @param queryName
    */
   public void initQueryList(String queryName) {

      if (queryName != null && !queryName.isEmpty()) {
         if (queryMap.containsKey(queryName)) {
            return;
         }
      }
      Command cmd = new Command(Command.IM, "queries");
      cmd.addOption(new Option("fields", "id,name,description,queryDefinition"));
      WorkItem wi;
      if (queryName != null && !queryName.isEmpty()) {
         cmd.addSelection(queryName);
      }
      try {
         Response response = this.executeCmd(cmd);
         // ResponseUtil.printResponse(response,1,System.out);
         WorkItemIterator wii = response.getWorkItems();
         while (wii.hasNext()) {
            wi = wii.next();
            queryName = wi.getField("name").getValueAsString();
            queryMap.put(queryName, new Query(
                    wi.getField("id").getInteger(),
                    queryName,
                    wi.getField("description").getValueAsString(),
                    wi.getField("queryDefinition").getValueAsString()));
            // log (wi.getField("displayname").getString());
         }
         debug("Rows in queryMap: " + queryMap.size());
      } catch (APIException ex) {
         Logger.getLogger(ChartStarterController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   /**
    * Creates a Field List from the current installation with four columns:
    * id,name,displayname,type
    */
   public void initTypeList() {

      Command cmd = new Command(Command.IM, "types");
      cmd.addOption(new Option("fields", "id,name,description,documentClass"));
      WorkItem wi;
      try {
         Response response = this.executeCmd(cmd);
         // ResponseUtil.printResponse(response,1,System.out);
         WorkItemIterator wii = response.getWorkItems();
         while (wii.hasNext()) {
            wi = wii.next();
            String typeName = wi.getField("name").getValueAsString();
            typeMap.put(typeName, new Type(
                    wi.getField("id").getInteger(),
                    typeName,
                    wi.getField("description").getValueAsString(),
                    wi.getField("documentClass").getValueAsString()));
            // log (wi.getField("displayname").getString());
         }
         debug("Rows in typeMap: " + typeMap.size());
      } catch (APIException ex) {
         Logger.getLogger(ChartStarterController.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   /**
    * Creates a Field List from the current installation with four columns:
    * id,name,displayname,type
    * @param report
    * @param selection
    * @param singleItemMode
    */
   public void runReport(AdminObject report, String selection, boolean singleItemMode) {

      String reportName = report.getName();
      String selectionArray[] = selection.split(",");
      // Command cmd;

      if (reportName.contentEquals(standardItemReportName)) {
         for (String selectionArray1 : selectionArray) {
            Command cmd = new Command(Command.IM, "printissue");
            cmd.addOption(new Option("g"));
            cmd.addSelection(selectionArray1);
            try {
               Response response = this.executeCmd(cmd);
               // ResponseUtil.printResponse(response,1,System.out);
            } catch (APIException ex) {
               Logger.getLogger(ChartStarterController.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      } else if (objectType.contentEquals("reports")) {
         Command cmd = new Command(Command.IM, "runreport");
         if (singleItemMode) {
            cmd.addOption(new Option("issues", selection));
         }
         cmd.addOption(new Option("g"));
         cmd.addSelection(reportName);
         // WorkItem wi;
         try {
            Response response = this.executeCmd(cmd);
            // ResponseUtil.printResponse(response,1,System.out);
         } catch (APIException ex) {
            Logger.getLogger(ChartStarterController.class.getName()).log(Level.SEVERE, null, ex);
         }
      } else {
         Command cmd = new Command(Command.IM, "runchart");
            // if (singleItemMode) {
         //     cmd.addOption(new Option("issues", selection));
         // }
         cmd.addOption(new Option("g"));
         cmd.addSelection(reportName);
         // WorkItem wi;
         try {
            debug("Query: " + report.getQuery());
            initQueryList(report.getQuery());
            Query query = queryMap.get(report.getQuery());
            debug("Query Definition: " + query.getQueryDefinition());
            String fieldname = (query.getQueryDefinition().contains("meaningful") ? "Document ID" : "ID");

            //queryDefinition = ((field["ID"] = 999) and (subquery[All Documents]))
            if (singleItemMode) {
               cmd.addOption("queryDefinition", "((field[" + fieldname + "] = " + selection + ") and (subquery[" + report.getQuery() + "]))");
            }

            Response response = this.executeCmd(cmd);
            // ResponseUtil.printResponse(response,1,System.out);
         } catch (APIException ex) {
            Logger.getLogger(ChartStarterController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                // MessageBox.show(ReportStarter.stage,
            //         ReportStarterController.MC.getMessage("CHART_RUN_ERROR").replace("{0}", ex.getMessage()),
            //         "Start Option",
            //         MessageBox.ICON_ERROR | MessageBox.OK);                
         }
      }
   }

   public void debugOn() {
      debugFlag = true;
   }

   public void debug(String text) {
      if (debugFlag) {
         log(text, 1);
      }
   }
}
