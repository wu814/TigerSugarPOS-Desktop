import java.sql.*;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.util.*;


public class GUI extends JFrame implements ActionListener {

    static Connection conn; //database connection

    static JFrame startFrame; 
    static JFrame currFrame;
    static JFrame prevFrame;
    static GUI s;
    static JPanel p; 
    static JTextArea hello; //text area for testing
    static JComboBox<Employee> employeeSelector; //drop down for employees, how we know to go in cashier view or  manager view
    static JButton employeeEnter;//locks in combobox entry
    static JButton backToLogin; //back button that returns to employee select
    static JTextArea orderLogs;
    static ArrayList<String> order = new ArrayList<String>();

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
      //initalize componenets
      s = new GUI();
      p = new JPanel();
      hello = new JTextArea();

      startFrame.add(p);

  

      p.add(hello);

      //ComboBox for Employees
      setEmployeeComboBox();
      p.add(employeeSelector);

      //setup enter button
      employeeEnter = new JButton("Enter"); 
      employeeEnter.addActionListener(s);
      
      p.add(employeeEnter);
      currFrame = startFrame;
      currFrame.setVisible(true);
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

    public static void main(String[] args)
    {
      //Connect to Database
      connect();

      //Setup Frame
      frameSetup();
     
      
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
        String s = e.getActionCommand();

        //Employee Enter
        if (s.equals("Enter")) {
            viewSelector(((Employee) employeeSelector.getSelectedItem()).isManager());
        }
        else if(s.equals("Back to Login")){
          System.out.println("ASF");
          changeFrame(startFrame);
        }
        else if (s.equals("Fruity and Refreshing")) {
          System.out.println("FAR");
          changeFrame(createFruityRefreshingPage());
        }
        else if (s.equals("Sweet and Creamy")) {
          System.out.println("SAC");
          changeFrame(createSweetAndCreamyPage());
        }
        else if (s.equals("Coffee Flavored")) {
          System.out.println("CF");
          changeFrame(createCoffeeFlavoredPage());
        }

      
    }

    // handle adding a drink to the order list
    private void addToOrder(String drinkName) {
        orderLogs.setText(orderLogs.getText() + "\n" + drinkName);

        // adding to arraylist of drinks in order
        order.add(drinkName);
    }

