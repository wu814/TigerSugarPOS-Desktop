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


public class GUI extends JFrame implements ActionListener {

    static Connection conn; //database connection

    static JFrame startFrame; 
    static JFrame inventoryFrame; 
    static JFrame managerFrame; //manager view menu
    static JFrame cashierFrame;
    static JFrame recentFrame; //recent orders frame
    static JFrame statsFrame;
    static JFrame currFrame; //the current framethat is being used.
    static JTable statsTable; //stats table
    static JFrame editorFrame;
    static GUI gui;
    static JPanel p; 
    static JTextArea hello; //text area for testing
    static JComboBox<Employee> employeeSelector; //drop down for employees, how we know to go in cashier view or  manager view
    static JButton employeeEnter;//locks in combobox entry
    static JButton backToLogin; //back button that returns to employee select
    static JButton payButton;
    static JPanel orderLogs;
    static ArrayList<String> order = new ArrayList<String>();
    static OrderLogic orderLogic = new OrderLogic();
    static double orderTotal = 0.0;
    static Map<String, Double> drinkPriceMap = new HashMap<String, Double>();
    static JScrollPane orderScrollPane;


    //establishes connection to the database, through the conn variable
    public static void connect(){
      conn = null;
      try {
        conn = DriverManager.getConnection(
          "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_10g_db",
          "csce315_910_dlanete",
          "password");
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      }
    }

    //change frame to a new frame, use when switching menus
    public static void changeFrame(JFrame newFrame){
      currFrame.setVisible(false);
      currFrame = newFrame;
      currFrame.setVisible(true);
    }

