public class Employee {
    // Attributes
    private String name;
    private String position;
    private double wage;
    private int hoursWorked;


   // Constructor
    public Employee(String name, String position, String wage, String hoursWorked) {
        this.name = name;
        this.position = position;
        this.wage = Double.valueOf(wage);
        this.hoursWorked = Integer.valueOf(hoursWorked);
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public boolean isManager(){
        if(position.equals("Manager"))
            return true;
        return false;
    }

    

}