    //displays either the cashier view or the manager view based on combobox selection
    public static void viewSelector(boolean manager){
      startFrame.setVisible(false);
      if(manager){
        JFrame cashierFrame = new JFrame("Manager Display");
        cashierFrame.setSize(300, 100);
        cashierFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(s);
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
        creamyButton.addActionListener(s);
        JButton fruityButton = StyledButton("Fruity and Refreshing");
        fruityButton.setActionCommand("Fruity and Refreshing");
        fruityButton.addActionListener(s);
        JButton coffeeButton = StyledButton("Coffee Flavored");
        coffeeButton.setActionCommand("Coffee Flavored");
        coffeeButton.addActionListener(s);

        navPanel.add(creamyButton);
        navPanel.add(fruityButton);
        navPanel.add(coffeeButton);

        navPanel.add(Box.createVerticalGlue());
        backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(s);
        navPanel.add(backToLogin);
        
       
        mainPanel.add(navPanel, BorderLayout.WEST);

        // Content Panel for drinks
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridLayout(3, 2, 20, 20));

        JButton drinkButton1 = StyledButton("Taro Bubble Tea");
        drinkButton1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Taro Bubble Tea");
          }
        });
        JButton drinkButton2 = StyledButton("Tiger Mango Sago");
        drinkButton2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Tiger Mango Sago");
          }
        });
        JButton drinkButton3 = StyledButton("Passion Fruit Tea");
        drinkButton3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Passion Fruit Tea");
          }
        });
        JButton drinkButton4 = StyledButton("Jasmine Green Tea");
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
        orderLogs = new JTextArea(10, 20);
        orderLogs.setEditable(false);
        orderLogs.setFont(new Font("Arial", Font.PLAIN, 16));

        // populating orderlogs if orders already exist
        if (order.size() > 0) {
          for (String drink : order) {
            orderLogs.setText(orderLogs.getText() + "\n" + drink);
          }
        }

        JScrollPane orderScrollPane = new JScrollPane(orderLogs);
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);

        JButton payButton = new JButton("Pay Now");
        payButton.setFont(new Font("Arial", Font.BOLD, 20));
        rightPanel.add(payButton, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        fruityRefreshingFrame.add(mainPanel);
        fruityRefreshingFrame.setVisible(true);

        changeFrame(fruityRefreshingFrame);
      
        return fruityRefreshingFrame;
    }

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
        creamyButton.addActionListener(s);
        JButton fruityButton = StyledButton("Fruity and Refreshing");
        fruityButton.setActionCommand("Fruity and Refreshing");
        fruityButton.addActionListener(s);
        JButton coffeeButton = StyledButton("Coffee Flavored");
        coffeeButton.setActionCommand("Coffee Flavored");
        coffeeButton.addActionListener(s);

        navPanel.add(creamyButton);
        navPanel.add(fruityButton);
        navPanel.add(coffeeButton);

        navPanel.add(Box.createVerticalGlue());
        backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(s);
        navPanel.add(backToLogin);

        mainPanel.add(navPanel, BorderLayout.WEST);

        // Content Panel for drinks
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridLayout(3, 2, 20, 20));

        JButton drinkButton1 = StyledButton("Classic Brown Sugar Boba Milk Tea");
        drinkButton1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Classic Brown Sugar Boba Milk Tea");
          }
        });
        JButton drinkButton2 = StyledButton("Matcha Black Sugar Boba Milk");
        drinkButton2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Matcha Black Sugar Boba Milk");
          }
        });
        JButton drinkButton3 = StyledButton("Red Bean Matcha Milk");
        drinkButton3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Red Bean Matcha Milk");
          }
        });
        JButton drinkButton4 = StyledButton("Strawberry Milk");
        drinkButton4.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addToOrder("Strawberry Milk");
          }
        });
        JButton drinkButton5 = StyledButton("Golden Oolong Tea");
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
        orderLogs = new JTextArea(10, 20);
        orderLogs.setEditable(false);
        orderLogs.setFont(new Font("Arial", Font.PLAIN, 16));

        // populating orderlogs if orders already exist
        if (order.size() > 0) {
          for (String drink : order) {
            orderLogs.setText(orderLogs.getText() + "\n" + drink);
          }
        }

        JScrollPane orderScrollPane = new JScrollPane(orderLogs);
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);

        JButton payButton = new JButton("Pay Now");
        payButton.setFont(new Font("Arial", Font.BOLD, 20));
        rightPanel.add(payButton, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        sweetAndCreamyFrame.add(mainPanel);
        sweetAndCreamyFrame.setVisible(true);

        changeFrame(sweetAndCreamyFrame);
      
        return sweetAndCreamyFrame;
    }

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
        creamyButton.addActionListener(s);
        JButton fruityButton = StyledButton("Fruity and Refreshing");
        fruityButton.setActionCommand("Fruity and Refreshing");
        fruityButton.addActionListener(s);
        JButton coffeeButton = StyledButton("Coffee Flavored");
        coffeeButton.setActionCommand("Coffee Flavored");
        coffeeButton.addActionListener(s);


        navPanel.add(creamyButton);
        navPanel.add(fruityButton);
        navPanel.add(coffeeButton);

        navPanel.add(Box.createVerticalGlue());
        backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(s);
        navPanel.add(backToLogin);
        
       
        mainPanel.add(navPanel, BorderLayout.WEST);

        // Content Panel for drinks
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new GridLayout(3, 2, 20, 20));

        JButton drinkButton1 = StyledButton("Black Sugar Coffee Jelly");
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
        orderLogs = new JTextArea(10, 20);
        orderLogs.setEditable(false);
        orderLogs.setFont(new Font("Arial", Font.PLAIN, 16));

        // populating orderlogs if orders already exist
        if (order.size() > 0) {
          for (String drink : order) {
            orderLogs.setText(orderLogs.getText() + "\n" + drink);
          }
        }

        JScrollPane orderScrollPane = new JScrollPane(orderLogs);
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);

        JButton payButton = new JButton("Pay Now");
        payButton.setFont(new Font("Arial", Font.BOLD, 20));
        rightPanel.add(payButton, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        coffeeFlavoredFrame.add(mainPanel);
        coffeeFlavoredFrame.setVisible(true);

        changeFrame(coffeeFlavoredFrame);
      
        return coffeeFlavoredFrame;
    }
}