import java.sql.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
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
    static GUI gui;
    static JPanel p; 
    static JTextArea hello; //text area for testing
    static JComboBox<Employee> employeeSelector; //drop down for employees, how we know to go in cashier view or  manager view
    static JButton employeeEnter;//locks in combobox entry


    //establishes connection to the database, through the conn variable
    public static void connect(){
      conn = null;
      try {
        conn = DriverManager.getConnection(
          "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_10g_db",
          "csce315_910_jmhhare",
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

        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        inventoryPanel.add(scroll);

        //getting the data
        try{
          Statement stmt = conn.createStatement();
          ResultSet result = stmt.executeQuery("SELECT * FROM inventory;");

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
              row.add(numDrinks.getObject(1));

              //FILL THIRD COL
              double price = drinkName.getDouble(2);
              double sales = price * units;
              row.add((Object)sales);

              data.add(row);
      
          }
          DefaultTableModel model = new DefaultTableModel(data,colNames);
          table.setModel(model);

         
          table.setPreferredScrollableViewportSize(new Dimension(800, 400)); // Adjust the width and height as needed
        
        } catch (Exception e){ //errors connecting to database
          JOptionPane.showMessageDialog(null,e);
        }
        return table;

    }

      //DECREAES STOCK BY 1 FOR AN INGREDIENT
    public static void updateInventory(String ingredient){
        String query = "UPDATE inventory SET stock_remaining = stock_remaining - 1 WHERE supply = " + ingredient + ";";
        try{
          Statement stmt = conn.createStatement();
          stmt.executeQuery(query);
        } catch (Exception e){ //errors connecting to database
          JOptionPane.showMessageDialog(null,e);
        }
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

        //Employee Enter
        if (event.equals("Enter")) {
            viewSelector(((Employee) employeeSelector.getSelectedItem()).isManager());
        }
        if(event.equals("Back to Login")){
          changeFrame(startFrame);
        }
        if(event.equals("Back to Manager Menu")){
          changeFrame(managerFrame);
        }
        if(event.equals("View Inventory")){
          
          changeFrame(inventoryFrame);
        }
        if(event.equals("Edit Prices")){
          System.out.println("asdf");
        }
        if(event.equals("Order Statistics")){
          changeFrame(statsFrame);
        }
        if(event.equals("Recent Orders")){
          changeFrame(recentFrame);
        }
        if(event.equals("Daily Stats")){
          //dailyStats()
        }
        if(event.equals("Custom Range")){
          //custom range
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