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

public class GUILogic{
    // Attribute
    private static final String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_10g_db";
    private static final String USER = "csce315_910_williamwu258814";
    private static final String PASSWORD = "password";
    private static Connection conn = null;


    /**
     * Constructor
    */
    public GUILogic(){
        // Initialize the connection in the constructor
        try{
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        }catch (SQLException e){
            // Handle connection initialization errors here
            e.printStackTrace();
        }
    }


    /**
     * loads the employees from database to a vector
     * @param employees the vector where we load the data to 
     */
    public static void loadEmployees(Vector<Employee> employees){
        try{
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM employees;");
            // Initializes employees with info from database, adds to vector
            while(result.next()){ 
                employees.add(new Employee(
                result.getString("first_name"),
                result.getString("position"),
                result.getString("wage"),
                result.getString("hours_worked")
                ));
            }
        // Errors connecting to database
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null,e);
        }
    }
}