    //Initialize variables and components necessary for the GUI
    public static void startFrameSetup(){
      //frame setup
      startFrame = new JFrame("Tiger Sugar POS");
      startFrame.setSize(1000, 800);
      startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //initalize componenets
      gui = new GUI();
      p = new JPanel();
      hello = new JTextArea();

      startFrame.add(p);
      p.add(hello);

      //ComboBox for Employees
      setEmployeeComboBox();
      p.add(employeeSelector);

      //setup enter button
      employeeEnter = new JButton("Enter"); 
      employeeEnter.addActionListener(gui);
      
      p.add(employeeEnter);
      currFrame = startFrame;
      currFrame.setVisible(true);
    }
    //CREATES THE INVENTORY FRAME AND READS IN FROM DATABASE
    public static void setUpInventory(){
        //frame setup
        inventoryFrame = new JFrame("Inventory");
        inventoryFrame.setSize(1000, 800);
        JPanel inventoryPanel = new JPanel();
        inventoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inventoryFrame.add(inventoryPanel);

        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Inventory");
        titlePanel.add(title);

        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        inventoryFrame.add(titlePanel,BorderLayout.NORTH);
        inventoryFrame.add(menuPanel,BorderLayout.CENTER);
        inventoryFrame.add(inventoryPanel,BorderLayout.SOUTH);

        JButton backToManager = new JButton("Back to Manager Menu"); //goes back to manager menu
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        JButton add = new JButton("Add Supply Item");
        add.addActionListener(gui);
        menuPanel.add(add);

        JButton remove = new JButton("Remove Supply Item");
        remove.addActionListener(gui);
        menuPanel.add(remove);

        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        inventoryPanel.add(scroll);

        //getting the data
        try{
          Statement stmt = conn.createStatement();
          ResultSet result = stmt.executeQuery("SELECT * FROM inventory ORDER BY inventory_id;");

          //get column names
          int cols = result.getMetaData().getColumnCount();
          Vector<String> colNames = new Vector<>();
          for(int i = 1;i<=cols;i++){
            colNames.add(result.getMetaData().getColumnName(i));
          }

          //get data
          Vector<Vector<Object>> data = new Vector<>();
          while (result.next()) { //initializes employees with info from database, adds to vector
              Vector<Object> row = new Vector<>();
              for(int i = 1;i<=cols;i++){
                row.add(result.getObject(i));
              }
              data.add(row);
      
          }
          DefaultTableModel model = new DefaultTableModel(data,colNames);
          table.setModel(model);
          table.getModel().addTableModelListener(new TableModelListener(){

              public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int id = e.getFirstRow();
                 //   System.out.println(id);
                    int column = e.getColumn();
                    String columnName = model.getColumnName(column);
                    Integer newValue = Integer.parseInt(model.getValueAt(id, column).toString());
                    if(columnName.equals("stock_remaining")){
                      id = (int)model.getValueAt(id,0);
                    }
                //    System.out.println(id);

                    // Update the corresponding database record
                   // updateDatabase(row, columnName, newValue);
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

        
        } catch (Exception e){ //errors connecting to database
          JOptionPane.showMessageDialog(null,e);
        }
        inventoryFrame.pack();

    }

    //CREATES Recent orders Frame
    public static void setUpRecentOrders(){
        recentFrame = new JFrame("RecentOrders");
        recentFrame.setSize(1000, 800);

        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Recent Orders");
        titlePanel.add(title);

        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        JPanel recentPanel = new JPanel();
        recentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        recentFrame.add(titlePanel,BorderLayout.NORTH);
        recentFrame.add(menuPanel,BorderLayout.CENTER);
        recentFrame.add(recentPanel,BorderLayout.SOUTH);

        JButton backToManager = new JButton("Back to Manager Menu"); //goes back to manager menu
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);


        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        recentPanel.add(scroll);

        //getting the data
        try{
          Statement stmt = conn.createStatement();
          ResultSet result = stmt.executeQuery("SELECT * FROM orders\r\n" + //
              "ORDER BY order_id DESC\r\n" + //
              "LIMIT 10;");

          //get column names
          int cols = result.getMetaData().getColumnCount();
          Vector<String> colNames = new Vector<>();
          for(int i = 1;i<=cols;i++){
            colNames.add(result.getMetaData().getColumnName(i));
          }

          //get data
          Vector<Vector<Object>> data = new Vector<>();
          while (result.next()) { //initializes employees with info from database, adds to vector
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
          table.setPreferredScrollableViewportSize(new Dimension(800, 400)); // Adjust the width and height as needed
        
        } catch (Exception e){ //errors connecting to database
          JOptionPane.showMessageDialog(null,e);
        }

        recentFrame.pack();
    }

    //STATS FRAME
    public static void setUpOrderStats(){
        statsFrame = new JFrame("Order Statistics");
        statsFrame.setSize(1000, 800);

        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Order Statistics");
        titlePanel.add(title);

        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        JPanel statsPanel = new JPanel();
        statsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        statsFrame.add(titlePanel,BorderLayout.NORTH);
        statsFrame.add(menuPanel,BorderLayout.CENTER);
        statsFrame.add(statsPanel,BorderLayout.SOUTH);

        JButton backToManager = new JButton("Back to Manager Menu"); //goes back to manager menu
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        JButton today = new JButton("Daily Stats"); //shows today's stats
        today.addActionListener(gui);
        menuPanel.add(today);

        JButton custom = new JButton("Custom Range"); //shows custom stats
        custom.addActionListener(gui);
        menuPanel.add(custom);


        statsTable = dailyStats();
        JScrollPane  scroll = new JScrollPane(statsTable);
        statsPanel.add(scroll);

        //getting the data
        
        statsFrame.pack();
    }

    public static JTable dailyStats(){
      JTable table = new JTable();
      try{
          Statement stmt = conn.createStatement();
          ResultSet drinkName = stmt.executeQuery("SELECT drink_name, price FROM products;");

          //get column names
          Vector<String> colNames = new Vector<>();
          colNames.add("Drink");
          colNames.add("Num Sold");
          colNames.add("Sales");
          Vector<Vector<Object>> data = new Vector<>();
          //Each row: DRINK NAME, NUMSOLD, SALES

          double totalUnits = 0;
          double totalSales = 0;

          
          while (drinkName.next()) { 
              Vector<Object> row = new Vector<>();

              //FILL FIRST COL
              Object curr = drinkName.getObject(1);
              row.add(curr);

              //FILL SECOND COL
              Statement stmt2 = conn.createStatement();
              ResultSet numDrinks= stmt2.executeQuery("SELECT COUNT(*) AS total FROM orders WHERE '"+(String)curr+"' = ANY (order_items) AND DATE(order_timestamp) = '2025-06-01'; ");
              numDrinks.next();
              double units = numDrinks.getDouble(1);
              totalUnits += units;
              row.add(numDrinks.getObject(1));

              //FILL THIRD COL
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

         
          table.setPreferredScrollableViewportSize(new Dimension(800, 400)); // Adjust the width and height as needed
        
        } catch (Exception e){ //errors connecting to database
          JOptionPane.showMessageDialog(null,e);
        }
        return table;

    }

    public static void setUpMenuEditor(){
              //frame setup
        editorFrame = new JFrame("Menu Editor");
        editorFrame.setSize(1000, 800);
        JPanel editorPanel = new JPanel();
        editorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        editorFrame.add(editorPanel);

        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Menu Editor");
        titlePanel.add(title);

        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        editorFrame.add(titlePanel,BorderLayout.NORTH);
        editorFrame.add(menuPanel,BorderLayout.CENTER);
        editorFrame.add(editorPanel,BorderLayout.SOUTH);

        JButton backToManager = new JButton("Back to Manager Menu"); //goes back to manager menu
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        JButton add = new JButton("Add Menu Item");
        add.addActionListener(gui);
        menuPanel.add(add);

        JButton remove = new JButton("Remove Menu Item");
        remove.addActionListener(gui);
        menuPanel.add(remove);

        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        editorPanel.add(scroll);

        //getting the data
        try{
          Statement stmt = conn.createStatement();
          ResultSet result = stmt.executeQuery("SELECT * FROM products ORDER BY product_id;");

          //get column names
          int cols = result.getMetaData().getColumnCount();
          Vector<String> colNames = new Vector<>();
          for(int i = 1;i<=cols;i++){
            colNames.add(result.getMetaData().getColumnName(i));
          }

          //get data
          Vector<Vector<Object>> data = new Vector<>();
          while (result.next()) { 
              Vector<Object> row = new Vector<>();
              for(int i = 1;i<=cols;i++){
                row.add(result.getObject(i));
              }
              data.add(row);
      
          }
          DefaultTableModel model = new DefaultTableModel(data,colNames);
          table.setModel(model);
          table.getModel().addTableModelListener(new TableModelListener(){

              public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
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
                   }catch (Exception ex){
                      System.out.println("HELP"+ex);
                   }
                 }
              }
            });

        
        } catch (Exception e){ //errors connecting to database
          JOptionPane.showMessageDialog(null,e);
        }
        editorFrame.pack();
    }

