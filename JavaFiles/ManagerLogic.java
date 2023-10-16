import java.sql.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.math.BigDecimal;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.util.*;

/**
 * @author Nai-Yun Wu, Josh Hare, Doby Lanete
 */
public class ManagerLogic{
    // Attribute
    private static final String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_10g_db";
    private static final String USER = "csce315_910_williamwu258814";
    private static final String PASSWORD = "password";
    private static Connection conn = null;


    /**
     * Constructor
    */
    public ManagerLogic(){
        // Initialize the connection in the constructor
        try{
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        }catch (SQLException e){
            // Handle connection initialization errors here
            e.printStackTrace();
        }
    }


    /**
    * @param table the table to hold inventory data
    */
    public static void getInventory(JTable table){
        try{
            // Query
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM inventory ORDER BY inventory_id;");

            // Get column names
            int cols = result.getMetaData().getColumnCount();
            Vector<String> colNames = new Vector<>();
            for(int i = 1;i<=cols;i++){
                colNames.add(result.getMetaData().getColumnName(i));
            }

            // Get data
            Vector<Vector<Object>> data = new Vector<>();
            while(result.next()){ 
                Vector<Object> row = new Vector<>();
                for(int i = 1;i<=cols;i++){
                    row.add(result.getObject(i));
                }
                data.add(row);   
            }

            // Table Listener
            DefaultTableModel model = new DefaultTableModel(data,colNames){
                public boolean isCellEditable(int row, int column){
                // Make the menu item column uneditable
                return column != 1 && column != 0;
                }
            };
            table.setModel(model);
            table.getModel().addTableModelListener(new TableModelListener(){
                public void tableChanged(TableModelEvent e){
                    // If it has been changed
                    if(e.getType() == TableModelEvent.UPDATE){
                        // Get changed value and its location
                        int id = e.getFirstRow();
                        int column = e.getColumn();
                        String columnName = model.getColumnName(column);
                        Integer newValue = Integer.parseInt(model.getValueAt(id, column).toString());
                        if(columnName.equals("stock_remaining") || columnName.equals("minimum_stock")){
                            id = (int)model.getValueAt(id,0);
                        }

                        // Update the corresponding database record
                        try{
                            String query = "UPDATE inventory SET " +columnName+ " = ? WHERE inventory_id = ?";
                            PreparedStatement pStat = conn.prepareStatement(query);
                            pStat.setInt(1,newValue);
                            pStat.setInt(2,id);
                            pStat.executeUpdate();
                        }catch(Exception ex){
                            System.out.println(ex);
                        }
                    }
                }
            });
        // Errors connecting to database
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null,e);
        }
    }


    /**
     * Gets a table of the 10 most recent orders
     */
    public static void getRecentOrders(JTable table, JTextArea textArea){
        // Getting the data
        try{
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM orders\r\n" + 
                "ORDER BY order_id DESC\r\n" +
                "LIMIT 10;");

            // Get column names
            int cols = result.getMetaData().getColumnCount();
            Vector<String> colNames = new Vector<>();
            for(int i = 1;i<=cols;i++){
                colNames.add(result.getMetaData().getColumnName(i));
            }

            // Get data
            Vector<Vector<Object>> data = new Vector<>();
            // Put info into rows for the table
            while(result.next()){ 
                Vector<Object> row = new Vector<>();
                for(int i = 1;i<=cols;i++){
                    row.add(result.getObject(i));
                    
                }
                data.add(row);
            }
            DefaultTableModel model = new DefaultTableModel(data,colNames);
            table.setModel(model);
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    int selectedColumn = table.getSelectedColumn();
                    if (selectedRow >= 0 && selectedColumn >= 0) {
                        Object selectedValue = table.getValueAt(selectedRow, selectedColumn);
                        textArea.setText(selectedValue.toString());
                    }
                }
            }
        });

            // Table configuration
            TableColumn column = table.getColumnModel().getColumn(0);
            column.setPreferredWidth(200);
            column = table.getColumnModel().getColumn(1);
            column.setPreferredWidth(400);
            column = table.getColumnModel().getColumn(2);
            column.setPreferredWidth(200);
            column = table.getColumnModel().getColumn(3);
            column.setPreferredWidth(200);
            column = table.getColumnModel().getColumn(4);
            column.setPreferredWidth(900);
            column = table.getColumnModel().getColumn(5);
            column.setPreferredWidth(200);
            column = table.getColumnModel().getColumn(6);
            column.setPreferredWidth(800);
            column = table.getColumnModel().getColumn(7);
            column.setPreferredWidth(800);
            // Adjust the width and height as needed
            table.setPreferredScrollableViewportSize(new Dimension(800, 400)); 
        // Errors connecting to database
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null,e);
        }
    }
    

    /**
    * @param table the table to hold stats data
    * @return the table with stats data loaded
    */
    public static JTable getDailyStats(JTable table){
        try{
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Statement stmt = conn.createStatement();
            ResultSet drinkName = stmt.executeQuery("SELECT drink_name, price FROM products;");

            // Get column names
            Vector<String> colNames = new Vector<>();
            colNames.add("Drink");
            colNames.add("Num Sold");
            colNames.add("Sales");
            Vector<Vector<Object>> data = new Vector<>();
            // Each row: DRINK NAME, NUMSOLD, SALES

            // Track values for the total (final row)
            double totalUnits = 0;
            double totalSales = 0;
            
            // Loop through query
            while(drinkName.next()){ 
                Vector<Object> row = new Vector<>();

                // Fill first column (name)
                Object curr = drinkName.getObject(1);
                row.add(curr);

                // Fill second column (num sold)
                Statement stmt2 = conn.createStatement();
                ResultSet numDrinks= stmt2.executeQuery("SELECT COUNT(*) AS total FROM orders WHERE '"+(String)curr+"' = ANY (order_items) AND DATE(order_timestamp) = '"+timestamp+"'; ");
                numDrinks.next();
                double units = numDrinks.getDouble(1);
                totalUnits += units;
                row.add(numDrinks.getObject(1));

                // Fill third column (sales)
                double price = drinkName.getDouble(2);
                double sales = price * units;
                totalSales += sales;
                row.add((Object)sales);

                data.add(row);
            }
            // Add total row
            Vector<Object> totalRow = new Vector<>();
            totalRow.add("Total");
            totalRow.add((int)totalUnits);
            totalRow.add(totalSales);
            data.add(totalRow);

            // Table model setup
            DefaultTableModel model = new DefaultTableModel(data,colNames);
            table.setModel(model);
            // Adjust the width and height as needed
            table.setPreferredScrollableViewportSize(new Dimension(800, 400)); 
        // Errors connecting to database
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null,e);
        }
        return table;
    }

    /**
    * @param table the table to hold stats data
    * @return the table with stats data loaded
    */
    public static JTable getCustomRange(JTable table, String start, String end){
        try{
            //generate list of all dates to look at
            



            Statement stmt = conn.createStatement();
            ResultSet drinkName = stmt.executeQuery("SELECT drink_name, price FROM products;");

            // Get column names
            Vector<String> colNames = new Vector<>();
            colNames.add("Drink");
            colNames.add("Num Sold");
            colNames.add("Sales");
            Vector<Vector<Object>> data = new Vector<>();
            // Each row: DRINK NAME, NUMSOLD, SALES

            // Track values for the total (final row)
            double totalUnits = 0;
            double totalSales = 0;
            
            // Loop through query
            while(drinkName.next()){ 
                Vector<Object> row = new Vector<>();

                // Fill first column (name)
                Object curr = drinkName.getObject(1);
                row.add(curr);

                double units = 0;
                Statement stmt0 = conn.createStatement();
                String getSeries = "SELECT generate_series (DATE_TRUNC('day', '"+start+" 00:00:00'::TIMESTAMP),DATE_TRUNC('day', '"+end+" 23:59:59'::TIMESTAMP), INTERVAL '1 day')::DATE AS day;";
                ResultSet series = stmt0.executeQuery(getSeries);
                while(series.next()){
                    Object timestamp = series.getObject(1);
                    // Fill second column (num sold)
                    Statement stmt2 = conn.createStatement();
                    ResultSet numDrinks= stmt2.executeQuery("SELECT COUNT(*) AS total FROM orders WHERE '"+(String)curr+"' = ANY (order_items) AND DATE(order_timestamp) = '"+timestamp+"'; ");
                    numDrinks.next();
                    units = numDrinks.getDouble(1);
                    totalUnits += units;
                    row.add(numDrinks.getObject(1));
                }

                // Fill third column (sales)
                double price = drinkName.getDouble(2);
                double sales = price * units;
                totalSales += sales;
                row.add((Object)sales);

                data.add(row);
                System.out.println(totalUnits);

            }
            // Add total row
            Vector<Object> totalRow = new Vector<>();
            totalRow.add("Total");
            totalRow.add((int)totalUnits);
            totalRow.add(totalSales);
            data.add(totalRow);

            // Table model setup
            DefaultTableModel model = new DefaultTableModel(data,colNames);
            table.setModel(model);
            // Adjust the width and height as needed
            table.setPreferredScrollableViewportSize(new Dimension(800, 400)); 
        // Errors connecting to database
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null,"You have entered an invalid date.\n     Try Again.");
        }
        return table;
    }



    /**
    * @param table the table to hold products
    */
    public static void getMenu(JTable table){
        try{
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM products ORDER BY product_id;");

            // Get column names
            int cols = result.getMetaData().getColumnCount();
            Vector<String> colNames = new Vector<>();
            for(int i = 1;i<=cols;i++){
                colNames.add(result.getMetaData().getColumnName(i));
            }

            // Get data
            Vector<Vector<Object>> data = new Vector<>();
            while(result.next()){ 
                Vector<Object> row = new Vector<>();
                for(int i = 1;i<=cols;i++){
                    row.add(result.getObject(i));
                }
                data.add(row);
            }

            // Setup table listener
            DefaultTableModel model = new DefaultTableModel(data,colNames){
                public boolean isCellEditable(int row, int column) {
                // Make the menu item column uneditable
                return column != 3 && column != 0;
                }
            };
            table.setModel(model);
            table.getModel().addTableModelListener(new TableModelListener(){
                public void tableChanged(TableModelEvent e){
                    // If a value has been changed
                    if(e.getType() == TableModelEvent.UPDATE){
                        // Get location and newvalue
                        int id = e.getFirstRow();
                        int column = e.getColumn();
                        String columnName = model.getColumnName(column);
                        String query = "UPDATE products SET " +columnName+ " = ? WHERE product_id = ?";
                        
                        // Update the corresponding database record
                        try{
                            PreparedStatement pStat = conn.prepareStatement(query);

                            // Define behaivor for each column
                            if(columnName.equals("price")){
                                Double newValue = Double.parseDouble(model.getValueAt(id, column).toString());
                                id = (int)model.getValueAt(id,0);
                                pStat.setBigDecimal(1,BigDecimal.valueOf(newValue));
                                pStat.setInt(2,id);
                            }
                            else if(columnName.equals("drink_name") || columnName.equals("drink_type")){
                                String newValue = (model.getValueAt(id, column).toString());
                                id = (int)model.getValueAt(id,0);
                                pStat.setString(1,newValue);
                                pStat.setInt(2,id);
                            }
                            
                            
                            pStat.executeUpdate();
                        }catch (Exception ex){
                            System.out.println("HELP"+ex);
                        }
                    }
                }
            });
        // Errors connecting to database
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null,e);
        }
    }


    /**
     * @param currFrame the frame of the pop out window
     */
    public static void addMenuItem(JFrame currFrame){
        try{
            // Create a statement object
            //TODO add input validation
            TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter new menu item","Enter price");
            TwoInputs inputs = dialog.showInputDialog();
            String newDrink = inputs.input1;
            try{
                Double newPrice = Double.parseDouble(inputs.input2); 
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null, "You have entered an invalid price.\nTry Again.", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Vector<String> ings = new Vector<>();
            Double newPrice = Double.parseDouble(inputs.input2); 

            // Get ingredients
            //TODO add input validation
            try{
                 Integer ingredientCount = Integer.parseInt(JOptionPane.showInputDialog("How many ingredients does this drink have?"));
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null, "You have entered an invalid ingredient.\nTry Again.", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Integer ingredientCount = Integer.parseInt(JOptionPane.showInputDialog("How many ingredients does this drink have?"));
            for(int i = 0;i<ingredientCount;i++){
                // For each ingredient:
                String ingredient = JOptionPane.showInputDialog("Enter an ingredient");
                ings.add(ingredient);
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery("SELECT * FROM inventory WHERE supply = '"+ingredient+"';");
                // If supply is not in the inventory
                if(!result.next()){ 
                    System.out.println(ingredient);
                    Statement stmt2 = conn.createStatement();
                    //add a new supply
                    try{ 
                        stmt2.executeQuery("INSERT INTO inventory (inventory_id, supply, stock_remaining) VALUES (DEFAULT, '"+ingredient+"', 100);");
                    }catch(Exception ex){ }
                }
            }
            //TODO Add Input Validation
            String drinkType = JOptionPane.showInputDialog("Enter drink type");

            // Convert vector to an array
            String[] ingredients = ings.toArray(new String[0]);
        
            //prep new query to insert new item onto menu
            String query = "INSERT INTO products (product_id, drink_name, price, ingredients, drink_type) VALUES (DEFAULT, ?, ?, ?, ?);";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,newDrink);
            preparedStatement.setDouble(2,newPrice);

            preparedStatement.setArray(3,conn.createArrayOf("text",ingredients));
            preparedStatement.setString(4,drinkType);
            preparedStatement.executeUpdate();
        // Errors connecting to database
        }catch(Exception ex){ 
            System.out.println(ex);   
        }
    }


    /**
     * Remove an product from the menu
     */
    public static void removeMenuItem(){
        try{
            // Get input and execute a query
            Integer item = Integer.parseInt(JOptionPane.showInputDialog("Enter ID of object to be removed"));
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("DELETE FROM products WHERE product_id = "+item+";");
        // Errors connecting to database
        }catch (Exception ex){ 
            JOptionPane.showMessageDialog(null,ex);
        }
    }


    /**
     * Add supply item to database
     * @param currFrame the frame of the pop out window
     */
    public static void addSupplyItem(JFrame currFrame){
        try{
            // Gets the inputs with the two input dialog
            TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter new supply","Enter amount of new stock");
            TwoInputs inputs = dialog.showInputDialog();
            String newSupply = inputs.input1;
            Integer newStock = Integer.parseInt(inputs.input2);

            // Query
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("INSERT INTO inventory (inventory_id, supply, stock_remaining) VALUES (DEFAULT, '"+newSupply+"', "+newStock+");");
        // Errors connecting to database
        }catch (Exception ex){ 
            JOptionPane.showMessageDialog(null,ex);
        }
    }


    /**
     * Remove a supply item from database
     */
    public static void removeSupplyItem(){
        try{
            // Gets the id of the object to remove
            Integer item = Integer.parseInt(JOptionPane.showInputDialog("Enter ID of object to be removed"));
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("DELETE FROM inventory WHERE inventory_id = "+item+";");
        // Errors connecting to database
        }catch (Exception ex){ 
            JOptionPane.showMessageDialog(null,ex);
        }
    }


    /**
     * Load the restock report to the table
     * @param table the table holding the restock report
     */
    public static void getRestockReport(JTable table){
        // Getting the data
        try{
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT supply, stock_remaining, minimum_stock FROM inventory\r\n" +
                "WHERE stock_remaining < minimum_stock;");

            // Get column names
            Vector<String> colNames = new Vector<>();
            colNames.add("Supply");
            colNames.add("Stock Remaining");
            colNames.add("Minimum Stock");
            // Get column names
            int cols = result.getMetaData().getColumnCount();

            Vector<Vector<Object>> data = new Vector<>();
            
            while(result.next()){ 
                Vector<Object> row = new Vector<>();
                for(int i = 1;i<=cols;i++){
                    row.add(result.getObject(i));
                }
                data.add(row);   
            }

            // Table Listener
            DefaultTableModel model = new DefaultTableModel(data,colNames){
                public boolean isCellEditable(int row, int column){
                // Make the menu item column uneditable
                return column != 1 && column != 0;
                }
            };
            table.setModel(model);
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null,e);
        }
    }

    public static void getExcessReport(JTable table, Timestamp timestamp){
        
    }
    

}
