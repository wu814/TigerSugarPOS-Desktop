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
     * @return the name of the employee
    */
    public String getName() {
        return name;
    }


    /**
     * @param name the employee's name
    */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the poition of the employee
    */
    public String getPosition() {
        return position;
    }


    /**
     * @param position the employee's position
    */
    public void setPosition(String position) {
        this.position = position;
    }


    /**
     * @return the wage of the employee
    */
    public double getWage() {
        return wage;
    }


    /**
     * @param wage the employee's wage
    */
    public void setWage(double wage) {
        this.wage = wage;
    }


    /**
     * @return the hours worked for the employee
    */
    public int getHoursWorked() {
        return hoursWorked;
    }


    /**
     * @param hoursWorked the employee's hours worked
    */
    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }


    /**
     * @return a booleaning indicating if that employee is the manager
    */
    public boolean isManager(){
        if(position.equals("Manager"))
            return true;
        return false;
    }
}
