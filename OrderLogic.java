import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class OrderLogic {

    private static final String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_10g_db";
    private static final String USER = "csce315_910_christophervu03";
    private static final String PASSWORD = "password";

    public static void placeOrder(int employeeId, int customerId, String[] orderItems, double orderTotal) {
        // TODO: change this to the real order DB
        String sqlCommand = "INSERT INTO order_test (order_timestamp, employee_id, customer_id, order_items, order_total) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = conn.prepareStatement(sqlCommand);

            // set parameters

            // getting timestamp without millisecond
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            timestamp.setNanos(0);
            preparedStatement.setTimestamp(1, timestamp);
            preparedStatement.setInt(2, employeeId);
            preparedStatement.setInt(3, customerId);
            preparedStatement.setArray(4, conn.createArrayOf("text", orderItems));
            preparedStatement.setDouble(5, orderTotal);

            // execute the SQL statement
            preparedStatement.executeUpdate();

            // updating inventory
            for (String item: orderItems) {
                // selecting ingredients for each item (it's an array)
                String selectIngredients = "SELECT ingredients FROM products WHERE drink_name = " + "'" + item + "'";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(selectIngredients);
                result.next();
                String[] ingredients = (String[]) result.getArray("ingredients").getArray();

                // updating inventory for each ingredient
                for (String ingredient: ingredients) {
                    String updateInventory = "UPDATE inventory SET stock_remaining = stock_remaining - 1 WHERE supply = " + "'" + ingredient + "'";
                    stmt.executeUpdate(updateInventory);
                }
            }

            System.out.println("Order added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding order: " + e.getMessage());
        } finally {
            // conn.close();
        }
    }

    public static Map<String, Double> fetchAllDrinkPrices() {
        String sqlCommand = "SELECT drink_name, price FROM products";

        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();

            // send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlCommand);

            // fetch results
            Map<String, Double> drinkPrices = new HashMap<String, Double>();
            while (result.next()) {
                drinkPrices.put(result.getString("drink_name"), result.getDouble("price"));
            }

            return drinkPrices;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching drink prices: " + e.getMessage());
            return null;
        } finally {
            // conn.close();
        }
    }
}
