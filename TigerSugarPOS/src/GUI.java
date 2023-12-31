import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.*;

/**
 * This class contains the GUI elements for the Tiger Sugar POS for cashiers and managers.
 * 
 * @author Chris Vu, Josh Hare, Doby Lanete, Tyson Long
 */
public class GUI extends JFrame implements ActionListener{
    // Attributes
    static Connection conn; // Database connection
    static JFrame startFrame; // Opens on start, allows you to select an employee
    static JFrame inventoryFrame; // Inventory screen
    static JFrame restockReportFrame; // Restock Report screen
    static JFrame excessReportFrame; // Excess Report screen
    static JFrame salesTogetherFrame; // What Sales Together screen
    static JFrame managerFrame; // Manager view menu screen
    static JFrame cashierFrame; // Cashier screenF
    static JFrame recentFrame; // Recent orders screen
    static JFrame statsFrame; // Order stats screen
    static JTable statsTable; // Stats table 
    static String currRange; // Current display on order stats
    static JFrame editorFrame; // Menu editor frame
    static JFrame currFrame; // The current framethat is being used.
    static JFrame prevFrame;
    static GUI gui;
    static JTextArea hello; // Text area for testing
    static JComboBox<Employee> employeeSelector; // Drop down for employees, how we know to go in cashier view or  manager view
    static JButton employeeEnter;// Locks in combobox entry
    static JButton backToLogin; // Back button that returns to employee select
    static JTextArea textArea; //for recent orders
    static JButton payButton;
    static JPanel orderLogs;
    static JPanel rightPanel;
    static ArrayList<String> order = new ArrayList<String>();
    static ArrayList<String> drinkAttributes = new ArrayList<String>();
    static ArrayList<String> drinkAddons = new ArrayList<String>();
    static OrderLogic orderLogic = new OrderLogic();
    static ManagerLogic managerLogic = new ManagerLogic();
    static GUILogic guiLogic = new GUILogic();
    static double orderTotal = 0.0;
    static Map<String, Double> drinkPriceMap = new HashMap<String, Double>();
    static ArrayList<JPanel> drinkButtonPanels = new ArrayList<JPanel>();
    static ArrayList<JButton> drinkButtons = new ArrayList<JButton>();
    static ArrayList<Boolean> openButtons = new ArrayList<Boolean>();
    static Timestamp timestamp = Timestamp.valueOf("2023-04-10 10:30:00");

    /**
     * Establishes connection to the database, through the conn variable
     */
    public static void connect(){
        conn = null;
        try{
            conn = DriverManager.getConnection(
            "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_10g_db",
            "csce315_910_dlanete",
            "password");
        }catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }


    /**
     * Change frame to a new frame, use when switching menus
     * @param newFrame the incoming new frame
     */
    public static void changeFrame(JFrame newFrame){
        currFrame.setVisible(false);
        prevFrame = currFrame;
        currFrame = newFrame;
        currFrame.setVisible(true);
        // Set all open buttons to false
        for(int i = 0; i < openButtons.size(); i++){
            openButtons.set(i, false);
        }
    }


    /**
     * Initialize variables and components necessary for the GUI
     */
    public static void frameSetup(){
        // Initiaize frame
        startFrame = new JFrame("Tiger Sugar POS");
        startFrame.setSize(1000, 800);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initalize panel and GUI
        gui = new GUI();

        Color backgroundColor = new Color(228, 229, 241);

        JPanel startPanel = new JPanel();
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setBackground(backgroundColor);

        // changing panel bg color
        startFrame.add(startPanel);

        JPanel titlePanel = new JPanel();
        titlePanel.setMaximumSize(new Dimension(1000,50));
        titlePanel.setBackground(backgroundColor);
        
        JPanel logoPanel = new JPanel();
        logoPanel.setMaximumSize(new Dimension(1000,150));
        logoPanel.setBackground(backgroundColor);

        JPanel employeeSelectorPanel = new JPanel();
        employeeSelectorPanel.setMaximumSize(new Dimension(1000,50));
        employeeSelectorPanel.setBackground(backgroundColor);

        JPanel employeeEnterPanel = new JPanel();
        employeeEnterPanel.setMaximumSize(new Dimension(1000,50));
        employeeEnterPanel.setBackground(backgroundColor);

        JLabel title = new JLabel("Tiger Sugar POS");
        titlePanel.add(title);
        
        ImageIcon icon = new ImageIcon("images\\TigerSugarLogo.jpg");
        JLabel logo = new JLabel(icon);
        logoPanel.add(logo);

        // ComboBox for Employees
        setEmployeeComboBox();
        employeeSelectorPanel.add(employeeSelector);

        // Setup enter button
        employeeEnter = new JButton("Enter"); 
        employeeEnter.addActionListener(gui);
        employeeEnterPanel.add(employeeEnter);
        
        startPanel.add(titlePanel);
        startPanel.add(logoPanel);
        startPanel.add(employeeSelectorPanel);
        startPanel.add(employeeEnterPanel);
        
        // Execute first frame
        currFrame = startFrame;
        currFrame.setVisible(true);
    }


    /**
     * Creates the restock report and reads in from database
     */
    public static void setUpRestockReport(){
        // Frame setup
        restockReportFrame = new JFrame("Restock Report");
        restockReportFrame.setSize(1000, 800);

        // Create bottom panel (content)
        JPanel restockReportPanel = new JPanel();
        restockReportFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        restockReportFrame.add(restockReportPanel);

        // Create top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Restock Report");
        titlePanel.add(title);

        // Create middle panel (menu)
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        // Frame layout
        restockReportFrame.add(titlePanel,BorderLayout.NORTH);
        restockReportFrame.add(menuPanel,BorderLayout.CENTER);
        restockReportFrame.add(restockReportPanel,BorderLayout.SOUTH);

        // Create back button
        JButton backToManager = new JButton("Back to Manager Menu"); 
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        // Create scrollable table
        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        restockReportPanel.add(scroll);

        // Filling the table with database data
        managerLogic.getRestockReport(table);

        restockReportFrame.pack();
    }


