/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package chartstarter;

import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import com.ptc.services.common.api.IntegrityMessages;
import static com.ptc.services.common.tools.StringUtils.isEmpty;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import jfx.messagebox.MessageBox;
import chartstarter.api.IntegrityCommands;
import chartstarter.model.AdminObject;
import chartstarter.model.Query;

/**
 *
 * @author veckardt
 */
public class ChartStarterController implements Initializable {

   public final static IntegrityMessages MC
           = new IntegrityMessages(ChartStarter.class);
   // String selection1="162,163";
   //
   String selection1 = "591";
   String runMode = "chart";
   //
   //
   private static final Map<String, String> env = System.getenv();
   IntegrityCommands ia = new IntegrityCommands();
   public HashMap<String, AdminObject> reportMap = new HashMap<>();
   private String defaultItemType = "items";
   @FXML
   private Label label;
   @FXML
   public TextField tFilter1;
   @FXML
   private TableView<AdminObject> table = new TableView<>();
   @FXML
   private CheckBox cbRunForAllItems, cbShowAllReports;
   @FXML
   private TableColumn<AdminObject, String> colId, colName, colDescription, colIsAdmin, colQuery;
   private static final ObservableList<AdminObject> tableContentAll
           = FXCollections.observableArrayList();
   private static final ObservableList<AdminObject> tableContentLimited
           = FXCollections.observableArrayList();

   @FXML
   private void handleButtonAction(ActionEvent event) {
      runReport();
   }

   @FXML
   private void runReport(Event event) {
      runReport();
   }

   @FXML
   private void cbRunForAllItems(Event event) {
      if (cbRunForAllItems.isSelected()) {
         label.setText(MC.getMessage("RUN_FOR_ALL").replace("report", runMode));
      } else {
         // label.setText(MC.getMessage("RUN_FOR_SELECTION", defaultItemType, selection1));
         label.setText(MC.getMessage("RUN_FOR_SELECTION").replace("{0}", defaultItemType).replace("{1}", selection1).replace("report", runMode));
      }
   }

   // table.setItems(tableContentLimited); 
   @FXML
   private void cbShowAllReports(Event event) {
      if (cbShowAllReports.isSelected()) {
         table.setItems(tableContentAll);
      } else {
         table.setItems(tableContentLimited);
      }
   }

   private void runReport() {
      final AdminObject selectedRow = (AdminObject) table.getSelectionModel().getSelectedItem();
      if (selectedRow != null) {
         // if ((selectedRow.getName() != null))
         ia.runReport(selectedRow, selection1, !cbRunForAllItems.isSelected());
      }
   }

   @FXML
   private void exitForm(ActionEvent event) {
      // disconnect();
      System.exit(0);
   }

