public class Employee {
    // Attributes
    private String name;
    private String position;
    private double wage;
    private int hoursWorked;

    /**
     * Constructor
     * @author Josh Hare
     * @param String name
     * @param String position
     * @param String wage
     * @param String hoursWorked
    */
    public Employee(String name, String position, String wage, String hoursWorked) {
        this.name = name;
        this.position = position;
        this.wage = Double.valueOf(wage);
        this.hoursWorked = Integer.valueOf(hoursWorked);
    }

    /**
     * Getters and setters
     * @author Josh Hare
     * @return name
    */
    public String getName() {
        return name;
    }

    /**
     * @author Josh Hare
     * @param name
    */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @author Josh Hare
     * @return position
    */
    public String getPosition() {
        return position;
    }

    /**
     * @author Josh Hare
     * @param position
    */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @author Josh Hare
     * @return wage
    */
    public double getWage() {
        return wage;
    }

    /**
     * @author Josh Hare
     * @param wage
    */
    public void setWage(double wage) {
        this.wage = wage;
    }

    /**
     * @author Josh Hare
     * @return hoursWorked
    */
    public int getHoursWorked() {
        return hoursWorked;
    }

    /**
     * @author Josh Hare
     * @param hoursWorked
    */
    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    /**
     * @author Josh Hare
     * @return boolean
    */
    public boolean isManager(){
        if(position.equals("Manager"))
            return true;
        return false;
    }
}