    /**
     * Creates the excess report and reads in from database
     */
    public static void setUpExcessReport(){
        // Frame setup
        excessReportFrame = new JFrame("Excess Report");
        excessReportFrame.setSize(1000, 800);

        // Create bottom panel (content)
        JPanel excessReportPanel = new JPanel();
        excessReportFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        excessReportFrame.add(excessReportPanel);

        // Create top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Excess Report");
        titlePanel.add(title);

        // Create middle panel (menu)
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        // Frame layout
        excessReportFrame.add(titlePanel,BorderLayout.NORTH);
        excessReportFrame.add(menuPanel,BorderLayout.CENTER);
        excessReportFrame.add(excessReportPanel,BorderLayout.SOUTH);

        // Create back button
        JButton backToManager = new JButton("Back to Manager Menu"); 
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        // Create scrollable table
        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        excessReportPanel.add(scroll);

        JTextField timestampField = new JTextField("YYYY-MM-DD HH:MM:SS");
        menuPanel.add(timestampField);

        JButton updateTimestampButton = new JButton("Update Timestamp");
        menuPanel.add(updateTimestampButton);

        updateTimestampButton.addActionListener(new ActionListener() {

            /**
             * Updates the timestamp
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the new timestamp value from the text field
                String newTimestampStr = timestampField.getText();
        
                try {
                    // Parse the user input into a Timestamp object
                    Timestamp newTimestamp = Timestamp.valueOf(newTimestampStr);
        
                    // Update the timestamp variable with the new value
                    timestamp = newTimestamp;
                    System.out.println("New Time: " + timestamp);
                    // Getting the data
                    managerLogic.getExcessReport(table, timestamp);
        
                    // You can optionally update the table or perform other actions here
                } catch (IllegalArgumentException ex) {
                    // Handle invalid input gracefully, e.g., show an error message
                    JOptionPane.showMessageDialog(excessReportFrame, "Invalid timestamp format. Please use yyyy-MM-dd HH:mm:ss", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        

        // Filling the table with database data
        

        

        excessReportFrame.pack();
    }

    /**
     * Creates the what sales together and reads in from database
     */
    public static void setUpWhatSalesTogether() {
        // Frame setup
        salesTogetherFrame = new JFrame("What Sales Together");
        salesTogetherFrame.setSize(1000, 800);

        // Create bottom panel (content)
        JPanel salesTogetherPanel = new JPanel();
        salesTogetherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        salesTogetherFrame.add(salesTogetherPanel);

        // Create top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("What Sales Together");
        titlePanel.add(title);

        // Create middle panel (menu)
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        // Frame layout
        salesTogetherFrame.add(titlePanel,BorderLayout.NORTH);
        salesTogetherFrame.add(menuPanel,BorderLayout.CENTER);
        salesTogetherFrame.add(salesTogetherPanel,BorderLayout.SOUTH);

        // Create back button
        JButton backToManager = new JButton("Back to Manager Menu"); 
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        // Create scrollable table
        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        salesTogetherPanel.add(scroll);

        // Filling the table with database data
        
        // getting two text fields to get timestamp
        JTextField startTimestampField = new JTextField("YYYY-MM-DD HH:MM:SS");
        menuPanel.add(startTimestampField);

        JTextField endTimestampField = new JTextField("YYYY-MM-DD HH:MM:SS");
        menuPanel.add(endTimestampField);

        JButton submitTimestampsButton = new JButton("Submit Timestamps");
        menuPanel.add(submitTimestampsButton);

        submitTimestampsButton.addActionListener(new ActionListener() {
            /**
             * Updates the timestamp
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the new timestamp value from the text field
                String startTimestampStr = startTimestampField.getText();
                String endTimestampStr = endTimestampField.getText();
        
                try {
                    // Parse the user input into a Timestamp object
                    Timestamp startTimestamp = Timestamp.valueOf(startTimestampStr);
                    Timestamp endTimestamp = Timestamp.valueOf(endTimestampStr);

                    // Getting the data
                    managerLogic.getWhatSalesTogether(table, startTimestamp, endTimestamp);
        
                    // You can optionally update the table or perform other actions here
                } catch (IllegalArgumentException ex) {
                    // Handle invalid input gracefully, e.g., show an error message
                    JOptionPane.showMessageDialog(excessReportFrame, "Invalid timestamp format. Please use yyyy-MM-dd HH:mm:ss", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        


        salesTogetherFrame.pack();
    }


    /**
     * Creates the inventory frame and reads in from database
     */
    public static void setUpInventory(){
        // Frame setup
        inventoryFrame = new JFrame("Inventory");
        inventoryFrame.setSize(1000, 800);

        // Create bottom panel (content)
        JPanel inventoryPanel = new JPanel();
        inventoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inventoryFrame.add(inventoryPanel);

        // Create top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Inventory");
        titlePanel.add(title);

        // Create middle panel (menu)
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        // Frame layout
        inventoryFrame.add(titlePanel,BorderLayout.NORTH);
        inventoryFrame.add(menuPanel,BorderLayout.CENTER);
        inventoryFrame.add(inventoryPanel,BorderLayout.SOUTH);

        // Create back button
        JButton backToManager = new JButton("Back to Manager Menu"); 
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        // Create restock report button
        JButton restockReport = new JButton("View Restock Report"); 
        restockReport.addActionListener(gui);
        menuPanel.add(restockReport);

        // Create excess report button
        JButton excessReport = new JButton("View Excess Report");
        excessReport.addActionListener(gui);
        menuPanel.add(excessReport);

        // Create add button
        JButton add = new JButton("Add Supply Item");
        add.addActionListener(gui);
        menuPanel.add(add);

        //create remove button
        JButton remove = new JButton("Remove Supply Item");
        remove.addActionListener(gui);
        menuPanel.add(remove);

        // Create scrollable table
        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        inventoryPanel.add(scroll);

        // Filling the table with database data
        managerLogic.getInventory(table);

        inventoryFrame.pack();
    }


    /**
     * Create Recent Orders Frame
     */
    public static void setUpRecentOrders(){
        // Initialize recent orderes frame
        recentFrame = new JFrame("RecentOrders");
        recentFrame.setSize(1000, 800);

        // Create top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Recent Orders");
        titlePanel.add(title);

        // Create middle panel (menu)
        JPanel menuPanel = new JPanel();
      //  menuPanel.setPreferredSize(new Dimension(1000,50));

        // Create bottom panel (content)
        JPanel recentPanel = new JPanel();
        recentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Frame layout
        recentFrame.add(titlePanel,BorderLayout.NORTH);
        recentFrame.add(menuPanel,BorderLayout.CENTER);
        recentFrame.add(recentPanel,BorderLayout.SOUTH);

        // Goes back to manager menu
        JButton backToManager = new JButton("Back to Manager Menu"); 
        backToManager.addActionListener(gui);
        titlePanel.add(backToManager);

        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        recentPanel.add(scroll);

        textArea = new JTextArea(100,70);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 15));
        menuPanel.add(textArea);

        // Populate table with data from database
        managerLogic.getRecentOrders(table,textArea);

        recentFrame.pack();
    }


    /**
     * Create Stats Frame
     */
    public static void setUpOrderStats(){
        // Initilize frame
        statsFrame = new JFrame("Order Statistics");
        statsFrame.setSize(1000, 800);
        statsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initilize top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Order Statistics");
        titlePanel.add(title);

        // Create middle panel (menu)
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        // Create bottom panel (content)
        JPanel statsPanel = new JPanel();

        // Frame layout
        statsFrame.add(titlePanel,BorderLayout.NORTH);
        statsFrame.add(menuPanel,BorderLayout.CENTER);
        statsFrame.add(statsPanel,BorderLayout.SOUTH);

        // Setup button that returns to manager menu
        JButton backToManager = new JButton("Back to Manager Menu"); 
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        // Button that displays daily stats
        JButton daily = new JButton("Daily Stats"); 
        daily.addActionListener(gui);
        menuPanel.add(daily);

        // Button that displays what pairs of items sells together in the same order
        JButton whatSalesTogether = new JButton("What Sales Together");
        whatSalesTogether.addActionListener(gui);
        menuPanel.add(whatSalesTogether);

        // Button that displays stats over a custom range
        JButton custom = new JButton("Custom Range"); 
        custom.addActionListener(gui);
        menuPanel.add(custom);
        JTextArea cRange = new JTextArea(currRange);
        menuPanel.add(cRange);

        // Sets up table; default is daily stats
        JScrollPane scroll = new JScrollPane(statsTable);
        statsPanel.add(scroll);

        statsFrame.pack();
    }


    /**
     * Creates a table that contains daily stats
     * @return a table that contains daily stats
     */
    public static JTable dailyStats(){
        JTable table = new JTable();
        // Calculates and displays the daily drinks sold and sales
        table = managerLogic.getDailyStats(table);
        return table;
    }

    /**
     * Creates a table that contains a custom range between start and end
     * @param start The start date
     * @param end The end date
     * @return a table that contains a custom range between start and end
     */
    public static JTable customRange(String start, String end){
        JTable table = new JTable();
        table = managerLogic.getCustomRange(table, start, end);
        return table;
    }


    /**
     * Sets up frame for menu editor
     */
    public static void setUpMenuEditor(){
        // Initialize frame
        editorFrame = new JFrame("Menu Editor");
        editorFrame.setSize(1000, 800);
        editorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initilize bottom panel (content)
        JPanel editorPanel = new JPanel();
        editorFrame.add(editorPanel);

        // Initialize top panel (title)
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(1000,50));
        JLabel title = new JLabel("Menu Editor");
        titlePanel.add(title);

        // Initialize middle panel (menu)
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(1000,50));

        // Frame layout
        editorFrame.add(titlePanel,BorderLayout.NORTH);
        editorFrame.add(menuPanel,BorderLayout.CENTER);
        editorFrame.add(editorPanel,BorderLayout.SOUTH);

        // Goes back to manager menu
        JButton backToManager = new JButton("Back to Manager Menu"); 
        backToManager.addActionListener(gui);
        menuPanel.add(backToManager);

        // Set up button that adds menu item
        JButton add = new JButton("Add Menu Item");
        add.addActionListener(gui);
        menuPanel.add(add);

        // Set up button that removes menu item
        JButton remove = new JButton("Remove Menu Item");
        remove.addActionListener(gui);
        menuPanel.add(remove);

        // Setup scrollable tabel
        JTable table = new JTable();
        JScrollPane  scroll = new JScrollPane(table);
        editorPanel.add(scroll);

        // Getting the data
        managerLogic.getMenu(table);

        editorFrame.pack();
    }


    /**
     * Set up employee selector
     */ 
    public static void setEmployeeComboBox(){
        // Loads in the names of the employees
        Vector<Employee> employees = new Vector<>(); 
        guiLogic.loadEmployees(employees);
        employeeSelector = new JComboBox<Employee>(employees);
        employeeSelector.setMaximumSize(new Dimension(200, 50));

        // Configures combobox options to be the employee names
        employeeSelector.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Employee) {
                    Employee employee = (Employee) value;
                    // Display the "name" attribute inthe combobox
                    value = employee.getName(); 
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
    }


    /**
     * Perform certain actions when a button is pressed
     * @param e the event occurred
     */
    public void actionPerformed(ActionEvent e){
        String event = e.getActionCommand();

        // Employee Enter
        if(event.equals("Enter")){
            // Caching drink prices
            drinkPriceMap = OrderLogic.fetchAllDrinkPrices();

            viewSelector(((Employee) employeeSelector.getSelectedItem()).isManager());
        }

        // Returns to Login from cashier page or manager menu
        if(event.equals("Back to Login")){
            changeFrame(startFrame);
        }
        else if(event.equals("Fruity and Refreshing")){
            System.out.println("FAR");
            changeFrame(createFruityRefreshingPage());
        }
        else if(event.equals("Sweet and Creamy")){
            System.out.println("SAC");
            changeFrame(createSweetAndCreamyPage());
        }
        else if(event.equals("Coffee Flavored")){
            System.out.println("CF");
            changeFrame(createCoffeeFlavoredPage());
        }
        else if(event.equals("Seasonal Drinks")){
            System.out.println("SD");
            changeFrame(createSeasonalDrinksPage());
        }
        else if(event.equals("Back to Manager Menu")){
            changeFrame(managerFrame);
        }
        // Opens inventory page
        else if(event.equals("View Inventory")){
            setUpInventory();
            changeFrame(inventoryFrame);
        }
        // Opens restock report
        else if(event.equals("View Inventory")){
            setUpInventory();
            changeFrame(inventoryFrame);
        }
        //Opens excess report
        else if(event.equals("View Inventory")){
            setUpInventory();
            changeFrame(inventoryFrame);
        }
        // Opens price editor
        else if(event.equals("Edit Menu")){
            setUpMenuEditor();
            changeFrame(editorFrame);
        }
        // Opens order stats
        else if(event.equals("Order Statistics")){
            statsTable = dailyStats();
            currRange = "Today";
            setUpOrderStats();
            changeFrame(statsFrame);
        }
        // Opens recent orders
        else if(event.equals("Recent Orders")){
            setUpRecentOrders();
            changeFrame(recentFrame);
        }
        // On order stats page, shows daily stats
        else if(event.equals("Daily Stats")){
            statsTable = dailyStats();
            currRange = "Today";
            setUpOrderStats();
            changeFrame(statsFrame);
        }
        // On order stats page, show stats for inputted range, input with TwoInputDialog
        // NEEDS TO BE FINISHED
        else if(event.equals("Custom Range")){
            // Using a custom 2 input dialog, get the two inputs
            TwoInputDialog dialog = new TwoInputDialog(currFrame,"Enter start date: YYYY-MM-DD","Enter end date: YYYY-MM-DD");
            TwoInputs inputs = dialog.showInputDialog();

            String start = inputs.input1;
            String end = inputs.input2;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Timestamp timestamp = new Timestamp(dateFormat.parse(start).getTime());
                statsTable = customRange(start,end);
                currRange = start + " to " + end;
                setUpOrderStats();
                changeFrame(statsFrame);
                
            } catch (ParseException | IllegalArgumentException exe) {
                JOptionPane.showMessageDialog(null, "You have entered an invalid date.\nTry Again.", "ERROR", JOptionPane.INFORMATION_MESSAGE);            
            } 
        }
        // On order stats page, show what sales together
        else if(event.equals("What Sales Together")) {
            setUpWhatSalesTogether();
            changeFrame(salesTogetherFrame);
        }
        // On inventory page, view the restock report
        else if(event.equals("View Restock Report")){
            // Update graphics
            setUpRestockReport();
            changeFrame(restockReportFrame);
        }
        // On inventory page, view the excess report
        else if(event.equals("View Excess Report")){
            // Update graphics
            setUpExcessReport();
            changeFrame(excessReportFrame);
        }
        // On inventory page, adds a supply item to the database
        else if(event.equals("Add Supply Item")){
            managerLogic.addSupplyItem(currFrame);
            // Update graphics
            setUpInventory();
            changeFrame(inventoryFrame);
        }
        //On inventory page, removes a supply item from the database
        else if(event.equals("Remove Supply Item")){
            managerLogic.removeSupplyItem();
            // Update graphics
            setUpInventory();
            changeFrame(inventoryFrame);
        }
        // On menu editor page, adds a menu item to the database
        else if(event.equals("Add Menu Item")){
            managerLogic.addMenuItem(currFrame);
            // Update graphics
            setUpMenuEditor();
            changeFrame(editorFrame);
        }
        // Remove a menu item
        else if(event.equals("Remove Menu Item")){
            managerLogic.removeMenuItem();
            // Update graphics
            setUpMenuEditor();
            changeFrame(editorFrame);
        }
    }


    /**
     * Logic to remove from order
     * @param drinkButton the button of the drink that is being removed from the order
     */
    private void removeFromOrder(JPanel drinkButtonPanel, JButton drinkButton){
        int buttonIndex = getButtonIndex(drinkButton);
        if(buttonIndex != -1){
            orderTotal -= drinkPriceMap.get(order.get(buttonIndex));
            System.out.println(orderTotal);
            int orderLogIndex = orderLogs.getComponentZOrder(drinkButtonPanel);
            orderLogs.remove(orderLogIndex);
            orderLogs.remove(orderLogIndex);
            order.remove(buttonIndex);
            drinkAttributes.remove(buttonIndex);
            drinkAddons.remove(buttonIndex);
            drinkButtons.remove(buttonIndex);
            openButtons.remove(buttonIndex);
            drinkButtonPanels.remove(buttonIndex);
            orderLogs.revalidate();
            orderLogs.repaint();
            String formattedOrderTotal = String.format("%.2f", orderTotal);
            payButton.setText("Pay: $" + formattedOrderTotal);
        }
    }


    /**
     * Finds the button's index
     * @param targetButton
     * @return the button's index
     */
    private int getButtonIndex(JButton targetButton){
        for (int i = 0; i < drinkButtons.size(); i++){
            if (drinkButtons.get(i) == targetButton){
                return i;
            }
        }
        return -1;
    }


    /**
     * A function to display the stats of a drink (dairy free alternative, sweetness level, ice level, cup size, special instructions)
     * @param drinkButtonPanel
     * @param drinkButton
     */
    private void displayDrinkAttributes(JPanel drinkButtonPanel, JButton drinkButton){
        // Adding the attributes underneath the drinkButton
        int buttonIndex = getButtonIndex(drinkButton);

        if(buttonIndex != -1){
            int orderLogIndex = orderLogs.getComponentZOrder(drinkButtonPanel);
            // Toggle for displaying the attributes
            if(openButtons.get(buttonIndex) == true){
                openButtons.set(buttonIndex, false);
                orderLogs.remove(orderLogIndex + 1);
            }
            else{
                openButtons.set(buttonIndex, true);
                JPanel attributesPanel = new JPanel();
                // Set the layout manager to BoxLayout with X_AXIS (horizontal) alignment
                attributesPanel.setLayout(new BoxLayout(attributesPanel, BoxLayout.Y_AXIS));
                // AttributesPanel.setLayout(new GridLayout(6, 1));
                
                JPanel dairyAttribute = new JPanel();
                dairyAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));
                JLabel dairyLabel = new JLabel(drinkAttributes.get(buttonIndex).split(", ")[0]);
                dairyLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                dairyAttribute.add(dairyLabel);

                JButton dairyOatButton = new JButton("Oat");
                dairyOatButton.setBackground(Color.BLUE);
                dairyOatButton.setForeground(Color.WHITE);
                dairyOatButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding attributes for oat milk
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = "Dairy Free Alternative: Oat Milk";
                        for (int i = 1; i < attributes.length; i++) {
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        dairyLabel.setText(newAttributes.split(", ")[0]);
                    }
                });

