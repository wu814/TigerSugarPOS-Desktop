import java.sql.*;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.*;
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
        if(s.equals("Back to Login")){
          System.out.println("ASF");
          changeFrame(startFrame);
        }
      
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
        JFrame cashierFrame = new JFrame("Cashier Display");
        cashierFrame.setSize(1000, 800);
        cashierFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        //Font buttonFont = new Font("Arial", Font.PLAIN, 24);
        Font titleButtonFont = new Font("Roboto", Font.BOLD, 24);

        // Left Nav panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

        // Drink Type Buttons
        JButton creamyButton = new JButton("Sweet and Creamy", null);
        creamyButton.setFont(titleButtonFont);
        navPanel.add(creamyButton);

        JButton fruityButton = new JButton("Fruity and Refreshing", null);
        fruityButton.setFont(titleButtonFont);
        navPanel.add(fruityButton);

        JButton coffeeButton = new JButton("Coffee Flavored", null);
        coffeeButton.setFont(titleButtonFont);
        navPanel.add(coffeeButton);

        mainPanel.add(navPanel, BorderLayout.WEST);

        // Center Panel

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.LIGHT_GRAY);
       
        backToLogin = new JButton("Back to Login");
        backToLogin.addActionListener(s);
        contentPanel.add(backToLogin);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        cashierFrame.add(mainPanel);
        cashierFrame.setVisible(true);

        changeFrame(cashierFrame);
      }
    }
}