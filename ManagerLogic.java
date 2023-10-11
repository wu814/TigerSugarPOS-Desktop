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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.util.*;

public class ManagerLogic {
    // Attribute
    private static final String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_10g_db";
    private static final String USER = "csce315_910_williamwu258814";
    private static final String PASSWORD = "password";
    Connection conn = null;


    /**
     * Constructor
     * @author Nai-Yun Wu
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
    * @author Nai-Yun Wu
    * @param table the table that holds the inventory data
    */
    public void getInventory(JTable table){
        try{
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM inventory ORDER BY inventory_id;");

            //get column names
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
            table.getModel().addTableModelListener(new TableModelListener(){
                public void tableChanged(TableModelEvent e){
                    if(e.getType() == TableModelEvent.UPDATE){
                        int id = e.getFirstRow();
                        int column = e.getColumn();
                        String columnName = model.getColumnName(column);
                        Integer newValue = Integer.parseInt(model.getValueAt(id, column).toString());
                        if(columnName.equals("stock_remaining")){
                        id = (int)model.getValueAt(id,0);
                        }
                        try{
                            String query = "UPDATE inventory SET " +columnName+ " = ? WHERE inventory_id = ?";
                            PreparedStatement pStat = conn.prepareStatement(query);
                            pStat.setInt(1,newValue);
                            pStat.setInt(2,id);
                            pStat.executeUpdate();
                        }catch (Exception ex){
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
    * @author Nai-Yun Wu
    * @param table the table that holds the orders data
    */
    public void getRecentOrders(JTable table){
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
            // Adjust the width and height as needed
            table.setPreferredScrollableViewportSize(new Dimension(800, 400)); 
        // Errors connecting to database
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null,e);
        }
    }


    /**
    * @author Nai-Yun Wu
    * @param table the table that holds the stats
    * @return the table after getting the stats
    */
    public JTable getDailyStats(JTable table){
        try{
            Statement stmt = conn.createStatement();
            ResultSet drinkName = stmt.executeQuery("SELECT drink_name, price FROM products;");

            // Get column names
            Vector<String> colNames = new Vector<>();
            colNames.add("Drink");
            colNames.add("Num Sold");
            colNames.add("Sales");
            Vector<Vector<Object>> data = new Vector<>();
            // Each row: DRINK NAME, NUMSOLD, SALES

            double totalUnits = 0;
            double totalSales = 0;

            
            while(drinkName.next()){ 
                Vector<Object> row = new Vector<>();

                // Fill first col
                Object curr = drinkName.getObject(1);
                row.add(curr);

                // FILL second col
                Statement stmt2 = conn.createStatement();
                ResultSet numDrinks= stmt2.executeQuery("SELECT COUNT(*) AS total FROM orders WHERE '"+(String)curr+"' = ANY (order_items) AND DATE(order_timestamp) = '2025-06-01'; ");
                numDrinks.next();
                double units = numDrinks.getDouble(1);
                totalUnits += units;
                row.add(numDrinks.getObject(1));

                // Fill third col
                double price = drinkName.getDouble(2);
                double sales = price * units;
                totalSales += sales;
                row.add((Object)sales);

                data.add(row);
            }
            Vector<Object> totalRow = new Vector<>();
            totalRow.add("Total");
            totalRow.add((int)totalUnits);
            totalRow.add(totalSales);
            data.add(totalRow);

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
    * @author Nai-Yun Wu
    * @param table the table that holds the products
    * @return the table after getting the products
    */
    public void getMenu(JTable table){
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
            DefaultTableModel model = new DefaultTableModel(data,colNames);
            table.setModel(model);
            table.getModel().addTableModelListener(new TableModelListener(){
                public void tableChanged(TableModelEvent e){
                    if(e.getType() == TableModelEvent.UPDATE){
                        int id = e.getFirstRow();
                        int column = e.getColumn();
                        String columnName = model.getColumnName(column);
                        Double newValue = Double.parseDouble(model.getValueAt(id, column).toString());
                        if(columnName.equals("price")){
                            id = (int)model.getValueAt(id,0);
                        }
                        // Update the corresponding database record
                        try{
                            String query = "UPDATE products SET " +columnName+ " = ? WHERE product_id = ?";
                            PreparedStatement pStat = conn.prepareStatement(query);
                            pStat.setBigDecimal(1,BigDecimal.valueOf(newValue));
                            pStat.setInt(2,id);
                            pStat.executeUpdate();
                        }catch(Exception ex){
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
}
