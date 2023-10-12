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

    static JFrame startFrame; //opens on start, allows you to select an employee
    static JFrame inventoryFrame; //inventory screen
    static JFrame managerFrame; //manager view menu screen
    static JFrame cashierFrame; //cashier screen
    static JFrame recentFrame; //recent orders screen
    static JFrame statsFrame; //order stats screen
    static JTable statsTable; //stats table 
    static JFrame editorFrame; //menu editor frame
    static JFrame currFrame; //the current framethat is being used.
    static JFrame prevFrame;
    static GUI gui;
    static JTextArea hello; //text area for testing
    static JComboBox<Employee> employeeSelector; //drop down for employees, how we know to go in cashier view or  manager view
    static JButton employeeEnter;//locks in combobox entry
    static JButton backToLogin; //back button that returns to employee select
    static JButton payButton;
    static JPanel orderLogs;
    static ArrayList<String> order = new ArrayList<String>();
    static OrderLogic orderLogic = new OrderLogic();
    static ManagerLogic managerLogic = new ManagerLogic();
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
      //initiaize frame
      startFrame = new JFrame("Tiger Sugar POS");
      startFrame.setSize(1000, 800);
      startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      //initalize panel and GUI
      gui = new GUI();
      JPanel startPanel = new JPanel();
      startFrame.add(startPanel);

      //ComboBox for Employees
      setEmployeeComboBox();
      startPanel.add(employeeSelector);

      //setup enter button
      employeeEnter = new JButton("Enter"); 
      employeeEnter.addActionListener(gui);
      startPanel.add(employeeEnter);

      //execute first frame
      currFrame = startFrame;
      currFrame.setVisible(true);
    }

    //CREATES THE INVENTORY FRAME AND READS IN FROM DATABASE
    public static void setUpInventory(){
        //frame setup
        inventoryFrame = new JFrame("Inventory");
        inventoryFrame.setSize(1000, 800);

        //create bottom panel (content)
        JPanel inventoryPanel = new JPanel();
        inventoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inventoryFrame.add(inventoryPanel);

        //create top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Inventory");
        titlePanel.add(title);

        //create middle panel (menu)
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        //frame layout
        inventoryFrame.add(titlePanel,BorderLayout.NORTH);
        inventoryFrame.add(menuPanel,BorderLayout.CENTER);
        inventoryFrame.add(inventoryPanel,BorderLayout.SOUTH);

        //create back button
        JButton backToManager = new JButton("Back to Manager Menu"); //goes back to manager menu
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        //create add button
        JButton add = new JButton("Add Supply Item");
        add.addActionListener(gui);
        menuPanel.add(add);

        //create remove button
        JButton remove = new JButton("Remove Supply Item");
        remove.addActionListener(gui);
        menuPanel.add(remove);

        //create scrollable table
        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        inventoryPanel.add(scroll);

        //filling the table with database data
        managerLogic.getInventory(table);

        inventoryFrame.pack();
    }

    //CREATES Recent orders Frame
    public static void setUpRecentOrders(){
        //initialize recent orderes frame
        recentFrame = new JFrame("RecentOrders");
        recentFrame.setSize(1000, 800);

        //create top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Recent Orders");
        titlePanel.add(title);

        //create middle panel (menu)
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        //create bottom panel (content)
        JPanel recentPanel = new JPanel();
        recentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //frame layout
        recentFrame.add(titlePanel,BorderLayout.NORTH);
        recentFrame.add(menuPanel,BorderLayout.CENTER);
        recentFrame.add(recentPanel,BorderLayout.SOUTH);

        //setup button that returns to manager
        JButton backToManager = new JButton("Back to Manager Menu"); //goes back to manager menu
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        //create scrollable table
        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        recentPanel.add(scroll);

        //populate table with data from database
        managerLogic.getRecentOrders(table);

        recentFrame.pack();
    }

    //STATS FRAME
    public static void setUpOrderStats(){
        //initilize frame
        statsFrame = new JFrame("Order Statistics");
        statsFrame.setSize(1000, 800);
        statsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //initilize top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Order Statistics");
        titlePanel.add(title);

        //create middle panel (menu)
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        //create bottom panel (content)
        JPanel statsPanel = new JPanel();

        //frame layout
        statsFrame.add(titlePanel,BorderLayout.NORTH);
        statsFrame.add(menuPanel,BorderLayout.CENTER);
        statsFrame.add(statsPanel,BorderLayout.SOUTH);

        //setup button that returns to manager menu
        JButton backToManager = new JButton("Back to Manager Menu"); //goes back to manager menu
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        //button that displays daily stats
        JButton daily = new JButton("Daily Stats"); //shows today's stats
        daily.addActionListener(gui);
        menuPanel.add(daily);

        //button that displays stats over a custom range
        JButton custom = new JButton("Custom Range"); //shows custom stats
        custom.addActionListener(gui);
        menuPanel.add(custom);

        //sets up table; default is daily stats
        statsTable = dailyStats();
        JScrollPane  scroll = new JScrollPane(statsTable);
        statsPanel.add(scroll);

        
        statsFrame.pack();
    }

    //returns the table of daily stats
    public static JTable dailyStats(){
      JTable table = new JTable();
      //calculates and displays the daily drinks sold and sales
      table = managerLogic.getDailyStats(table);
      return table;
    }

    //TODO: returns table of custom range stats
    //TODO: write a function in manager logic similar to getDailyStats that does the same over a custom range
    public static JTable customRange(){
      JTable table = new JTable();
      return table;
    }

    //sets up frame formenu editor
    public static void setUpMenuEditor(){

        //initialize frame
        editorFrame = new JFrame("Menu Editor");
        editorFrame.setSize(1000, 800);
        editorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //initilize bottom panel (content)
        JPanel editorPanel = new JPanel();
        editorFrame.add(editorPanel);

        //initialize top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Menu Editor");
        titlePanel.add(title);

        //initialize middle panel (menu)
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        //frame layout
        editorFrame.add(titlePanel,BorderLayout.NORTH);
        editorFrame.add(menuPanel,BorderLayout.CENTER);
        editorFrame.add(editorPanel,BorderLayout.SOUTH);

        //set up button that goes back to manager menu
        JButton backToManager = new JButton("Back to Manager Menu"); //goes back to manager menu
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        //set up button that adds menu item
        JButton add = new JButton("Add Menu Item");
        add.addActionListener(gui);
        menuPanel.add(add);

        //set up button that removes menu item
        JButton remove = new JButton("Remove Menu Item");
        remove.addActionListener(gui);
        menuPanel.add(remove);

        //setup scrollable tabel
        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        editorPanel.add(scroll);

        //getting the data
        managerLogic.getMenu(table);
        editorFrame.pack();
    }

    //set up employee selector
    public static void setEmployeeComboBox(){
      //loads in the names of the employees
      Vector<Employee> employees = new Vector<>(); 
      try{
        Statement stmt = conn.createStatement();
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
  

    public static void main(String[] args)
    {
      //Connect to Database
      connect();

      //Setup Frames
      frameSetup();
      setUpInventory();
      setUpRecentOrders();
      setUpOrderStats();
      setUpMenuEditor();
     
      
      //closing the connection
      try {
        // conn.close();
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
        else if (event.equals("Seasonal Drinks")) {
          System.out.println("SD");
          changeFrame(createSeasonalDrinksPage());
        }
        else if(event.equals("Back to Manager Menu")){
          changeFrame(managerFrame);
        }

        //opens inventory page
        else if(event.equals("View Inventory")){
          setUpInventory();
          changeFrame(inventoryFrame);
        }

        //opens price editor
        else if(event.equals("Edit Menu")){
          setUpMenuEditor();
          changeFrame(editorFrame);
        }
        
        //opens order stats
        else if(event.equals("Order Statistics")){
          setUpOrderStats();
          changeFrame(statsFrame);
        }

        //opens recent orders
        else if(event.equals("Recent Orders")){
          setUpRecentOrders();
          changeFrame(recentFrame);
        }

        //on order stats page, shows daily stats
        else if(event.equals("Daily Stats")){
          dailyStats();
        }

        //on order stats page, show stats for inputted range, input with TwoInputDialog
        //NEEDS TO BE FINISHED
        else if(event.equals("Custom Range")){
          //using a custom 2 input dialog, get the two inputs
          TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter start date: YYYY-MM-DD","Enter end date: YYYY-MM-DD");
          TwoInputs inputs = dialog.showInputDialog();

          String start = inputs.input1;
          String end = inputs.input2;
          if(start != "" && end != ""){   //TODO: Input Validtaion
              System.out.println("VALID");
          }
          else{
            JOptionPane.showMessageDialog(null, "You have entered an invalid date.", "ERROR", JOptionPane.INFORMATION_MESSAGE);
          }
          //TODO: Call a method customRange(start, end) that table
        }

        //on inventory page, adds a supply item to the database
        else if(event.equals("Add Supply Item")){
          try{
            //gets the inputs with the two input dialog
            TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter new supply","Enter amount of new stock");
            TwoInputs inputs = dialog.showInputDialog();
            String newSupply = inputs.input1;
            Integer newStock = Integer.parseInt(inputs.input2);

            //query
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("INSERT INTO inventory (inventory_id, supply, stock_remaining) VALUES (DEFAULT, '"+newSupply+"', "+newStock+");");
        
          }  catch (Exception ex){ //errors connecting to database
            //JOptionPane.showMessageDialog(null,ex);
          }

           //update graphics
          setUpInventory();
          changeFrame(inventoryFrame);
        }

        //on inventory page, removes a supply item from the database
        else if(event.equals("Remove Supply Item")){
          try{
            //gets the id of the object to remove
            Integer item = Integer.parseInt(JOptionPane.showInputDialog("Enter ID of object to be removed"));
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("DELETE FROM inventory WHERE inventory_id = "+item+";");
          }catch (Exception ex){ //errors connecting to database
            //JOptionPane.showMessageDialog(null,ex);
          }

           //update graphics
          setUpInventory();
          changeFrame(inventoryFrame);
        }

        //on menu editor page, adds a menu item to the database
        else if(event.equals("Add Menu Item")){
          try{
            //create a statement object
            //TODO add input validation
            TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter new menu item","Enter price");
            TwoInputs inputs = dialog.showInputDialog();
            String newDrink = inputs.input1;
            Double newPrice = Double.parseDouble(inputs.input2); 
            Vector<String> ings = new Vector<>();

            //get ingredients
            //TODO add input validation
            Integer ingredientCount = Integer.parseInt(JOptionPane.showInputDialog("How many ingredients does this drink have?"));
            for(int i = 0;i<ingredientCount;i++){
              //for each ingredient:
              String ingredient = JOptionPane.showInputDialog("Enter an ingredient");
              ings.add(ingredient);
              Statement stmt = conn.createStatement();
              ResultSet result = stmt.executeQuery("SELECT * FROM inventory WHERE supply = '"+ingredient+"';");
              if(!result.next()){ //if supply is not in the inventory
                System.out.println(ingredient);
                Statement stmt2 = conn.createStatement();
                try{ //add a new supply
                  stmt2.executeQuery("INSERT INTO inventory (inventory_id, supply, stock_remaining) VALUES (DEFAULT, '"+ingredient+"', 100);");
                } catch(Exception ex){ }
              }
            }
            //TODO Add Input Validation
            String drinkType = JOptionPane.showInputDialog("Enter drink type");


            //convert vector to an array
            String[] ingredients = ings.toArray(new String[0]);
           
            //prep new query to insert new item onto menu
            String query = "INSERT INTO products (product_id, drink_name, price, ingredients, drink_type) VALUES (DEFAULT, ?, ?, string_to_array(?, ', '), ?);";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,newDrink);
            preparedStatement.setDouble(2,newPrice);

            String ing = String.join(",",ingredients);
            preparedStatement.setString(3,ing);
            preparedStatement.setString(4,drinkType);
            preparedStatement.executeUpdate();
        
          }  catch (Exception ex){ //errors connecting to database
            System.out.println(ex);   
           }
          
           //update graphics
          setUpMenuEditor();
          changeFrame(editorFrame);
        }
        //remove a menu item
        else if(event.equals("Remove Menu Item")){
          try{
            //get input and execute a query
            Integer item = Integer.parseInt(JOptionPane.showInputDialog("Enter ID of object to be removed"));
            Statement stmt = conn.createStatement();
            ResultSet r = stmt.executeQuery("DELETE FROM products WHERE product_id = "+item+";");
          }catch (Exception ex){ //errors connecting to database
            //JOptionPane.showMessageDialog(null,ex);
          }

           //update graphics
          setUpMenuEditor();
          changeFrame(editorFrame);
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
      if(manager){ //go to manager view
        //setup manager frame
        managerFrame = new JFrame("Manager Display");
        managerFrame.setSize(1000, 800);
        JPanel managerPanel = new JPanel();
        managerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        managerFrame.add(managerPanel);

        //setup back to login page button
        JButton backToLogin = new JButton("Back to Login"); //goes back to login
        backToLogin.addActionListener(gui);
        managerPanel.add(backToLogin);

        //setup view inventory button
        JButton viewInventory = new JButton("View Inventory"); //open view inventory menu
        viewInventory.addActionListener(gui);
        managerPanel.add(viewInventory);

        //setup menu editor button
        JButton editMenu = new JButton("Edit Menu"); //open edit prices menu
        editMenu.addActionListener(gui);
        managerPanel.add(editMenu);

        //setup order stats button
        JButton orderStats = new JButton("Order Statistics"); //open order stats
        orderStats.addActionListener(gui);
        managerPanel.add(orderStats);

        //setup recent orders button
        JButton recentOrders = new JButton("Recent Orders"); //open recent orders
        recentOrders.addActionListener(gui);
        managerPanel.add(recentOrders);

         //switch frame
        changeFrame(managerFrame);
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
        JButton seasonalButton = StyledButton("Seasonal Drinks");
        seasonalButton.setActionCommand("Seasonal Drinks");
        seasonalButton.addActionListener(gui);

        navPanel.add(creamyButton);
        navPanel.add(fruityButton);
        navPanel.add(coffeeButton);
        navPanel.add(seasonalButton);

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
        JButton seasonalButton = StyledButton("Seasonal Drinks");
        seasonalButton.setActionCommand("Seasonal Drinks");
        seasonalButton.addActionListener(gui);

        navPanel.add(creamyButton);
        navPanel.add(fruityButton);
        navPanel.add(coffeeButton);
        navPanel.add(seasonalButton);

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
        JButton seasonalButton = StyledButton("Seasonal Drinks");
        seasonalButton.setActionCommand("Seasonal Drinks");
        seasonalButton.addActionListener(gui);


        navPanel.add(creamyButton);
        navPanel.add(fruityButton);
        navPanel.add(coffeeButton);
        navPanel.add(seasonalButton);

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

    public JFrame createSeasonalDrinksPage() {
      JFrame seasonalDrinksFrame = new JFrame("Seasonal Drinks");
        seasonalDrinksFrame.setSize(1000, 800);
        seasonalDrinksFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        JButton seasonalButton = StyledButton("Seasonal Drinks");
        seasonalButton.setActionCommand("Seasonal Drinks");
        seasonalButton.addActionListener(gui);


        navPanel.add(creamyButton);
        navPanel.add(fruityButton);
        navPanel.add(coffeeButton);
        navPanel.add(seasonalButton);

        navPanel.add(Box.createVerticalGlue());
        backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(gui);
        navPanel.add(backToLogin);
        
       
        mainPanel.add(navPanel, BorderLayout.WEST);

        // Content Panel for drinks
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridLayout(3, 2, 20, 20));

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

        seasonalDrinksFrame.add(mainPanel);
        seasonalDrinksFrame.setVisible(true);

        changeFrame(seasonalDrinksFrame);
      
        return seasonalDrinksFrame;
    }
}