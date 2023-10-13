import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public static ArrayList<String> placeOrder(int employeeId, int customerId, String[] orderItems, double orderTotal, String[] orderAttributes, String[] orderAddons){
        // TODO: change this to the real order DB
        String sqlCommand = "INSERT INTO orders (order_timestamp, employee_id, customer_id, order_items, order_total, drink_attributes, drink_addons) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String selectIngredients = "SELECT ingredients FROM products WHERE drink_name = ?";
        String updateInventory = "UPDATE inventory SET stock_remaining = stock_remaining - 1 WHERE supply = ?";
        Connection conn = null;
        ArrayList<String> outOfStock = new ArrayList<String>();

        try{
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // Disable auto commit (for speed with large insertions)
            conn.setAutoCommit(false);

            PreparedStatement preparedStatement = conn.prepareStatement(sqlCommand);
            PreparedStatement selectStmt = conn.prepareStatement(selectIngredients);
            PreparedStatement updateStmt = conn.prepareStatement(updateInventory);

            // creating HashMap of all the ingredients we're going to use
            Map<String, String> ingredients = new LinkedHashMap<String, String>();

            // adding the order items in orderAttributes to my hashmap
            for(String attribute: orderAttributes) {
                // split each attribute on comma
                String[] individualAttribute = attribute.split(",");
                boolean first = true;

                for(String ingredient: individualAttribute) {

                    // split each ingredient on colon
                    String[] individualIngredient = ingredient.split(": ");

                    if(first) {
                        first = false;
                    } else {
                        individualIngredient[0] = individualIngredient[0].substring(1);
                    }
                    // trimming off first character of individualIngredient[0]

                    ingredients.put(individualIngredient[0], individualIngredient[1]);
                }
            }

            // now doing same for addons
            for(String addon: orderAddons) {
                // split each addon on comma
                String[] individualAddon = addon.split(",");
                boolean first = true;

                for(String ingredient: individualAddon) {
                    // split each ingredient on colon
                    String[] individualIngredient = ingredient.split(": ");

                    if(first) {
                        first = false;
                    } else {
                        individualIngredient[0] = individualIngredient[0].substring(1);
                    }
                    ingredients.put(individualIngredient[0], individualIngredient[1]);
                }
            }

            // printing out each one of the ingredients and the amount of each one
            // for(Map.Entry<String, String> entry: ingredients.entrySet()) {
            //     System.out.println(entry.getKey() + " " + entry.getValue());
            // }

            // fetching all inventory items and their stock remaining and storing it in a hashmap
            String fetchInventory = "SELECT supply, stock_remaining FROM inventory";
            Statement fetchStmt = conn.createStatement();
            ResultSet inventoryResult = fetchStmt.executeQuery(fetchInventory);
            conn.commit();
            Map<String, Integer> inventoryCounts = new HashMap<String, Integer>();
            while(inventoryResult.next()) {
                inventoryCounts.put(inventoryResult.getString("supply"), inventoryResult.getInt("stock_remaining"));
            }

            // decrementing for each base ingredient of the drink
            for (String item : orderItems) {
                selectStmt.setString(1, item);
                ResultSet result = selectStmt.executeQuery();
                if (result.next()) {
                    String[] ingreds = (String[]) result.getArray("ingredients").getArray();

                    for (String ingredient : ingreds) {
                        updateStmt.setString(1, ingredient);
                        updateStmt.addBatch();
                    }
                }
            }

            // decrementing for each hashmap ingredient
            for(Map.Entry<String, String> entry: ingredients.entrySet()) {

                if(entry.getKey().equals("Dairy Free Alternative")) {
                    if(entry.getValue().equals("None")) {
                        if(inventoryCounts.get("Fresh Milk") == 0) {
                            outOfStock.add("Fresh Milk");
                            continue;
                        }
                        updateStmt.setString(1, "Fresh Milk");
                        updateStmt.addBatch();
                    } else {
                        updateStmt.setString(1, entry.getValue());
                        updateStmt.addBatch();
                    }
                } else if(entry.getKey().equals("Cup Size")) {
                    String cup_name = "Cups (" + entry.getValue() + ")";
                    if(inventoryCounts.get(cup_name) == 0) {
                        outOfStock.add(cup_name);
                        continue;
                    }
                    updateStmt.setString(1, cup_name); 
                    updateStmt.addBatch();
                    continue;
                } else if(entry.getKey().equals("Extra Boba")) {
                    String boba_name = "Tapioca Pearls (Boba)";
                    if(inventoryCounts.get(boba_name) == 0 && entry.getValue().equals("Added")) {
                        outOfStock.add(boba_name);
                        continue;
                    }
                    updateStmt.setString(1, boba_name);
                    updateStmt.addBatch();
                    continue;
                } else if(entry.getKey().equals("Tiger Pearls")) {
                    String boba_name = "Tiger Pearls";
                    if(inventoryCounts.get(boba_name) == 0 && entry.getValue().equals("Added")) {
                        outOfStock.add(boba_name);
                        continue;
                    }
                    updateStmt.setString(1, boba_name);
                    updateStmt.addBatch();
                    continue;
                } else if(entry.getKey().equals("Cream Mousse")) {
                    String cream_name = "Cream Mousse";
                    if(inventoryCounts.get(cream_name) == 0 && entry.getValue().equals("Added")) {
                        outOfStock.add(cream_name);
                        continue;
                    }
                    updateStmt.setString(1, cream_name);
                    updateStmt.addBatch();
                    continue;
                } else if(entry.getKey().equals("Taro")) {
                    String taro_name = "Taro";
                    if(inventoryCounts.get(taro_name) == 0 && entry.getValue().equals("Added")) {
                        outOfStock.add(taro_name);
                        continue;
                    }
                    updateStmt.setString(1, taro_name);
                    updateStmt.addBatch();
                    continue;
                } else if(entry.getKey().equals("Red Bean")) {
                    String redbean_name = "Red Beans";
                    if(inventoryCounts.get(redbean_name) == 0 && entry.getValue().equals("Added")) {
                        outOfStock.add(redbean_name);
                        continue;
                    }
                    updateStmt.setString(1, redbean_name);
                    updateStmt.addBatch();
                    continue;
                } else if(entry.getKey().equals("Pudding")) {
                    String pudding_name = "Pudding";
                    if(inventoryCounts.get(pudding_name) == 0 && entry.getValue().equals("Added")) {
                        outOfStock.add(pudding_name);
                        continue;
                    }
                    updateStmt.setString(1, pudding_name);
                    updateStmt.addBatch();
                    continue;
                } else if(entry.getKey().equals("Mochi")) {
                    String mochi_name = "Mochi";
                    if(inventoryCounts.get(mochi_name) == 0 && entry.getValue().equals("Added")) {
                        outOfStock.add(mochi_name);
                        continue;
                    }
                    updateStmt.setString(1, "Mochi");
                    updateStmt.addBatch();
                    continue;
                }
            }

            // if arraylist is empty, we can go, otherwise, we return the arraylist 
            if(outOfStock.size() != 0) {
                return outOfStock;
            }

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
            updateStmt.executeBatch();
            conn.commit();

            System.out.println("Order added successfully!");
            return outOfStock;
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
        return outOfStock;
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
