import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author Chris Vu
 */
public class OrderLogic {

    // Attributes
    private static final String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_10g_db";
    private static final String USER = "csce315_910_christophervu03";
    private static final String PASSWORD = "password";

    /**
     * @param employeeId the employee who took the order
     * @param customerId the customer who placed the order
     * @param orderItems the items purchased
     * @param orderTotal the total of the order
     */

    public static void placeOrder(int employeeId, int customerId, String[] orderItems, double orderTotal, String[] orderAttributes, String[] orderAddons){
        // TODO: change this to the real order DB
        String sqlCommand = "INSERT INTO order_test (order_timestamp, employee_id, customer_id, order_items, order_total) VALUES (?, ?, ?, ?, ?)";
        String selectIngredients = "SELECT ingredients FROM products WHERE drink_name = ?";
        String updateInventory = "UPDATE inventory SET stock_remaining = stock_remaining - 1 WHERE supply = ?";
        Connection conn = null;

        try{
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // Disable auto commit (for speed with large insertions)
            conn.setAutoCommit(false);

            PreparedStatement preparedStatement = conn.prepareStatement(sqlCommand);

            // Set parameters

            // Getting timestamp without millisecond
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            timestamp.setNanos(0);
            preparedStatement.setTimestamp(1, timestamp);
            preparedStatement.setInt(2, employeeId);
            preparedStatement.setInt(3, customerId);
            preparedStatement.setArray(4, conn.createArrayOf("text", orderItems));
            preparedStatement.setDouble(5, orderTotal);
            preparedStatement.setArray(6, conn.createArrayOf("text", orderAttributes));
            preparedStatement.setArray(7, conn.createArrayOf("text", orderAddons));

            // Execute the SQL statement
            preparedStatement.executeUpdate();
            conn.commit();

            // Updating inventory
            PreparedStatement selectStmt = conn.prepareStatement(selectIngredients);
            PreparedStatement updateStmt = conn.prepareStatement(updateInventory);
            for (String item : orderItems) {
                selectStmt.setString(1, item);
                ResultSet result = selectStmt.executeQuery();
                if (result.next()) {
                    String[] ingredients = (String[]) result.getArray("ingredients").getArray();

                    for (String ingredient : ingredients) {
                        updateStmt.setString(1, ingredient);
                        updateStmt.addBatch();
                    }
                }
            }
            updateStmt.executeBatch();
            conn.commit();

            System.out.println("Order added successfully!");
        }catch(SQLException e){
            e.printStackTrace();
            System.err.println("Error adding order: " + e.getMessage());
        }finally{
            try{
                conn.close();
            }catch (SQLException e){
                e.printStackTrace();
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }


    /**
     * @return a map the maps the drinks to their corresponding prices
     */
    public static Map<String, Double> fetchAllDrinkPrices(){
        String sqlCommand = "SELECT drink_name, price FROM products";
        Connection conn = null;

        try{
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();

            // Send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlCommand);

            // Fetch results
            Map<String, Double> drinkPrices = new HashMap<String, Double>();
            while(result.next()){
                drinkPrices.put(result.getString("drink_name"), result.getDouble("price"));
            }

            return drinkPrices;

        }catch(SQLException e){
            e.printStackTrace();
            System.err.println("Error fetching drink prices: " + e.getMessage());
            return null;
        }finally{
            try{
                conn.close();
            }catch(SQLException e){
                e.printStackTrace();
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static ArrayList<String> fetchDrinksByType(String type){
        String sqlCommand = "SELECT drink_name FROM products WHERE drink_type = ?";
        Connection conn = null;

        try{
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sqlCommand);
            stmt.setString(1, type);

            // Send statement to DBMS
            ResultSet result = stmt.executeQuery();

            // Fetch results
            ArrayList<String> drinks = new ArrayList<String>();
            while(result.next()){
                drinks.add(result.getString("drink_name"));
            }

            return drinks;

        }catch(SQLException e){
            e.printStackTrace();
            System.err.println("Error fetching drinks by type: " + e.getMessage());
            return null;
        }finally{
            try{
                conn.close();
            }catch(SQLException e){
                e.printStackTrace();
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
