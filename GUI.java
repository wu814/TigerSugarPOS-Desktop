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
    static JTable statsTable; //stats table
    static JFrame editorFrame;
    static JFrame currFrame; //the current framethat is being used.
    static JFrame prevFrame;
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
      prevFrame = currFrame;
      currFrame = newFrame;
      currFrame.setVisible(true);
    }

    //Initialize variables and components necessary for the GUI
    public static void frameSetup(){
      //frame setup
      startFrame = new JFrame("Tiger Sugar POS");
      startFrame.setSize(1000, 800);
      startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //initalize components
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

      //Setup Frame
      frameSetup();
      setUpInventory();
      setUpRecentOrders();
      setUpOrderStats();
      setUpMenuEditor();
     
      
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
            // caching drink prices
            drinkPriceMap = OrderLogic.fetchAllDrinkPrices();

            viewSelector(((Employee) employeeSelector.getSelectedItem()).isManager());
        }

        //Returns to Login from cashier page or manager menu
        if(event.equals("Back to Login")){
          changeFrame(startFrame);
        }
        else if (event.equals("Fruity and Refreshing")) {
          System.out.println("FAR");
          changeFrame(createFruityRefreshingPage());
        }
        else if (event.equals("Sweet and Creamy")) {
          System.out.println("SAC");
          changeFrame(createSweetAndCreamyPage());
        }
        else if (event.equals("Coffee Flavored")) {
          System.out.println("CF");
          changeFrame(createCoffeeFlavoredPage());
        }

      
    }

    private void removeFromOrder(JButton drinkButton) {

        orderLogs.remove(drinkButton);
        orderLogs.revalidate();
        orderLogs.repaint();

        String[] drinkInfo = drinkButton.getText().split(" \\$");
        
        orderTotal -= drinkPriceMap.get(drinkInfo[0]);

        System.out.println("Order total: " + orderTotal);

        payButton.setText("Charge $" + orderTotal);

        // splitting drinkbutton text on $

        order.remove(drinkInfo[0]);
    }

    // handle adding a drink to the order list
    private void addToOrder(String drinkName) {

        orderTotal += drinkPriceMap.get(drinkName);

        JButton drinkButton = new JButton(drinkName + " $" + drinkPriceMap.get(drinkName));
        drinkButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            removeFromOrder(drinkButton);
          }
        });
        orderLogs.add(drinkButton);
        orderLogs.revalidate();
        orderLogs.repaint();

        System.out.println("Order total: " + orderTotal);

        payButton.setText("Charge $" + orderTotal);

        // adding to arraylist of drinks in order
        order.add(drinkName);
    }

    private void completeOrder() {
        // TODO: add employee id and customer id and order total

        OrderLogic.placeOrder(1, 1, order.toArray(new String[order.size()]), orderTotal);
        order.clear();
        orderTotal = 0.0;
        orderLogs.removeAll();
        orderLogs.revalidate();
        orderLogs.repaint();

        payButton.setText("Charge $" + orderTotal);
    }

    //displays either the cashier view or the manager view based on combobox selection
    public static void viewSelector(boolean manager){
      startFrame.setVisible(false);
      if(manager){
        JFrame cashierFrame = new JFrame("Manager Display");
        cashierFrame.setSize(300, 100);
        cashierFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(gui);
        cashierFrame.add(backToLogin);
        changeFrame(cashierFrame);
      }
      else{
        GUI guiInstance = new GUI();
        JFrame cashierFrame = guiInstance.createSweetAndCreamyPage();
        changeFrame(cashierFrame);
      }
    }

    private static JButton StyledButton(String text) {
      JButton button = new JButton("<html><center>" + text + "</center></html>", null);
      button.setFont(new Font("Roboto", Font.PLAIN, 20));
      button.setBackground(Color.WHITE);
      button.setFocusPainted(false);
      
      button.setBorder(BorderFactory.createEmptyBorder());

      button.setBorderPainted(false);
      button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      //button.setToolTipText(text); // INteresting mechanic

      // Hover Mechanics
      button.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
          button.setBackground(new Color(230, 230, 230));
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
          button.setBackground(Color.WHITE);
        }
      });
      return button;
    }

    // FruityRefreshingPage
    // TODO: add to the order list a remove button next to each drink
    public JFrame createFruityRefreshingPage() {
      JFrame fruityRefreshingFrame = new JFrame("Fruity and Refreshing");
        fruityRefreshingFrame.setSize(1000, 800);
        fruityRefreshingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        //Font titleButtonFont = new Font("Roboto", Font.BOLD, 24);

        // Left Nav panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Drink Type Buttons
        JButton creamyButton = StyledButton("Sweet and Creamy");
        creamyButton.setActionCommand("Sweet and Creamy");
        creamyButton.addActionListener(gui);
        JButton fruityButton = StyledButton("Fruity and Refreshing");
        fruityButton.setActionCommand("Fruity and Refreshing");
        fruityButton.addActionListener(gui);
        JButton coffeeButton = StyledButton("Coffee Flavored");
        coffeeButton.setActionCommand("Coffee Flavored");
        coffeeButton.addActionListener(gui);

        navPanel.add(creamyButton);
        navPanel.add(fruityButton);
        navPanel.add(coffeeButton);

        navPanel.add(Box.createVerticalGlue());
        backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(gui);
        navPanel.add(backToLogin);
        
       
        mainPanel.add(navPanel, BorderLayout.WEST);

        // Content Panel for drinks
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridLayout(3, 2, 20, 20));

        JButton drinkButton1 = StyledButton("Taro Bubble Tea" + " $" + drinkPriceMap.get("Taro Bubble Tea"));
        drinkButton1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Taro Bubble Tea");
          }
        });
        JButton drinkButton2 = StyledButton("Tiger Mango Sago" + " $" + drinkPriceMap.get("Tiger Mango Sago"));
        drinkButton2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Tiger Mango Sago");
          }
        });
        JButton drinkButton3 = StyledButton("Passion Fruit Tea" + " $" + drinkPriceMap.get("Passion Fruit Tea"));
        drinkButton3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Passion Fruit Tea");
          }
        });
        JButton drinkButton4 = StyledButton("Jasmine Green Tea" + " $" + drinkPriceMap.get("Jasmine Green Tea"));
        drinkButton4.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Jasmine Green Tea");
          }
        });

        contentPanel.add(drinkButton1);
        contentPanel.add(drinkButton2);
        contentPanel.add(drinkButton3);
        contentPanel.add(drinkButton4);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        //Right Panel for orders

        JPanel rightPanel = new JPanel(new BorderLayout());

        JLabel orderListLabel = new JLabel("Order List");
        orderListLabel.setFont(new Font("Arial", Font.BOLD, 24));
        orderListLabel.setHorizontalAlignment(JLabel.CENTER);
        rightPanel.add(orderListLabel, BorderLayout.NORTH);

          //Order Text
        // orderLogs = new JTextArea(10, 20);
        // orderLogs.setEditable(false);
        orderLogs = new JPanel();
        orderLogs.setFont(new Font("Arial", Font.PLAIN, 16));
        orderLogs.setLayout(new BoxLayout(orderLogs, BoxLayout.Y_AXIS));

        // populating orderlogs if orders already exist
        if (order.size() > 0) {
          for (String drink : order) {
            JButton drinkButton = new JButton(drink + " $" + drinkPriceMap.get(drink));
            drinkButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                removeFromOrder(drinkButton);
              }
            });
            orderLogs.add(drinkButton);
          }
        }

        JScrollPane orderScrollPane = new JScrollPane(orderLogs);
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);

        payButton = new JButton("Charge $" + orderTotal);
        payButton.setFont(new Font("Arial", Font.BOLD, 20));
        payButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            completeOrder();
          }
        });
        rightPanel.add(payButton, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        fruityRefreshingFrame.add(mainPanel);
        fruityRefreshingFrame.setVisible(true);

        changeFrame(fruityRefreshingFrame);
      
        return fruityRefreshingFrame;
    }

    // TODO: add to the order list a remove button next to each drink
    public JFrame createSweetAndCreamyPage() {
      JFrame sweetAndCreamyFrame = new JFrame("Sweet and Creamy");
        sweetAndCreamyFrame.setSize(1000, 800);
        sweetAndCreamyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        //Font titleButtonFont = new Font("Roboto", Font.BOLD, 24);

        // Left Nav panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Drink Type Buttons
        JButton creamyButton = StyledButton("Sweet and Creamy");
        creamyButton.setActionCommand("Sweet and Creamy");
        creamyButton.addActionListener(gui);
        JButton fruityButton = StyledButton("Fruity and Refreshing");
        fruityButton.setActionCommand("Fruity and Refreshing");
        fruityButton.addActionListener(gui);
        JButton coffeeButton = StyledButton("Coffee Flavored");
        coffeeButton.setActionCommand("Coffee Flavored");
        coffeeButton.addActionListener(gui);

        navPanel.add(creamyButton);
        navPanel.add(fruityButton);
        navPanel.add(coffeeButton);

        navPanel.add(Box.createVerticalGlue());
        backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(gui);
        navPanel.add(backToLogin);

        mainPanel.add(navPanel, BorderLayout.WEST);

        // Content Panel for drinks
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridLayout(3, 2, 20, 20));

        JButton drinkButton1 = StyledButton("Classic Brown Sugar Boba Milk Tea" + " $" + drinkPriceMap.get("Classic Brown Sugar Boba Milk Tea"));
        drinkButton1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Classic Brown Sugar Boba Milk Tea");
          }
        });
        JButton drinkButton2 = StyledButton("Matcha Black Sugar Boba Milk" + " $" + drinkPriceMap.get("Matcha Black Sugar Boba Milk"));
        drinkButton2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Matcha Black Sugar Boba Milk");
          }
        });
        JButton drinkButton3 = StyledButton("Red Bean Matcha Milk" + " $" + drinkPriceMap.get("Red Bean Matcha Milk"));
        drinkButton3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Red Bean Matcha Milk");
          }
        });
        JButton drinkButton4 = StyledButton("Strawberry Milk" + " $" + drinkPriceMap.get("Strawberry Milk"));
        drinkButton4.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Strawberry Milk");
          }
        });
        JButton drinkButton5 = StyledButton("Golden Oolong Tea" + " $" + drinkPriceMap.get("Golden Oolong Tea"));
        drinkButton5.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Golden Oolong Tea");
          }
        });

        contentPanel.add(drinkButton1);
        contentPanel.add(drinkButton2);
        contentPanel.add(drinkButton3);
        contentPanel.add(drinkButton4);
        contentPanel.add(drinkButton5);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        //Right Panel for orders

        JPanel rightPanel = new JPanel(new BorderLayout());

        JLabel orderListLabel = new JLabel("Order List");
        orderListLabel.setFont(new Font("Arial", Font.BOLD, 24));
        orderListLabel.setHorizontalAlignment(JLabel.CENTER);
        rightPanel.add(orderListLabel, BorderLayout.NORTH);

          //Order Text
        // orderLogs = new JTextArea(10, 20);
        // orderLogs.setEditable(false);
        orderLogs = new JPanel();
        orderLogs.setFont(new Font("Arial", Font.PLAIN, 16));
        orderLogs.setLayout(new BoxLayout(orderLogs, BoxLayout.Y_AXIS));

        // populating orderlogs if orders already exist
        if (order.size() > 0) {
          for (String drink : order) {
            JButton drinkButton = new JButton(drink + " $" + drinkPriceMap.get(drink));
            drinkButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                removeFromOrder(drinkButton);
              }
            });
            orderLogs.add(drinkButton);
          }
        }

        JScrollPane orderScrollPane = new JScrollPane(orderLogs);
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);

        payButton = new JButton("Charge $" + orderTotal);
        payButton.setFont(new Font("Arial", Font.BOLD, 20));
        payButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            completeOrder();
          }
        });
        rightPanel.add(payButton, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        sweetAndCreamyFrame.add(mainPanel);
        sweetAndCreamyFrame.setVisible(true);

        changeFrame(sweetAndCreamyFrame);
      
        return sweetAndCreamyFrame;
    }

    // TODO: add to the order list a remove button next to each drinks
    public JFrame createCoffeeFlavoredPage() {
      JFrame coffeeFlavoredFrame = new JFrame("Coffee Flavored");
        coffeeFlavoredFrame.setSize(1000, 800);
        coffeeFlavoredFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        //Font titleButtonFont = new Font("Roboto", Font.BOLD, 24);

        // Left Nav panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Drink Type Buttons
        JButton creamyButton = StyledButton("Sweet and Creamy");
        creamyButton.setActionCommand("Sweet and Creamy");
        creamyButton.addActionListener(gui);
        JButton fruityButton = StyledButton("Fruity and Refreshing");
        fruityButton.setActionCommand("Fruity and Refreshing");
        fruityButton.addActionListener(gui);
        JButton coffeeButton = StyledButton("Coffee Flavored");
        coffeeButton.setActionCommand("Coffee Flavored");
        coffeeButton.addActionListener(gui);


        navPanel.add(creamyButton);
        navPanel.add(fruityButton);
        navPanel.add(coffeeButton);

        navPanel.add(Box.createVerticalGlue());
        backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(gui);
        navPanel.add(backToLogin);
        
       
        mainPanel.add(navPanel, BorderLayout.WEST);

        // Content Panel for drinks
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridLayout(3, 2, 20, 20));

        JButton drinkButton1 = StyledButton("Black Sugar Coffee Jelly" + " $" + drinkPriceMap.get("Black Sugar Coffee Jelly"));
        drinkButton1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Black Sugar Coffee Jelly");
          }
        });

        contentPanel.add(drinkButton1);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        //Right Panel for orders

        JPanel rightPanel = new JPanel(new BorderLayout());

        JLabel orderListLabel = new JLabel("Order List");
        orderListLabel.setFont(new Font("Arial", Font.BOLD, 24));
        orderListLabel.setHorizontalAlignment(JLabel.CENTER);
        rightPanel.add(orderListLabel, BorderLayout.NORTH);

          //Order Text
        // orderLogs = new JTextArea(10, 20);
        // orderLogs.setEditable(false);
        orderLogs = new JPanel();
        orderLogs.setFont(new Font("Arial", Font.PLAIN, 16));
        orderLogs.setLayout(new BoxLayout(orderLogs, BoxLayout.Y_AXIS));


        // populating orderlogs if orders already exist
        if (order.size() > 0) {
          for (String drink : order) {
            JButton drinkButton = new JButton(drink + " $" + drinkPriceMap.get(drink));
            drinkButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                removeFromOrder(drinkButton);
              }
            });
            orderLogs.add(drinkButton);
          }
        }

        JScrollPane orderScrollPane = new JScrollPane(orderLogs);
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);

        payButton = new JButton("Charge $" + orderTotal);
        payButton.setFont(new Font("Arial", Font.BOLD, 20));
        payButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            completeOrder();
          }
        });
        rightPanel.add(payButton, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        coffeeFlavoredFrame.add(mainPanel);
        coffeeFlavoredFrame.setVisible(true);

        changeFrame(coffeeFlavoredFrame);
      
        return coffeeFlavoredFrame;
    }
}