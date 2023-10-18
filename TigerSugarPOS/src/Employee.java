/**
 * @author Josh Hare
 */
public class Employee {
    // Attributes
    private String name;
    private String position;
    private double wage;
    private int hoursWorked;

    /**
     * Constructor
     * @param name employee's name
     * @param position employee's position
     * @param wage employee's wage
     * @param hoursWorked employee's hours worked
    */
    public Employee(String name, String position, String wage, String hoursWorked) {
        this.name = name;
        this.position = position;
        this.wage = Double.valueOf(wage);
        this.hoursWorked = Integer.valueOf(hoursWorked);
    }


    /**
     * Getter function for name
     * @return the name of the employee
    */
    public String getName() {
        return name;
    }


    /**
     * Setter function for name
     * @param name the employee's name
     * @return
    */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Getter function for position
     * @return the poition of the employee
    */
    public String getPosition() {
        return position;
    }


    /**
     * Setter fucntion for position
     * @param position the employee's position
     * @return 
    */
    public void setPosition(String position) {
        this.position = position;
    }


    /**
     * Getter function for wage
     * @return the wage of the employee
    */
    public double getWage() {
        return wage;
    }


    /**
     * Setter function for wage
     * @param wage the employee's wage
     * @return
    */
    public void setWage(double wage) {
        this.wage = wage;
    }


    /**
     * Getter function for hours worked
     * @return the hours worked for the employee
    */
    public int getHoursWorked() {
        return hoursWorked;
    }


    /**
     * Setter function for hours worked
     * @param hoursWorked the employee's hours worked
     * @return
    */
    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }


    /**
     * See if an employee is the manager
     * @return a booleaning indicating if that employee is the manager
    */
    public boolean isManager(){
        if(position.equals("Manager"))
            return true;
        return false;
    }
}