    public static Timestamp getCurrentTime(){
          return new Timestamp(System.currentTimeMillis());
    }



    public static void setEmployeeComboBox(){
      //loads in the names of the employees
      Vector<Employee> employees = new Vector<>();
      
      try{
        //create a statement object
        Statement stmt = conn.createStatement();
        //send statement to DBMS
        ResultSet result = stmt.executeQuery("SELECT * FROM employees;");
        while (result.next()) { //initializes employees with info from database, adds to vector
            employees.add(new Employee(
              result.getString("first_name"),
              result.getString("position"),
              result.getString("wage"),
              result.getString("hours_worked")
            ));
            
        }
        
      } catch (Exception e){ //errors connecting to database
        JOptionPane.showMessageDialog(null,e);
      }
      employeeSelector = new JComboBox<Employee>(employees);

      //configures combobox options to be the employee names
      employeeSelector.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Employee) {
                Employee employee = (Employee) value;
                value = employee.getName(); // Display the "name" attribute inthe combobox
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
      });
      
    }
    //updates order history
    // public static void addOrderToDatabase(String timestamp,String employee, String customer, String[] items){
    //     String query = "INSERT INTO orders (order_timestamp, employee_id, customer_id, order_items, order_total) VALUES (" + timestamp + ", " + employee + ", " + customerID + ", " + ARRAY['Item1', 'Item2', 'Item3'] + ", " + total + ");";
    //     try{
    //       Statement stmt = conn.createStatement();
    //       stmt.executeQuery(query);
    //     } catch (Exception e){ //errors connecting to database
    //       JOptionPane.showMessageDialog(null,e);
    //     }
    // }

    public static void main(String[] args)
    {
      //Connect to Database
      connect();

      //Setup Start Frame
      startFrameSetup();
      setUpInventory();
      setUpRecentOrders();
      setUpOrderStats();
      setUpMenuEditor();
      
      //closing the connection
      // try {
      //   conn.close();
      //   //JOptionPane.showMessageDialog(null,"Connection Closed.");
      // } catch(Exception e) {
      //  // JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      // }
    }

    // if button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String event = e.getActionCommand();

        //Employee Enter in Combo Box
        if (event.equals("Enter")) {
            viewSelector(((Employee) employeeSelector.getSelectedItem()).isManager());
        }

        //Returns to Login from cashier page or manager menu
        if(event.equals("Back to Login")){
          changeFrame(startFrame);
        }

        //Returns to manager menu from view inventory, edit prices, order stats, and recent orders
        if(event.equals("Back to Manager Menu")){
          changeFrame(managerFrame);
        }

        //opens inventory page
        if(event.equals("View Inventory")){
          
          changeFrame(inventoryFrame);
        }

        //opens price editor
        if(event.equals("Edit Prices")){
          changeFrame(editorFrame);
        }
        
        //opens order stats
        if(event.equals("Order Statistics")){
          changeFrame(statsFrame);
        }

        //opens recent orders
        if(event.equals("Recent Orders")){
          changeFrame(recentFrame);
        }

        //on order stats page, shows daily stats
        if(event.equals("Daily Stats")){
          dailyStats();
        }

        //on order stats page, show stats for inputted range, input with TwoInputDialog
        if(event.equals("Custom Range")){
          TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter start date: YYYY-MM-DD","Enter end date: YYYY-MM-DD");
          TwoInputs inputs = dialog.showInputDialog();
          String start = inputs.input1;
          String end = inputs.input2;
          if(start != "" && end != ""){
              System.out.println("VALID");
          }
          else{
            JOptionPane.showMessageDialog(null, "You have entered an invalid date.", "ERROR", JOptionPane.INFORMATION_MESSAGE);
          }
        }

        //on inventory page, adds a supply item to the database
        if(event.equals("Add Supply Item")){
          try{
            //create a statement object
            TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter new supply","Enter amount of new stock");
            TwoInputs inputs = dialog.showInputDialog();
            String newSupply = inputs.input1;
            Integer newStock = Integer.parseInt(inputs.input2);
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("INSERT INTO inventory (inventory_id, supply, stock_remaining) VALUES (DEFAULT, '"+newSupply+"', "+newStock+");");
        
          }  catch (Exception ex){ //errors connecting to database
            //JOptionPane.showMessageDialog(null,ex);
          }
          setUpInventory();
        }

        //on inventory page, removes a supply item from the database
        if(event.equals("Remove Supply Item")){
          try{
            Integer item = Integer.parseInt(JOptionPane.showInputDialog("Enter ID of object to be removed"));
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("DELETE FROM inventory WHERE inventory_id = "+item+";");
          }catch (Exception ex){ //errors connecting to database
            //JOptionPane.showMessageDialog(null,ex);
          }
          setUpInventory();
        }

        //on menu editor page, adds a menu item to the database
        if(event.equals("Add Menu Item")){
          try{
            //create a statement object
            TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter new menu item","Enter price");
            TwoInputs inputs = dialog.showInputDialog();
            String newDrink = inputs.input1;
            Double newPrice = Double.parseDouble(inputs.input2); //WARNING: MUST BE between 0 and 9.99
            Vector<String> ings = new Vector<>();
            //get ingredients
            Integer ingredientCount = Integer.parseInt(JOptionPane.showInputDialog("How many ingredients does this drink have?"));
            for(int i = 0;i<ingredientCount;i++){
              String ingredient = JOptionPane.showInputDialog("Enter an ingredient");
              ings.add(ingredient);
              Statement stmt = conn.createStatement();
              ResultSet result = stmt.executeQuery("SELECT * FROM inventory WHERE supply = '"+ingredient+"';");
              if(!result.next()){ //if supply is not in the inventory
                System.out.println(ingredient);
                Statement stmt2 = conn.createStatement();
                ResultSet adsf = stmt2.executeQuery("INSERT INTO inventory (inventory_id, supply, stock_remaining) VALUES (DEFAULT, '"+ingredient+"', 100);");
              }

            }

            String[] ingredients = ings.toArray(new String[0]);
           
            String query = "INSERT INTO products (product_id, drink_name, price, ingredients) VALUES (DEFAULT, ?, ?, string_to_array(?, ', '));";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,newDrink);
            preparedStatement.setDouble(2,newPrice);

            String ing = String.join(",",ingredients);
            preparedStatement.setString(3,ing);
            preparedStatement.executeUpdate();
            System.out.println(ing);
        
          }  catch (Exception ex){ //errors connecting to database
            System.out.println(ex);   
           }
          setUpMenuEditor();
        }
        if(event.equals("Remove Menu Item")){
          try{
            Integer item = Integer.parseInt(JOptionPane.showInputDialog("Enter ID of object to be removed"));
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("DELETE FROM products WHERE product_id = "+item+";");
          }catch (Exception ex){ //errors connecting to database
            //JOptionPane.showMessageDialog(null,ex);
          }
          setUpMenuEditor();
        }

        
        
      
    }

    //displays either the cashier view or the manager view based on combobox selection
    public static void viewSelector(boolean manager){
      //startFrame.setVisible(false);
      if(manager){ //go to manager view
        //setup manager frame
        managerFrame = new JFrame("Manager Display");
        managerFrame.setSize(1000, 800);
        JPanel managerPanel = new JPanel();
        managerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        managerFrame.add(managerPanel);

        JButton backToLogin = new JButton("Back to Login"); //goes back to login
        backToLogin.addActionListener(gui);
        managerPanel.add(backToLogin);

        JButton viewInventory = new JButton("View Inventory"); //open view inventory menu
        viewInventory.addActionListener(gui);
        managerPanel.add(viewInventory);

        JButton editPrices = new JButton("Edit Prices"); //open edit prices menu
        editPrices.addActionListener(gui);
        managerPanel.add(editPrices);

        JButton orderStats = new JButton("Order Statistics"); //open order stats
        orderStats.addActionListener(gui);
        managerPanel.add(orderStats);

        JButton recentOrders = new JButton("Recent Orders"); //open recent orders
        recentOrders.addActionListener(gui);
        managerPanel.add(recentOrders);

         //switch frame
        changeFrame(managerFrame);
      }
      else{ //go to cashier view
        cashierFrame = new JFrame("Cashier Display");
        cashierFrame.setSize(300, 100);
        cashierFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(gui);
        cashierFrame.add(backToLogin);
        changeFrame(cashierFrame);
      }
    }
}