                JButton dairySoyButton = new JButton("Soy");
                dairySoyButton.setBackground(Color.BLUE);
                dairySoyButton.setForeground(Color.WHITE);
                dairySoyButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding attributes for soy milk
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = "Dairy Free Alternative: Soy Milk";
                        for(int i = 1; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        dairyLabel.setText(newAttributes.split(", ")[0]);
                    }
                });

                JButton dairyLactoseFreeButton = new JButton("Lactose Free");
                dairyLactoseFreeButton.setBackground(Color.BLUE);
                dairyLactoseFreeButton.setForeground(Color.WHITE);
                dairyLactoseFreeButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding attributes for lactose free milk
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = "Dairy Free Alternative: Lactose Free Milk";
                        for(int i = 1; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        dairyLabel.setText(newAttributes.split(", ")[0]);
                    }
                });

                JButton dairyNoneButton = new JButton("None");
                dairyNoneButton.setBackground(Color.BLUE);
                dairyNoneButton.setForeground(Color.WHITE);
                dairyNoneButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding attributes for none
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = "Dairy Free Alternative: None";
                        for(int i = 1; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        dairyLabel.setText(newAttributes.split(", ")[0]);
                    }
                });
                
                dairyAttribute.add(dairyOatButton);
                dairyAttribute.add(dairySoyButton);
                dairyAttribute.add(dairyLactoseFreeButton);
                dairyAttribute.add(dairyNoneButton);

                JPanel sweetnessAttribute = new JPanel();
                sweetnessAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel sweetnessLabel = new JLabel(drinkAttributes.get(buttonIndex).split(", ")[1]);
                sweetnessLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                sweetnessAttribute.add(sweetnessLabel);

                JButton sweetness50Button = new JButton("50%");
                sweetness50Button.setBackground(Color.BLUE);
                sweetness50Button.setForeground(Color.WHITE);
                sweetness50Button.addActionListener(new ActionListener(){

                    /**
                     * Action listener for modifying the sweetness level to 50%
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", Sweetness Level: 50%";
                        for(int i = 2; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        sweetnessLabel.setText(newAttributes.split(", ")[1]);
                    }
                });

                JButton sweetness100Button = new JButton("100%");
                sweetness100Button.setBackground(Color.BLUE);
                sweetness100Button.setForeground(Color.WHITE);
                sweetness100Button.addActionListener(new ActionListener(){

                    /**
                     * Action listener for modifying the sweetness level to 100%
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", Sweetness Level: 100%";
                        for(int i = 2; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        sweetnessLabel.setText(newAttributes.split(", ")[1]);
                    }
                });

                sweetnessAttribute.add(sweetness50Button);
                sweetnessAttribute.add(sweetness100Button);

                JPanel iceAttribute = new JPanel();
                iceAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel iceLabel = new JLabel(drinkAttributes.get(buttonIndex).split(", ")[2]);
                iceLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                iceAttribute.add(iceLabel);

                JButton iceLessButton = new JButton("Less Ice");
                iceLessButton.setBackground(Color.BLUE);
                iceLessButton.setForeground(Color.WHITE);
                iceLessButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for modifying the ice level to less
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", Ice Level: Less";
                        for(int i = 3; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        iceLabel.setText(newAttributes.split(", ")[2]);
                    }
                });

                JButton iceNormalButton = new JButton("Normal");
                iceNormalButton.setBackground(Color.BLUE);
                iceNormalButton.setForeground(Color.WHITE);

                iceNormalButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for modifying the ice level to normal
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", Ice Level: Normal";
                        for(int i = 3; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        iceLabel.setText(newAttributes.split(", ")[2]);
                    }
                });

                JButton iceNoneButton = new JButton("None");
                iceNoneButton.setBackground(Color.BLUE);
                iceNoneButton.setForeground(Color.WHITE);

                iceNoneButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for modifying the ice level to none
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", Ice Level: None";
                        for(int i = 3; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        iceLabel.setText(newAttributes.split(", ")[2]);
                    }
                });

                iceAttribute.add(iceLessButton);
                iceAttribute.add(iceNormalButton);
                iceAttribute.add(iceNoneButton);

                JPanel cupSizeAttribute = new JPanel();
                cupSizeAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel cupSizeLabel = new JLabel(drinkAttributes.get(buttonIndex).split(", ")[3]);
                cupSizeLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                cupSizeAttribute.add(cupSizeLabel);

                JButton cupSizeRegularButton = new JButton("Regular");
                cupSizeRegularButton.setBackground(Color.BLUE);
                cupSizeRegularButton.setForeground(Color.WHITE);

                cupSizeRegularButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for modifying the cup size to regular
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", Cup Size: Regular";
                        for(int i = 4; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        cupSizeLabel.setText(newAttributes.split(", ")[3]);
                    }
                });

                JButton cupSizeRegularHot = new JButton("Regular Hot");
                cupSizeRegularHot.setBackground(Color.BLUE);
                cupSizeRegularHot.setForeground(Color.WHITE);

                cupSizeRegularHot.addActionListener(new ActionListener(){

                    /**
                     * Action listener for modifying the cup size to regular hot
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", Cup Size: Regular Hot";
                        for(int i = 4; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        cupSizeLabel.setText(newAttributes.split(", ")[3]);
                    }
                });

                JButton cupSizeXLButton = new JButton("XL");
                cupSizeXLButton.setBackground(Color.BLUE);
                cupSizeXLButton.setForeground(Color.WHITE);

                cupSizeXLButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for modifying the cup size to XL
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e) {
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", Cup Size: XL";
                        for(int i = 4; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAttributes.set(buttonIndex, newAttributes);
                        cupSizeLabel.setText(newAttributes.split(", ")[3]);
                    }
                });

                cupSizeAttribute.add(cupSizeRegularButton);
                cupSizeAttribute.add(cupSizeRegularHot);
                cupSizeAttribute.add(cupSizeXLButton);
                
                JPanel specialInstructionsPanel = new JPanel();
                specialInstructionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

                JTextArea specialInstructionsTextArea = new JTextArea(drinkAttributes.get(buttonIndex).split(", ")[4]);
                specialInstructionsTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
                specialInstructionsTextArea.setLineWrap(true);
                specialInstructionsTextArea.setWrapStyleWord(true);
                specialInstructionsTextArea.setPreferredSize(new Dimension(300, 100));

                // Submit special instructions to put into arraylist
                JButton submitSpecialInstructionsButton = new JButton("Submit Special Instructions");
                submitSpecialInstructionsButton.setBackground(Color.BLUE);
                submitSpecialInstructionsButton.setForeground(Color.WHITE);

                submitSpecialInstructionsButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding special instructions
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAttributes.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", " + attributes[3] + ", Special Instructions: " + specialInstructionsTextArea.getText();
                        drinkAttributes.set(buttonIndex, newAttributes);
                        specialInstructionsTextArea.setText(newAttributes.split(", ")[4]);
                    }
                });

                specialInstructionsPanel.add(specialInstructionsTextArea);
                specialInstructionsPanel.add(submitSpecialInstructionsButton);

                // Start of addons buttons
                // Create a panel for Boba attribute

                JPanel bobaAttribute = new JPanel();
                bobaAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel bobaLabel = new JLabel("Extra Boba: None");
                bobaLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                bobaAttribute.add(bobaLabel);

                JButton addBobaButton = new JButton("+");
                addBobaButton.setBackground(Color.BLUE);
                addBobaButton.setForeground(Color.WHITE);
                addBobaButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding boba
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = "Extra Boba: Added";
                        for(int i = 1; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        bobaLabel.setText(newAttributes.split(", ")[0]);
                    }
                });

                JButton removeBobaButton = new JButton("-");
                removeBobaButton.setBackground(Color.BLUE);
                removeBobaButton.setForeground(Color.WHITE);
                removeBobaButton.addActionListener(new ActionListener() {

                    /**
                     * Action listener for having no boba
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e) {
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = "Extra Boba: None";
                        for(int i = 1; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        bobaLabel.setText(newAttributes.split(", ")[0]);
                    }
                });

                bobaAttribute.add(addBobaButton);
                bobaAttribute.add(removeBobaButton);

                //Create a panel for Tiger Pearls
                JPanel tigerPearlsAttribute = new JPanel();
                tigerPearlsAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));

                // Create a label to display the current Tiger Pearls selection
                JLabel tigerPearlsLabel = new JLabel("Tiger Pearls: None");
                tigerPearlsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                tigerPearlsAttribute.add(tigerPearlsLabel);

                // Create buttons for adding and removing Tiger Pearls
                JButton addTigerPearlsButton = new JButton("+");
                addTigerPearlsButton.setBackground(Color.BLUE);
                addTigerPearlsButton.setForeground(Color.WHITE);
                addTigerPearlsButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding tiger pearls
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", Tiger Pearls: Added";
                        for(int i = 2; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        tigerPearlsLabel.setText(newAttributes.split(", ")[1]);
                    }
                });

                JButton removeTigerPearlsButton = new JButton("-");
                removeTigerPearlsButton.setBackground(Color.BLUE);
                removeTigerPearlsButton.setForeground(Color.WHITE);
                removeTigerPearlsButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for having no tiger pearls
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", Tiger Pearls: None";
                        for(int i = 2; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        tigerPearlsLabel.setText(newAttributes.split(", ")[1]);
                    }
                });

                tigerPearlsAttribute.add(addTigerPearlsButton);
                tigerPearlsAttribute.add(removeTigerPearlsButton);

                // Create a panel for Cream Mousse
                JPanel creamMousseAttribute = new JPanel();
                creamMousseAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel creamMousseLabel = new JLabel("Cream Mousse: None");
                creamMousseLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                creamMousseAttribute.add(creamMousseLabel);

                JButton addCreamMousseButton = new JButton("+");
                addCreamMousseButton.setBackground(Color.BLUE);
                addCreamMousseButton.setForeground(Color.WHITE);
                addCreamMousseButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding cream mousse
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", Cream Mousse: Added";
                        for(int i = 3; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        creamMousseLabel.setText(newAttributes.split(", ")[2]);
                    }
                });

                JButton removeCreamMousseButton = new JButton("-");
                removeCreamMousseButton.setBackground(Color.BLUE);
                removeCreamMousseButton.setForeground(Color.WHITE);
                removeCreamMousseButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for having no cream mousse
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", Cream Mousse: None";
                        for(int i = 3; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        creamMousseLabel.setText(newAttributes.split(", ")[2]);
                    }
                });

                creamMousseAttribute.add(addCreamMousseButton);
                creamMousseAttribute.add(removeCreamMousseButton);

                // Create a panel for Taro
                JPanel taroAttribute = new JPanel();
                taroAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel taroLabel = new JLabel("Taro: None");
                taroLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                taroAttribute.add(taroLabel);

                JButton addTaroButton = new JButton("+");
                addTaroButton.setBackground(Color.BLUE);
                addTaroButton.setForeground(Color.WHITE);
                addTaroButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding taro
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] +", Taro: Added";
                        for(int i = 4; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        taroLabel.setText(newAttributes.split(", ")[3]);
                    }
                });

                JButton removeTaroButton = new JButton("-");
                removeTaroButton.setBackground(Color.BLUE);
                removeTaroButton.setForeground(Color.WHITE);
                removeTaroButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for having no taro
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] +", Taro: None";
                        for(int i = 4; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        taroLabel.setText(newAttributes.split(", ")[3]);
                    }
                });

                taroAttribute.add(addTaroButton);
                taroAttribute.add(removeTaroButton);

                // Create a panel for Red Bean
                JPanel redBeanAttribute = new JPanel();
                redBeanAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel redBeanLabel = new JLabel("Red Bean: None");
                redBeanLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                redBeanAttribute.add(redBeanLabel);

                JButton addRedBeanButton = new JButton("+");
                addRedBeanButton.setBackground(Color.BLUE);
                addRedBeanButton.setForeground(Color.WHITE);
                addRedBeanButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding red beans
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", " + attributes[3] + ", Red Bean: Added";
                        for(int i = 5; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        redBeanLabel.setText(newAttributes.split(", ")[4]);
                    }
                });

                JButton removeRedBeanButton = new JButton("-");
                removeRedBeanButton.setBackground(Color.BLUE);
                removeRedBeanButton.setForeground(Color.WHITE);
                removeRedBeanButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for having no red beans
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", " + attributes[3] + ", Red Bean: None";
                        for(int i = 5; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        redBeanLabel.setText(newAttributes.split(", ")[4]);
                    }
                });

                redBeanAttribute.add(addRedBeanButton);
                redBeanAttribute.add(removeRedBeanButton);

                // Create a panel for Pudding
                JPanel puddingAttribute = new JPanel();
                puddingAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel puddingLabel = new JLabel("Pudding: None");
                puddingLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                puddingAttribute.add(puddingLabel);

                JButton addPuddingButton = new JButton("+");
                addPuddingButton.setBackground(Color.BLUE);
                addPuddingButton.setForeground(Color.WHITE);
                addPuddingButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding pudding
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e) {
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", " + attributes[3] + ", " + attributes[4] + ", Pudding: Added";
                        for(int i = 6; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        puddingLabel.setText(newAttributes.split(", ")[5]);
                    }
                });

                JButton removePuddingButton = new JButton("-");
                removePuddingButton.setBackground(Color.BLUE);
                removePuddingButton.setForeground(Color.WHITE);
                removePuddingButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for having no pudding
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", " + attributes[3] + ", " + attributes[4] + ", Pudding: None";
                        for(int i = 6; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        puddingLabel.setText(newAttributes.split(", ")[5]);
                    }
                });

                puddingAttribute.add(addPuddingButton);
                puddingAttribute.add(removePuddingButton);

                // Create a panel for Mochi
                JPanel mochiAttribute = new JPanel();
                mochiAttribute.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel mochiLabel = new JLabel("Mochi: None");
                mochiLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                mochiAttribute.add(mochiLabel);

                JButton addMochiButton = new JButton("+");
                addMochiButton.setBackground(Color.BLUE);
                addMochiButton.setForeground(Color.WHITE);
                addMochiButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for adding mochi
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", " + attributes[3] + ", " + attributes[4] + ", " + attributes[5] + ", Mochi: Added";
                        for(int i = 7; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        mochiLabel.setText(newAttributes.split(", ")[6]);
                    }
                });

                JButton removeMochiButton = new JButton("-");
                removeMochiButton.setBackground(Color.BLUE);
                removeMochiButton.setForeground(Color.WHITE);
                removeMochiButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for having no mochi
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e) {
                        String[] attributes = drinkAddons.get(buttonIndex).split(", ");
                        String newAttributes = attributes[0] + ", " + attributes[1] + ", " + attributes[2] + ", " + attributes[3] + ", " + attributes[4] + ", " + attributes[5] + ", Mochi: None";
                        for(int i = 7; i < attributes.length; i++){
                            newAttributes += ", " + attributes[i];
                        }
                        drinkAddons.set(buttonIndex, newAttributes);
                        mochiLabel.setText(newAttributes.split(", ")[6]);
                    }
                });

                mochiAttribute.add(addMochiButton);
                mochiAttribute.add(removeMochiButton);

                attributesPanel.add(bobaAttribute);
                attributesPanel.add(tigerPearlsAttribute);

                // Button to remove the drink entirely from the order
                JPanel removeDrinkPanel = new JPanel();
                removeDrinkPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

                JButton removeDrinkButton = new JButton("Remove Drink");
                removeDrinkButton.setBackground(Color.RED);
                removeDrinkButton.setForeground(Color.WHITE);

                removeDrinkButton.addActionListener(new ActionListener(){

                    /**
                     * Action listener for removing drink from order
                     * @param e
                     */
                    public void actionPerformed(ActionEvent e){
                        removeFromOrder(drinkButtonPanel, drinkButton);
                    }
                });
                
                removeDrinkPanel.add(removeDrinkButton);

                JPanel addonsPanel = new JPanel();
                addonsPanel.setLayout(new GridLayout(4, 2));
                addonsPanel.add(bobaAttribute);
                addonsPanel.add(tigerPearlsAttribute);
                addonsPanel.add(creamMousseAttribute);
                addonsPanel.add(taroAttribute);
                addonsPanel.add(redBeanAttribute);
                addonsPanel.add(puddingAttribute);
                addonsPanel.add(mochiAttribute);

                attributesPanel.add(dairyAttribute);
                attributesPanel.add(sweetnessAttribute);
                attributesPanel.add(iceAttribute);
                attributesPanel.add(cupSizeAttribute);
                attributesPanel.add(specialInstructionsPanel);
                attributesPanel.add(addonsPanel);
                attributesPanel.add(removeDrinkPanel);
                orderLogs.add(attributesPanel, orderLogIndex + 1);
            }

            // Revalidate and repaint the container
            orderLogs.revalidate();
            orderLogs.repaint();
            rightPanel.revalidate();
            rightPanel.repaint();
        } 
        else{
            System.out.println("Button not found in orderLogs.");
        }
    }


    /**
     * Handle adding a drink to the order list
     * @param drinkName the name of the drink that is being added on the order
     */
    private void addToOrder(String drinkName){
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());

        drinkButtonPanels.add(buttonPanel);

        orderTotal += drinkPriceMap.get(drinkName);

        JButton drinkButton = new JButton(drinkName + " ($" + String.format("%.2f", drinkPriceMap.get(drinkName)) + ")");

        drinkButton.addActionListener(new ActionListener(){

            /**
             * Action listener for displaying drink attributes
             * @param e
             */
            public void actionPerformed(ActionEvent e){
                displayDrinkAttributes(buttonPanel, drinkButton);
            }
        });

        buttonPanel.add(drinkButton);

        orderLogs.add(buttonPanel, BorderLayout.WEST);
        orderLogs.revalidate();
        orderLogs.repaint();

        String formattedOrderTotal = String.format("%.2f", orderTotal);
        System.out.println("Order total: " + formattedOrderTotal);

        payButton.setText("Charge $" + formattedOrderTotal);

        // Adding to arraylist of drinks in order
        order.add(drinkName);
        drinkButtons.add(drinkButton);
        openButtons.add(false);
        
        // Getting default attributes
        drinkAttributes.add("Dairy Free Alternative: None, Sweetness Level: 100%, Ice Level: Normal, Cup Size: Regular, Special Instructions: None");

        drinkAddons.add("Extra Boba: None, Tiger Pearls: None, Cream Mousse: None, Taro: None, Red Bean: None, Pudding: None, Mochi: None");
    }

    /**
     * Logic for when completing the order
     */
    private void completeOrder(){
        ArrayList<String> outOfStock = OrderLogic.placeOrder(1, 1, order.toArray(new String[order.size()]), orderTotal, drinkAttributes.toArray(new String[drinkAttributes.size()]), drinkAddons.toArray(new String[drinkAddons.size()]));
        
        if(outOfStock.size() > 0){
            String outOfStockString = "";
            for(int i = 0; i < outOfStock.size(); i++){
                outOfStockString += outOfStock.get(i) + "\n";
            }
            JOptionPane.showMessageDialog(null, "The following items are out of stock:\n" + outOfStockString);
        }
        else{
            JOptionPane.showMessageDialog(null, "Order placed successfully!");
            order.clear();
            orderLogs.removeAll();
            drinkButtonPanels.clear();
            drinkButtons.clear();
            openButtons.clear();
            drinkAttributes.clear();
            drinkAddons.clear();
            orderTotal = 0.0;
        }

        orderLogs.revalidate();
        orderLogs.repaint();
        String formattedOrderTotal = String.format("%.2f", orderTotal);
        payButton.setText("Charge $" + formattedOrderTotal);
    }


    /**
     * Displays either the cashier view or the manager view based on combobox selection
     * @param manager true if manager view, false if cashier view
     */
    public static void viewSelector(boolean manager){
        startFrame.setVisible(false);
        // Go to manager view
        if(manager){ 
            // Setup manager frame
            managerFrame = new JFrame("Manager Display");
            managerFrame.setSize(1000, 800);
            JPanel managerPanel = new JPanel();
            managerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            managerFrame.add(managerPanel);

            // Setup back to login page button
            JButton backToLogin = new JButton("Back to Login"); 
            backToLogin.addActionListener(gui);
            managerPanel.add(backToLogin);

            // Setup view inventory button
            JButton viewInventory = new JButton("View Inventory");
            viewInventory.addActionListener(gui);
            managerPanel.add(viewInventory);

            // Setup menu editor button
            JButton editMenu = new JButton("Edit Menu");
            editMenu.addActionListener(gui);
            managerPanel.add(editMenu);

            // Setup order stats button
            JButton orderStats = new JButton("Order Statistics"); 
            orderStats.addActionListener(gui);
            managerPanel.add(orderStats);

            // Setup recent orders button
            JButton recentOrders = new JButton("Recent Orders"); 
            recentOrders.addActionListener(gui);
            managerPanel.add(recentOrders);

            // Switch frame
            changeFrame(managerFrame);
        }
        else{
            GUI guiInstance = new GUI();
            JFrame cashierFrame = guiInstance.createSweetAndCreamyPage();
            changeFrame(cashierFrame);
        }
    }

    /**
     * Function for styling button
     * @param text
     * @return the styled button
     */
    private static JButton StyledButton(String text){
        JButton button = new JButton("<html><center>" + text + "</center></html>", null);
        button.setFont(new Font("Roboto", Font.PLAIN, 20));
        button.setFocusPainted(false);
        button.setBackground(new Color(39, 41, 53));
        
        button.setBorder(BorderFactory.createEmptyBorder());

        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setForeground(Color.WHITE);

        // Hover Mechanics
        button.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent evt){
                button.setBackground(new Color(69, 73, 96));
            }

            public void mouseExited(java.awt.event.MouseEvent evt){
                button.setBackground(new Color(39, 41, 53));
            }
        });
        return button;
    }

    /**
     * Fruity and refreshing page
     * @return JFrame of the fruity and refreshing page
     */
    public JFrame createFruityRefreshingPage() {
        JFrame fruityRefreshingFrame = new JFrame("Fruity and Refreshing");
        fruityRefreshingFrame.setSize(1000, 800);
        fruityRefreshingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Font titleButtonFont = new Font("Roboto", Font.BOLD, 24);

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
        fruityButton.setForeground(new Color(238, 212, 132));
        fruityButton.setBackground(new Color(39, 41, 54));
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

        ArrayList<String> drinks = OrderLogic.fetchDrinksByType("Fruity and Refreshing");
        for(String drink : drinks){
            JButton drinkButton = StyledButton(drink + " $" + String.format("%.2f", drinkPriceMap.get(drink)));
            drinkButton.addActionListener(new ActionListener(){

                /**
                 * Action listener for adding drink to order
                 * @param e
                 */
                public void actionPerformed(ActionEvent e){
                    addToOrder(drink);
                }
            });
            contentPanel.add(drinkButton);
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Right Panel for orders

        rightPanel = new JPanel(new BorderLayout());

        JLabel orderListLabel = new JLabel("Order List");
        orderListLabel.setFont(new Font("Arial", Font.BOLD, 24));
        orderListLabel.setHorizontalAlignment(JLabel.CENTER);
        rightPanel.add(orderListLabel, BorderLayout.NORTH);

        //  Order Text
        orderLogs = new JPanel();
        orderLogs.setFont(new Font("Arial", Font.PLAIN, 16));
        orderLogs.setLayout(new BoxLayout(orderLogs, BoxLayout.Y_AXIS));

        // Populating orderlogs if orders already exist
        for(JPanel drinkButtonPanel : drinkButtonPanels){
            orderLogs.add(drinkButtonPanel, BorderLayout.WEST);
            orderLogs.revalidate();
            orderLogs.repaint();
        }

        JScrollPane orderScrollPane = new JScrollPane(orderLogs);
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);

        String formattedOrderTotal = String.format("%.2f", orderTotal);
        payButton = new JButton("Charge $" + formattedOrderTotal);
        payButton.setFont(new Font("Arial", Font.BOLD, 20));
        payButton.addActionListener(new ActionListener(){

            /**
             * Action listener for completing the order
             * @param e
             */
            public void actionPerformed(ActionEvent e){
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

    /**
     * Sweet and creamy page
     * @return JFrame of the sweet and creamy page
     */
    public JFrame createSweetAndCreamyPage() {
        JFrame sweetAndCreamyFrame = new JFrame("Sweet and Creamy");
        sweetAndCreamyFrame.setSize(1000, 800);
        sweetAndCreamyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Left Nav panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Drink Type Buttons
        JButton creamyButton = StyledButton("Sweet and Creamy");
        creamyButton.setActionCommand("Sweet and Creamy");
        creamyButton.addActionListener(gui);
        creamyButton.setForeground(new Color(238, 212, 132));
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

        ArrayList<String> drinks = OrderLogic.fetchDrinksByType("Sweet and Creamy");
        for(String drink : drinks){
            JButton drinkButton = StyledButton(drink + " $" + String.format("%.2f", drinkPriceMap.get(drink)));
            drinkButton.addActionListener(new ActionListener(){
                
                /**
                 * Action listener for adding drink to order
                 * @param e
                 */
                public void actionPerformed(ActionEvent e){
                    addToOrder(drink);
                }
            });
            contentPanel.add(drinkButton);
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Right Panel for orders

        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JLabel orderListLabel = new JLabel("Order List");
        orderListLabel.setFont(new Font("Arial", Font.BOLD, 24));
        orderListLabel.setHorizontalAlignment(JLabel.CENTER);
        rightPanel.add(orderListLabel, BorderLayout.NORTH);

        // Order Text
        orderLogs = new JPanel();
        orderLogs.setFont(new Font("Arial", Font.PLAIN, 16));
        orderLogs.setLayout(new BoxLayout(orderLogs, BoxLayout.Y_AXIS));

        // Populating orderlogs if orders already exist
        for(JPanel drinkButtonPanel : drinkButtonPanels){
            orderLogs.add(drinkButtonPanel, BorderLayout.WEST);
            orderLogs.revalidate();
            orderLogs.repaint();
        }

        JScrollPane orderScrollPane = new JScrollPane(orderLogs);
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);

        String formattedOrderTotal = String.format("%.2f", orderTotal);
        payButton = new JButton("Charge $" + formattedOrderTotal);
        payButton.setFont(new Font("Arial", Font.BOLD, 20));
        payButton.addActionListener(new ActionListener(){

            /**
             * Action listener for completing the order
             * @param e
             */
            public void actionPerformed(ActionEvent e){
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

    /**
     * Coffee flavored page
     * @return JFrame of the coffee flavored page
     */
    public JFrame createCoffeeFlavoredPage() {
        JFrame coffeeFlavoredFrame = new JFrame("Coffee Flavored");
        coffeeFlavoredFrame.setSize(1000, 800);
        coffeeFlavoredFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

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
        coffeeButton.setForeground(new Color(238, 212, 132));
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

        ArrayList<String> drinks = OrderLogic.fetchDrinksByType("Coffee Flavored");
        for(String drink : drinks){
            JButton drinkButton = StyledButton(drink + " $" + String.format("%.2f", drinkPriceMap.get(drink)));
            drinkButton.addActionListener(new ActionListener(){
                
                /**
                 * Action listener for adding to order
                 * @param e
                 */
                public void actionPerformed(ActionEvent e) {
                    addToOrder(drink);
                }
            });
            contentPanel.add(drinkButton, BorderLayout.CENTER);
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Right Panel for orders

        rightPanel = new JPanel(new BorderLayout());

        JLabel orderListLabel = new JLabel("Order List");
        orderListLabel.setFont(new Font("Arial", Font.BOLD, 24));
        orderListLabel.setHorizontalAlignment(JLabel.CENTER);
        rightPanel.add(orderListLabel, BorderLayout.NORTH);

        // Order Text
        orderLogs = new JPanel();
        orderLogs.setFont(new Font("Arial", Font.PLAIN, 16));
        orderLogs.setLayout(new BoxLayout(orderLogs, BoxLayout.Y_AXIS));

        // Populating orderlogs if orders already exist
        for(JPanel drinkButtonPanel : drinkButtonPanels){
            orderLogs.add(drinkButtonPanel, BorderLayout.WEST);
            orderLogs.revalidate();
            orderLogs.repaint();
        }
        JScrollPane orderScrollPane = new JScrollPane(orderLogs);
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);

        String formattedOrderTotal = String.format("%.2f", orderTotal);
        payButton = new JButton("Charge $" + formattedOrderTotal);
        payButton.setFont(new Font("Arial", Font.BOLD, 20));
        payButton.addActionListener(new ActionListener(){

            /**
             * Action listener for completing the order
             * @param e
             */
            public void actionPerformed(ActionEvent e){
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

    /**
     * Seasonal drinks page
     * @return JFrame of the seasonal drinks page
     */
    public JFrame createSeasonalDrinksPage(){
        JFrame seasonalDrinksFrame = new JFrame("Seasonal Drinks");
        seasonalDrinksFrame.setSize(1000, 800);
        seasonalDrinksFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

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
        seasonalButton.setForeground(new Color(238, 212, 132));


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

        ArrayList<String> drinks = OrderLogic.fetchDrinksByType("Seasonal Drinks");
        for(String drink : drinks){
            JButton drinkButton = StyledButton(drink + " $" + String.format("%.2f", drinkPriceMap.get(drink)));
            drinkButton.addActionListener(new ActionListener(){
                
                /**
                 * Action listener for adding to order
                 * @param e
                 */
                public void actionPerformed(ActionEvent e){
                    addToOrder(drink);
                }
            });
            contentPanel.add(drinkButton, BorderLayout.CENTER);
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Right Panel for orders

        rightPanel = new JPanel(new BorderLayout());

        JLabel orderListLabel = new JLabel("Order List");
        orderListLabel.setFont(new Font("Arial", Font.BOLD, 24));
        orderListLabel.setHorizontalAlignment(JLabel.CENTER);
        rightPanel.add(orderListLabel, BorderLayout.NORTH);

        // Order Text
        orderLogs = new JPanel();
        orderLogs.setFont(new Font("Arial", Font.PLAIN, 16));
        orderLogs.setLayout(new BoxLayout(orderLogs, BoxLayout.Y_AXIS));

        // Populating orderlogs if orders already exist
        for(JPanel drinkButtonPanel : drinkButtonPanels){
            orderLogs.add(drinkButtonPanel, BorderLayout.WEST);
            orderLogs.revalidate();
            orderLogs.repaint();
        }

        JScrollPane orderScrollPane = new JScrollPane(orderLogs);
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);

        String formattedOrderTotal = String.format("%.2f", orderTotal);
        payButton = new JButton("Charge $" + formattedOrderTotal);
        payButton.setFont(new Font("Arial", Font.BOLD, 20));
        payButton.addActionListener(new ActionListener(){

            /**
             * Action listener for completing the order
             * @param e
             */
            public void actionPerformed(ActionEvent e){
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
    
    /**
     * Main function
     * @param args command line arguments
     */
    public static void main(String[] args){
        // Connect to Database
        connect();

        // Setup Frame
        frameSetup();
        setUpInventory();
        setUpRecentOrders();
        setUpMenuEditor();
    }
}