   @Override
   public void initialize(URL url, ResourceBundle rb) {
      try {
         ia.log("initializing ...", 1);
         // cbShowAllReports.setText("Show all " + runMode + "s");

         assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'ReportStarter.fxml'.";
         assert colName != null : "fx:id=\"colName\" was not injected: check your FXML file 'ReportStarter.fxml'.";
         assert colDescription != null : "fx:id=\"colDescription\" was not injected: check your FXML file 'ReportStarter.fxml'.";
         assert colIsAdmin != null : "fx:id=\"colIsAdmin\" was not injected: check your FXML file 'ReportStarter.fxml'.";
         assert cbRunForAllItems != null : "fx:id=\"cbRunForAllItems\" was not injected: check your FXML file 'ReportStarter.fxml'.";
         assert cbShowAllReports != null : "fx:id=\"cbShowAllReports\" was not injected: check your FXML file 'ReportStarter.fxml'.";

         if (!isEmpty(env.get("MKSSI_HOST"))) {

            if ((ia.isEmpty(env.get("MKSSI_ISSUE0"))) && (ia.isEmpty(env.get("MKSSI_DOCUMENT")))) {
               MessageBox.show(ChartStarter.stage,
                       MC.getMessage("AT_LEAST_ONE_ITEM"),
                       "Start Option",
                       MessageBox.ICON_ERROR | MessageBox.OK);
               exitForm(null);
            }

            int issueCnt = Integer.parseInt(env.get("MKSSI_NISSUE"));

            selection1 = env.get("MKSSI_DOCUMENT");
            if (isEmpty(selection1)) {
               selection1 = env.get("MKSSI_ISSUE0");
               for (int j = 2; j <= issueCnt; j++) {
                  selection1 = selection1.concat("," + env.get("MKSSI_ISSUE" + Integer.toString(j - 1)));
               }
            }
         }

         table.setEditable(true);
         table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

         colId.setCellValueFactory(new PropertyValueFactory<AdminObject, String>("Id"));
         colName.setCellValueFactory(new PropertyValueFactory<AdminObject, String>("Name"));
         colDescription.setCellValueFactory(new PropertyValueFactory<AdminObject, String>("Description"));
         colIsAdmin.setCellValueFactory(new PropertyValueFactory<AdminObject, String>("IsAdmin"));
         colQuery.setCellValueFactory(new PropertyValueFactory<AdminObject, String>("Query"));

         ia.log("Selected item is: " + selection1, 1);

         //
         //
         // This solution supports two ways to display the objects to run
         //   a) it reads the objects to display from a type property
         //   b) it determines the objects by parsing the query
         // if b) is set, it will overrule a)
         // Both ways are of interest, b) might be a bit faster when starting
         // the a) requires that the queries used in the charts/reports are as specifc as possible
         // 
         WorkItem wiType = ia.getWorkItem(selection1, "type");
         String typeName = wiType.getField("type").getValueAsString();

         String objectList = ia.getTypePropertyValue(typeName, "Chart.Configurations");
         ia.log(typeName + " => " + objectList, 1);
         if (objectList.isEmpty()) {

            // ia.debugOff();
            ia.initAdminObjectList(runMode + "s");
            ia.initQueryList("");
            ia.initTypeList();
            fillTables(selection1);
         } else {
            defaultItemType = typeName;
            cbRunForAllItems.setVisible(false);
            cbShowAllReports.setVisible(false);
            // 
            ia.setObjectType(runMode + "s");

            // Part 1: Adding non-admin Charts
            Command cmd = new Command(Command.IM, "charts");
            cmd.addOption(new Option("fields", "name,query,description,isAdmin"));
            Response response = ia.executeCmd(cmd);
            WorkItemIterator wit = response.getWorkItems();
            while (wit.hasNext()) {
               WorkItem wi = wit.next();
               String name = wi.getField("name").getValueAsString();
               if (!wi.getField("isAdmin").getBoolean()) {
                  for (String object : objectList.split(",")) {
                     if (name.startsWith(object)) {
                        tableContentAll.add(new AdminObject(0, wi));
                        tableContentLimited.add(new AdminObject(0, wi));
                     }
                  }
               }
            }

            // Part 2: Adding Admin Charts
            cmd = new Command(Command.IM, "charts");
            cmd.addOption(new Option("fields", "name,query,description,isAdmin"));
            for (String object : objectList.split(",")) {
               cmd.addSelection(object);
            }
            // String name = "";
            // String query = "";
            // String description = "";
            // String isAdmin = "";
            try {
               // Response 
               response = ia.executeCmd(cmd);
               // ResponseUtil.printResponse(response,1,System.out);
               for (String object : objectList.split(",")) {
                  WorkItem wi = response.getWorkItem(object);
                  // query = wi.getField("query").getValueAsString();
                  // description = wi.getField("description").getValueAsString();
                  // isAdmin = wi.getField("isAdmin").getBoolean().toString();
                  tableContentAll.add(new AdminObject(0, wi));
                  tableContentLimited.add(new AdminObject(0, wi));
               }
            } catch (APIException ex) {
               Logger.getLogger(ChartStarterController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
               System.exit(2);
            }
         }

         // showAllFields(null); 
         table.setItems(tableContentLimited);
         initFilter(tFilter1, tableContentLimited, table);
         initFilter(tFilter1, tableContentAll, table);

         cbRunForAllItems(null);
      } catch (APIException ex) {
         Logger.getLogger(ChartStarterController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
      }
   }

   /**
    * Look for the given type and class in the query, if found, add to the
    * reportMap
    *
    * @param query
    * @param itemType
    * @param docClass
    * @param report
    */
   public void findTypeInQuery(Query query, String itemType, String docClass, AdminObject report) {
      if (query != null) {

         ia.debug("Query name: '" + query.getName() + "'");
         ia.debug("Query def:  '" + query.getQueryDefinition() + "'");

         String[] analysed = query.getQueryDefinition().split("\"");
         for (int j = 0; j < analysed.length; j++) {
            ia.debug(j + " (qs): " + analysed[j]);
         }

         if ((analysed[0].contains("item.")) && (analysed[0].contains("item." + docClass))) {
            // add to the list if it contains item.segment or item.node
            reportMap.put(report.getName(), report);
         } else if (analysed[0].contains("subquery")) {
            // get the subquery
            analysed[0] = analysed[0].replaceAll("\\]", "\\[");
            analysed = analysed[0].split("\\[");
            for (int j = 0; j < analysed.length; j++) {
               ia.debug(j + " (sq): " + analysed[j]);
            }
            for (int j = 0; j < analysed.length; j = j + 2) {
               if (analysed[j].contains("subquery")) {
                  findTypeInQuery(ia.queryMap.get(analysed[j + 1]), itemType, docClass, report);
               }
            }
         } else if (analysed.length > 2) {
            if (analysed[1].contentEquals("Type") && analysed[3].contentEquals(itemType)) {
               // tableContentAll.add(report);        
               reportMap.put(report.getName(), report);
            }
         }
      }
   }

   /**
    *
    * @param selection
    */
   public void fillTables(String selection) {

      // split the entry list if multiple items
      String[] itemList = selection.split(",");

      //
      // Part 1: Fill the tableContentLimited map
      //
      // clear the output table first, should be empty anyway
      tableContentLimited.clear();

      // for each selected item, check the report queries and add to the 
      for (int k = 0; k < itemList.length; k++) {
         ia.debug(itemList[k]);

         String itemType = ia.getItemType(itemList[k]);
         if (k == 0) {
            defaultItemType = itemType;
         } else if (!itemType.contentEquals(defaultItemType) && (!defaultItemType.contentEquals("items"))) {
            defaultItemType = "items";
         }
         // ia.debug(itemType);

         String docClass = ia.getDocClass(itemType);
         ia.debug(itemType + ", " + docClass);

         for (AdminObject report : ia.reportList) {
            // if add all: tableContentAll.add(report);
            ia.debug("---------------------------------------------------------------");
            ia.debug("Analysing " + runMode + " '" + report.getName() + "' ...");
            // ia.debug(" -> Query is: " + ia.queryMap.get(report.getQuery()).getName());
            ia.debug("---------------------------------------------------------------");
            // String queryDefinition = ia.queryMap.get(report.getQuery()).getQueryDefinition();
            findTypeInQuery(ia.queryMap.get(report.getQuery()), itemType, docClass, report);
         }
      }
      if (!defaultItemType.contentEquals("items") && (itemList.length > 1)) {
         defaultItemType = defaultItemType.concat("s");  // plural
      }
      // Sort the unsorted report list
      Map<String, AdminObject> sortedReportMapLimited = AdminObject.sortByComparator(reportMap);

      // put the list into the form
      for (Map.Entry<String, AdminObject> entry : sortedReportMapLimited.entrySet()) {
         tableContentLimited.add(entry.getValue());
      }

      // add also the item report itself
      tableContentLimited.add(new AdminObject(0, ia.standardItemReportName, ia.standardItemReportDesc, true, "", ""));

      //
      // Part 2: Fill the tableContentAll map
      //
      reportMap.clear();
      // fill All List
      for (AdminObject r : ia.reportList) {
         reportMap.put(r.getName(), r);
      }

      Map<String, AdminObject> sortedReportMapAll = AdminObject.sortByComparator(reportMap);
      for (Map.Entry<String, AdminObject> entry : sortedReportMapAll.entrySet()) {
         tableContentAll.add(entry.getValue());
      }
      tableContentAll.add(new AdminObject(0, ia.standardItemReportName, ia.standardItemReportDesc, true, "", ""));
   }

   /**
    * initFilter
    *
    * @param tFilter
    * @param tableContent
    * @param tableView
    */
   public void initFilter(final TextField tFilter, final ObservableList<AdminObject> tableContent, final TableView<AdminObject> tableView) {
      // tFilter = TextFields.createSearchField();
      // txtField.setPromptText("Filter");
      tFilter.textProperty().addListener(new InvalidationListener() {
         @Override
         public void invalidated(Observable o) {
            if (tFilter.textProperty().get().isEmpty()) {
               tableView.setItems(tableContent);
               return;
            }
            ObservableList<AdminObject> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<AdminObject, ?>> cols = tableView.getColumns();
            for (AdminObject tableContent1 : tableContent) {
               for (int j = 0; j < cols.size(); j++) {
                  TableColumn col = cols.get(j);
                  if (col.getCellData(tableContent1) != null) {
                     String cellValue = col.getCellData(tableContent1).toString();
                     cellValue = cellValue.toLowerCase();
                     if (cellValue.contains(tFilter.textProperty().get().toLowerCase())) {
                        tableItems.add(tableContent1);
                        break;
                     }
                  }
               }
            }
            tableView.setItems(tableItems);
         }
      });
   }
}
