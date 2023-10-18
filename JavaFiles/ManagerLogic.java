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
        }catch(SQLException e){
            // Handle connection initialization errors here
            e.printStackTrace();
        }
    }


    /**
    * Loads the inventory data from database to a table
    * @param table the table to hold inventory data
    * @return
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
     * @param table the table to hold recent orders
     * @param textArea the text area to hold the order details
     * @return
     */
    public static void getRecentOrders(JTable table, JTextArea textArea){
        // Getting the data
        try{
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM orders ORDER BY order_id DESC LIMIT 10;");

            // Get column names
            int cols = result.getMetaData().getColumnCount();
            Vector<String> colNames = new Vector<>();
            for(int i = 1;i<=cols;i++){
                colNames.add(result.getMetaData().getColumnName(i));
            }

            // Get data
            Vector<Vector<Object>> data = new Vector<>();

            // Initializes employees with info from database, adds to vector
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
    * Loads the sales data from database to a table
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
    * Get the data within the specified range of time
    * @param table the table to hold stats data
    * @param start the start of the range
    * @param end the end of the range
    * @return the table with stats data loaded
    */
    public static JTable getCustomRange(JTable table, String start, String end){
        try{
            // Generate list of all dates to look at
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
    * Loads the products data from database to a table
    * @param table the table to hold products
    * @return 
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
                    return column != 3 && column != 0 && column != 4;

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
                        }catch(Exception ex){
                            System.out.println("HELP"+ex);
                        }
                    }
                }
            });
            TableColumn column = table.getColumnModel().getColumn(0);
            column.setPreferredWidth(125);
            column = table.getColumnModel().getColumn(1);
            column.setPreferredWidth(400);
            column = table.getColumnModel().getColumn(2);
            column.setPreferredWidth(100);
            column = table.getColumnModel().getColumn(3);
            column.setPreferredWidth(800);
            column = table.getColumnModel().getColumn(4);
            column.setPreferredWidth(300);
            table.setPreferredScrollableViewportSize(new Dimension(800, 400));
        // Errors connecting to database
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null,e);
        }
    }


    /**
     * Add a new item to menu
     * @param currFrame the frame of the pop out window
     * @return 
     */
    public static void addMenuItem(JFrame currFrame){
        try{
            // Create a statement object
            TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter new menu item","Enter price");
            TwoInputs inputs = dialog.showInputDialog();
            String newDrink = inputs.input1;
            Statement stmt0 = conn.createStatement();
            ResultSet result0 = stmt0.executeQuery("SELECT * FROM products WHERE drink_name = '"+newDrink+"';");
                // If supply is not in the inventory
            if(result0.next()){ 
                JOptionPane.showMessageDialog(null,"A drink by the same name is already on the menu");
                return;
            }


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
            String input = JOptionPane.showInputDialog("How many ingredients does this drink have?");
            try{
                 Integer ingredientCount = Integer.parseInt(input);
                 if(ingredientCount <=0){
                    JOptionPane.showMessageDialog(null, "A drink must have at least 1 ingredient.", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                    return;
                 }
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null, "You have entered an invalid amount of ingredients.\nTry Again.", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Integer ingredientCount = Integer.parseInt(input);

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
                    // Add a new supply
                    try{ 
                        stmt2.executeQuery("INSERT INTO inventory (inventory_id, supply, stock_remaining) VALUES (DEFAULT, '"+ingredient+"', 100);");
                    }catch(Exception ex){}
                }
            }

            String drinkType = JOptionPane.showInputDialog("Enter drink type");
            if(!drinkType.equals("Seasonal Drinks") && !drinkType.equals("Sweet and Creamy") && !drinkType.equals("Fruity and Refreshing") && !drinkType.equals( "Coffee Flavored")){
                JOptionPane.showMessageDialog(null, "Drink type must be one of the following:\nFruity and Refreshing\nSweet and Creamy\nCoffee Flavored\nSeasonal Drinks", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Convert vector to an array
            String[] ingredients = ings.toArray(new String[0]);
        
            // Prep new query to insert new item onto menu
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
     * @return
     */
    public static void removeMenuItem(){
        String input = JOptionPane.showInputDialog("Enter ID of object to be removed");
        try{
            // Get input and execute a query
            try{
                Integer item = Integer.parseInt(input);
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null,"Invalid ID.\n Try Again.");
                return;
            }
            Integer item = Integer.parseInt(input);

            Statement stmt0 = conn.createStatement();
            ResultSet result = stmt0.executeQuery("SELECT * FROM products WHERE product_id = '"+item+"';");
                // If supply is not in the inventory
            if(!result.next()){ 
                JOptionPane.showMessageDialog(null,"Product not found in database.");
                return;
            }

            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("DELETE FROM products WHERE product_id = "+item+";");
        // Errors connecting to database
        }catch (Exception ex){ }
    }


    /**
     * Add supply item to database
     * @param currFrame the frame of the pop out window
     * @return
     */
    public static void addSupplyItem(JFrame currFrame){
        try{
            // Gets the inputs with the two input dialog
            TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter new supply","Enter amount of new stock");
            TwoInputs inputs = dialog.showInputDialog();
            String newSupply = inputs.input1;

            Statement stmt0 = conn.createStatement();
            ResultSet result = stmt0.executeQuery("SELECT * FROM inventory WHERE supply = '"+newSupply+"';");
                // If supply is not in the inventory
            if(result.next()){ 
                JOptionPane.showMessageDialog(null,"Supply Already Exists in Inventory");
                return;
            }

            try{
                Integer newStock = Integer.parseInt(inputs.input2);
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null,"Invalid Stock Amount.\n Try Again.");
                return;
            }


            Integer newStock = Integer.parseInt(inputs.input2);

            // Query
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("INSERT INTO inventory (inventory_id, supply, stock_remaining,minimum_stock) VALUES (DEFAULT, '"+newSupply+"', "+newStock+",100);");
        // Errors connecting to database
        }catch (Exception ex){ 
           // JOptionPane.showMessageDialog(null,ex);
       
        }
    }


    /**
     * Remove a supply item from database
     * @return
     */
    public static void removeSupplyItem(){
        String input = JOptionPane.showInputDialog("Enter ID of object to be removed");
        try{
            // Gets the id of the object to remove
            try{
                Integer item = Integer.parseInt(input);
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null,"Invalid ID.\n Try Again.");
                return;
            }
            Integer item = Integer.parseInt(input);

            Statement stmt0 = conn.createStatement();
            ResultSet result = stmt0.executeQuery("SELECT * FROM inventory WHERE inventory_id = '"+item+"';");
                // If supply is not in the inventory
            if(!result.next()){ 
                JOptionPane.showMessageDialog(null,"Supply not found in database.");
                return;
            }

            Statement stmt = conn.createStatement();
            stmt.executeQuery("DELETE FROM inventory WHERE inventory_id = "+item+";");
        // Errors connecting to database
        }catch (SQLException ex){ 

        }
    }


    /**
     * Load the restock report to the table
     * @param table the table holding the restock report
     * @return
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
    
    /**
     * Load the "excess report" to the table
     * @param table the table holding the excess report
     * @param timestamp the timestamp to start the report from
     * @return
     */
    public static void getExcessReport(JTable table, Timestamp timestamp){
        System.out.println("Excess Rans");
        try {


            Timestamp currTimestamp = new Timestamp(System.currentTimeMillis());
            Statement stmt = conn.createStatement();
            Statement stmt2 = conn.createStatement();
            String sqlHistoryQuery = "SELECT " +
                "SUM(sago) AS sago_count, " +
                "SUM(cups_xl) AS cups_xl_count, " +
                "SUM(cups_regular_hot) AS cups_regular_hot_count, " +
                "SUM(grass_jelly) AS grass_jelly_count, " +
                "SUM(crystal_jelly) AS crystal_jelly_count, " +
                "SUM(mango_milk_cream) AS mango_milk_cream_count, " +
                "SUM(black_sugars) AS black_sugars_count, " +
                "SUM(aloe_vera_bits) AS aloe_vera_bits_count, " +
                "SUM(straws_jumbo) AS straws_jumbo_count, " +
                "SUM(brown_sugar) AS brown_sugar_count, " +
                "SUM(black_sugar) AS black_sugar_count, " +
                "SUM(lids_dome) AS lids_dome_count, " +
                "SUM(strawberry_milk_cream) AS strawberry_milk_cream_count, " +
                "SUM(condiment_station_supplies) AS condiment_station_supplies_count, " +
                "SUM(matcha) AS matcha_count, " +
                "SUM(fresh_milk) AS fresh_milk_count, " +
                "SUM(tapioca_pearls_boba) AS tapioca_pearls_boba_count, " +
                "SUM(tiger_pearls) AS tiger_pearls_count, " +
                "SUM(cream_mousse) AS cream_mousse_count, " +
                "SUM(taro) AS taro_count, " +
                "SUM(red_beans) AS red_beans_count, " +
                "SUM(pudding) AS pudding_count, " +
                "SUM(mochi) AS mochi_count, " +
                "SUM(jasmine_green_tea_leaves) AS jasmine_green_tea_leaves_count, " +
                "SUM(passion_fruit_tea_leaves) AS passion_fruit_tea_leaves_count, " +
                "SUM(lychee_jelly) AS lychee_jelly_count, " +
                "SUM(oat_milk) AS oat_milk_count, " +
                "SUM(strawberry_mango) AS strawberry_mango_count, " +
                "SUM(oolong_tea_leaves) AS oolong_tea_leaves_count, " +
                "SUM(straws_regular) AS straws_regular_count, " +
                "SUM(lids_flat) AS lids_flat_count, " +
                "SUM(napkins_regular) AS napkins_regular_count, " +
                "SUM(to_go_bags_small) AS to_go_bags_small_count, " +
                "SUM(cups_regular) AS cups_regular_count, " +
                "SUM(soy_milk) AS soy_milk_count, " +
                "SUM(lactose_free_milk) AS lactose_free_milk_count " +
                "FROM inventory_history " +
                "WHERE order_timestamp BETWEEN '" + timestamp + "' AND '" + currTimestamp + "'";


            String sqlInventoryQuery = "SELECT supply, stock_remaining FROM inventory";


            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Inventory_item");
            tableModel.addColumn("Amount");
            tableModel.addColumn("Inventory Amount at Date");

            ResultSet resultHistorySet = stmt.executeQuery(sqlHistoryQuery);
            ResultSet inventoryResultSet = stmt2.executeQuery(sqlInventoryQuery);

            Map<String, Integer> inventoryMap = new HashMap<>();
            
            while (inventoryResultSet.next()) {
                String supply = inventoryResultSet.getString("supply").replace(" ", "").replace("_", "").replace(")", "").replace("(", "").replace("-", "").toLowerCase();
                int stockRemaining = inventoryResultSet.getInt("stock_remaining");
                
                // Put the data into the HashMap
                inventoryMap.put(supply, stockRemaining);
            }


            String[] itemNames = {
                "sago", "cups_xl", "cups_regular_hot", "grass_jelly", "crystal_jelly",
                "mango_milk_cream", "black_sugars", "aloe_vera_bits", "straws_jumbo",
                "brown_sugar", "black_sugar", "lids_dome", "strawberry_milk_cream",
                "condiment_station_supplies", "matcha", "fresh_milk", "tapioca_pearls_boba",
                "tiger_pearls", "cream_mousse", "taro", "red_beans", "pudding", "mochi",
                "jasmine_green_tea_leaves", "passion_fruit_tea_leaves", "lychee_jelly",
                "oat_milk", "strawberry_mango", "oolong_tea_leaves", "straws_regular",
                "lids_flat", "napkins_regular", "to_go_bags_small", "cups_regular",
                "soy_milk", "lactose_free_milk"
            };
            

            while (resultHistorySet.next()){
                for (String itemName : itemNames) {
                    inventoryResultSet.next();
                    Object[] rowData = new Object[3]; // Create a new rowData array for each row
                    rowData[0] = itemName;
                    if (resultHistorySet.getString(itemName + "_count") == null) {
                        rowData[1] = 0;
                    } else {
                        rowData[1] = resultHistorySet.getString(itemName + "_count");
                    }
                    
                    String data1 = itemName.replace(" ", "").replace("_", "").replace(")", "").replace("(", "").replace("-", "").toLowerCase();
                    rowData[2] = inventoryMap.get(data1) + Integer.parseInt(rowData[1].toString());
                    if (Integer.parseInt(rowData[1].toString()) == Integer.parseInt(rowData[2].toString())) {
                        System.out.println(rowData[0] + " is out of stock");
                    } else if (Double.parseDouble(rowData[1].toString()) / Double.parseDouble(rowData[2].toString()) <= 0.1) {
                        System.out.println(rowData[0] + " had excess = " + (Double.parseDouble(rowData[1].toString()) / Double.parseDouble(rowData[2].toString())));
                        tableModel.addRow(rowData);
                    } else {
                        System.out.println(rowData[0] + " was used and there was not much excess = " + (Double.parseDouble(rowData[1].toString()) / Double.parseDouble(rowData[2].toString())));
                        //tableModel.addRow(rowData);
                    }
                    
                }
            }
            
            table.setModel(tableModel);

            stmt.close();
            stmt2.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Load the "pairs of items" that sell togeher to the table
     * @param table the table holding what sales together
     * @param beginTimestamp the begin timestamp
     * @param endTimestamp the end timestamp
     * @return
     */
    public static void getWhatSalesTogether(JTable table, Timestamp beginTimestamp, Timestamp endTimestamp) {
        String whatSalesTogetherQuery = "WITH OrderItems AS (\n" +
            "   SELECT DISTINCT\n" +
            "       unnest(order_items) AS item,\n" +
            "       order_timestamp\n" +
            "   FROM orders WHERE order_timestamp BETWEEN ? AND ?\n" +
            ")\n" +
            "SELECT\n" +
            "   a.item AS item1,\n" +
            "   b.item AS item2,\n" +
            "   COUNT(*) AS frequency\n" +
            "FROM\n" +
            "   OrderItems a\n" +
            "JOIN\n" +
            "   OrderItems b ON a.order_timestamp = b.order_timestamp AND a.item < b.item\n" +
            "GROUP BY\n" +
            "   item1, item2\n" +
            "ORDER BY\n" +
            "   frequency DESC\n";
        
        try (PreparedStatement preparedStatement = conn.prepareStatement(whatSalesTogetherQuery)) {
            // Set the parameters
            preparedStatement.setTimestamp(1, beginTimestamp);
            preparedStatement.setTimestamp(2, endTimestamp);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Item 1");
            tableModel.addColumn("Item 2");
            tableModel.addColumn("Frequency");

            // Process the result set
            while (resultSet.next()) {
                String item1 = resultSet.getString("item1");
                String item2 = resultSet.getString("item2");
                int frequency = resultSet.getInt("frequency");

                Object[] rowData = new Object[]{
                    item1, item2, frequency
                };

                tableModel.addRow(rowData);
            }

            table.setModel(tableModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        

    }
}
