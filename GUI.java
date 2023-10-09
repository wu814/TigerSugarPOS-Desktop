import java.sql.*;
import java.awt.Component;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class GUI extends JFrame implements ActionListener {

    static Connection conn; //database connection

    static JFrame startFrame; 
    static JFrame inventoryFrame;
    static JFrame managerFrame;
    static JFrame cashierFrame;
    static JFrame currFrame; //the current framethat is being used.
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

    public static void setUpInventory(){
        //frame setup
        inventoryFrame = new JFrame("Inventory");
        inventoryFrame.setSize(1000, 800);
        JPanel inventoryPanel = new JPanel();
        inventoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inventoryFrame.add(inventoryPanel);

        JButton backToManager = new JButton("Back to Manager Menu"); //goes back to manager menu
        backToManager.addActionListener(gui);
        inventoryPanel.add(backToManager);

        //getting the data
        String supply = "";
        String stock = "";
        try{
          Statement stmt = conn.createStatement();
          ResultSet result = stmt.executeQuery("SELECT * FROM inventory;");
          while (result.next()) { //initializes employees with info from database, adds to vector
              supply += result.getString("supply") + '\n';
              stock +=result.getString("stock_remaining")+ '\n';
        }
        
        } catch (Exception e){ //errors connecting to database
          JOptionPane.showMessageDialog(null,e);
        }
        JTextArea supplies = new JTextArea(supply);
        JTextArea stocks = new JTextArea(stock);
        inventoryPanel.add(supplies);
        inventoryPanel.add(stocks);
    }
      public static void updateInventory(String ingredient){
        String query = "UPDATE inventory SET stock_remaining = stock_remaining - 1 WHERE supply = " + ingredient + ";";
        try{
          Statement stmt = conn.createStatement();
          stmt.executeQuery(query);
        } catch (Exception e){ //errors connecting to database
          JOptionPane.showMessageDialog(null,e);
        }
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
     
      
      //closing the connection
      try {
        conn.close();
        //JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
       // JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
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
          System.out.println("asdf");
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