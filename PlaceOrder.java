import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PlaceOrder {

    private static final String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_10g_db";
    private static final String USER = "csce315_910_christophervu03";
    private static final String PASSWORD = "password";

    public static void placeOrder(int employeeId, int customerId, String[] orderItems, double orderTotal) {
        String sql = "INSERT INTO order_test (order_timestamp, employee_id, customer_id, order_items, order_total) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set parameters
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(1, timestamp);
            preparedStatement.setInt(2, employeeId);
            preparedStatement.setInt(3, customerId);
            preparedStatement.setArray(4, connection.createArrayOf("text", orderItems));
            preparedStatement.setDouble(5, orderTotal);

            // Execute the SQL statement
            preparedStatement.executeUpdate();

            System.out.println("Order added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding order: " + e.getMessage());
        }
    }
}
