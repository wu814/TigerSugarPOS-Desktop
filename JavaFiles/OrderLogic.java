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
     * Sending the order to the database for record
     * @param employeeId the employee who took the order
     * @param customerId the customer who placed the order
     * @param orderItems the items purchased
     * @param orderTotal the total of the order
     * @return
     */

    public static ArrayList<String> placeOrder(int employeeId, int customerId, String[] orderItems, double orderTotal, String[] orderAttributes, String[] orderAddons){
        String sqlCommand = "INSERT INTO orders (order_timestamp, employee_id, customer_id, order_items, order_total, drink_attributes, drink_addons) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String selectIngredients = "SELECT ingredients FROM products WHERE drink_name = ?";
        String updateInventory = "UPDATE inventory SET stock_remaining = stock_remaining - 1 WHERE supply = ?";
        Connection conn = null;
        ArrayList<String> outOfStock = new ArrayList<String>();
        Map<String, Object> inventoryHistoryData = new HashMap<>();

        try{
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // Disable auto commit (for speed with large insertions)
            conn.setAutoCommit(false);

            PreparedStatement preparedStatement = conn.prepareStatement(sqlCommand);
            PreparedStatement selectStmt = conn.prepareStatement(selectIngredients);
            PreparedStatement updateStmt = conn.prepareStatement(updateInventory);

            // Creating HashMap of all the ingredients we're going to use
            Map<String, String> ingredients = new LinkedHashMap<String, String>();

            // Adding the order items in orderAttributes to my hashmap
            for(String attribute: orderAttributes){
                // Split each attribute on comma
                String[] individualAttribute = attribute.split(",");
                boolean first = true;

                for(String ingredient: individualAttribute){
                    // Split each ingredient on colon
                    String[] individualIngredient = ingredient.split(": ");
                    if(first){
                        first = false;
                    }
                    else{
                        individualIngredient[0] = individualIngredient[0].substring(1);
                    }
                    // Trimming off first character of individualIngredient[0]
                    ingredients.put(individualIngredient[0], individualIngredient[1]);
                }
            }

            // Now doing same for add ons
            for(String addon: orderAddons){
                // Split each addon on comma
                String[] individualAddon = addon.split(",");
                boolean first = true;

                for(String ingredient: individualAddon){
                    // Split each ingredient on colon
                    String[] individualIngredient = ingredient.split(": ");
                    if(first){
                        first = false;
                    }
                    else{
                        individualIngredient[0] = individualIngredient[0].substring(1);
                    }
                    ingredients.put(individualIngredient[0], individualIngredient[1]);
                }
            }

            // Fetching all inventory items and their stock remaining and storing it in a hashmap
            String fetchInventory = "SELECT supply, stock_remaining FROM inventory";
            Statement fetchStmt = conn.createStatement();
            ResultSet inventoryResult = fetchStmt.executeQuery(fetchInventory);
            conn.commit();
            Map<String, Integer> inventoryCounts = new HashMap<String, Integer>();
            while(inventoryResult.next()){
                inventoryCounts.put(inventoryResult.getString("supply"), inventoryResult.getInt("stock_remaining"));
            }

            // Decrementing for each base ingredient of the drink
            for(String item : orderItems){
                selectStmt.setString(1, item);
                ResultSet result = selectStmt.executeQuery();
                if(result.next()){
                    String[] ingreds = (String[]) result.getArray("ingredients").getArray();
                    for(String ingredient : ingreds){
                        if(inventoryCounts.get(ingredient) == 0){
                            outOfStock.add(ingredient);
                            continue;
                        }
                        int currentHistoryCount = (int) inventoryHistoryData.getOrDefault(ingredient, 0);
                        inventoryHistoryData.put(ingredient, currentHistoryCount + 1);
                        updateStmt.setString(1, ingredient);
                        updateStmt.addBatch();
                    }
                }
            }
            
            // Decrementing for each hashmap ingredient
            for(Map.Entry<String, String> entry: ingredients.entrySet()){
                if(entry.getKey().equals("Dairy Free Alternative")){
                    if(entry.getValue().equals("None")){
                        if(inventoryCounts.get("Fresh Milk") == 0){
                            outOfStock.add("Fresh Milk");
                            continue;
                        }
                        int currentHistoryCount = (int) inventoryHistoryData.getOrDefault("Fresh Milk", 0);
                        inventoryHistoryData.put("Fresh Milk", currentHistoryCount + 1);
                        updateStmt.setString(1, "Fresh Milk");
                        updateStmt.addBatch();
                    }
                    else{
                        updateStmt.setString(1, entry.getValue());
                        updateStmt.addBatch();
                    }
                }
                else if(entry.getKey().equals("Cup Size")){
                    String cup_name = "Cups (" + entry.getValue() + ")";
                    if(inventoryCounts.get(cup_name) == 0){
                        outOfStock.add(cup_name);
                        continue;
                    }
                    int currentHistoryCount = (int) inventoryHistoryData.getOrDefault(cup_name, 0);
                    inventoryHistoryData.put(cup_name, currentHistoryCount + 1);
                    updateStmt.setString(1, cup_name); 
                    updateStmt.addBatch();
                    continue;
                }
                else if(entry.getKey().equals("Extra Boba")){
                    if(entry.getValue().equals("Added")){
                        String boba_name = "Tapioca Pearls (Boba)";
                        if(inventoryCounts.get(boba_name) == 0){
                            outOfStock.add(boba_name);
                            continue;
                        } 
                        else{
                            int currentHistoryCount = (int) inventoryHistoryData.getOrDefault(boba_name, 0);
                            inventoryHistoryData.put(boba_name, currentHistoryCount + 1);
                            updateStmt.setString(1, boba_name);
                            updateStmt.addBatch();
                        }
                    }
                }
                else if(entry.getKey().equals("Tiger Pearls")){
                    String pearl_name = "Tiger Pearls";
                    if(entry.getValue().equals("Added")){
                        if(inventoryCounts.get(pearl_name) == 0){
                            outOfStock.add(pearl_name);
                            continue;
                        } 
                        else{
                            int currentHistoryCount = (int) inventoryHistoryData.get(pearl_name);
                            inventoryHistoryData.put(pearl_name, currentHistoryCount + 1);
                            updateStmt.setString(1, pearl_name);
                            updateStmt.addBatch();
                        }
                    }
                }
                else if(entry.getKey().equals("Cream Mousse")){
                    String cream_name = "Cream Mousse";
                    if(entry.getValue().equals("Added")){
                        if(inventoryCounts.get(cream_name) == 0){
                            outOfStock.add(cream_name);
                            continue;
                        }
                        else{
                            int currentHistoryCount = (int) inventoryHistoryData.get(cream_name);
                            inventoryHistoryData.put(cream_name, currentHistoryCount + 1);
                            updateStmt.setString(1, cream_name);
                            updateStmt.addBatch();
                        }
                    }
                }
                else if(entry.getKey().equals("Taro")){
                    String taro_name = "Taro";
                    if(entry.getValue().equals("Added")){ 
                        // If out of stock, add to out of stock list.
                        if(inventoryCounts.get(taro_name) == 0){ 
                            outOfStock.add(taro_name);
                        }
                        int currentHistoryCount = (int) inventoryHistoryData.getOrDefault(taro_name, 0);
                        inventoryHistoryData.put(taro_name, currentHistoryCount + 1);
                        updateStmt.setString(1, taro_name);
                        updateStmt.addBatch();
                    }
                } 
                else if(entry.getKey().equals("Red Bean")){
                    String redbean_name = "Red Beans";
                    if(entry.getValue().equals("Added")){
                        if(inventoryCounts.get(redbean_name) == 0){
                            outOfStock.add(redbean_name);
                        }
                        int currentHistoryCount = (int) inventoryHistoryData.getOrDefault(redbean_name, 0);
                        inventoryHistoryData.put(redbean_name, currentHistoryCount + 1);
                        updateStmt.setString(1, redbean_name);
                        updateStmt.addBatch();
                    }
                } 
                else if(entry.getKey().equals("Pudding")){
                    String pudding_name = "Pudding";
                    if (entry.getValue().equals("Added")){
                        if(inventoryCounts.get(pudding_name) == 0){
                            outOfStock.add(pudding_name);
                        }
                        int currentHistoryCount = (int) inventoryHistoryData.getOrDefault(pudding_name, 0);
                        inventoryHistoryData.put(pudding_name, currentHistoryCount + 1);
                        updateStmt.setString(1, pudding_name);
                        updateStmt.addBatch();
                    }
                } 
                else if(entry.getKey().equals("Mochi")){
                    String mochi_name = "Mochi";
                    if(entry.getValue().equals("Added")){ 
                        if(inventoryCounts.get(mochi_name) == 0){ 
                            outOfStock.add(mochi_name);
                        }
                        int currentHistoryCount = (int) inventoryHistoryData.getOrDefault(mochi_name, 0);
                        inventoryHistoryData.put(mochi_name, currentHistoryCount + 1);
                        updateStmt.setString(1, mochi_name);
                        updateStmt.addBatch();
                    }
                }
            }
            // If arraylist is empty, we can go, otherwise, we return the arraylist 
            if(outOfStock.size() != 0){
                return outOfStock;
            }

            // Getting timestamp without millisecond
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            inventoryHistoryData.put("order_timestamp", timestamp);
            timestamp.setNanos(0);
            preparedStatement.setTimestamp(1, timestamp);
            preparedStatement.setInt(2, employeeId);
            preparedStatement.setInt(3, customerId);
            preparedStatement.setArray(4, conn.createArrayOf("text", orderItems));
            preparedStatement.setDouble(5, orderTotal);
            preparedStatement.setArray(6, conn.createArrayOf("text", orderAttributes));
            preparedStatement.setArray(7, conn.createArrayOf("text", orderAddons));

            String inventorySQL = "INSERT INTO inventory_history (order_timestamp, Sago, Cups_XL, Cups_Regular_Hot, Grass_Jelly, Crystal_Jelly, Mango_Milk_Cream, " +
                "Black_Sugars, Aloe_Vera_Bits, Straws_Jumbo, Brown_Sugar, Black_Sugar, Lids_Dome, Strawberry_Milk_Cream, " +
                "Condiment_Station_Supplies, Matcha, Fresh_Milk, Tapioca_Pearls_Boba, Tiger_Pearls, Cream_Mousse, Taro, " +
                "Red_Beans, Pudding, Mochi, Jasmine_Green_Tea_Leaves, Passion_Fruit_Tea_Leaves, Lychee_Jelly, Oat_Milk, " +
                "Strawberry_Mango, Oolong_Tea_Leaves, Straws_Regular, Lids_Flat, Napkins_Regular, To_Go_Bags_Small, " +
                "Cups_Regular) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement2 = conn.prepareStatement(inventorySQL);
            timestamp = (Timestamp) inventoryHistoryData.get("order_timestamp");
            preparedStatement2.setTimestamp(1, timestamp);
            preparedStatement2.setInt(2, (int) inventoryHistoryData.getOrDefault("Sago", 0));
            preparedStatement2.setInt(3, (int) inventoryHistoryData.getOrDefault("Cups (XL)", 0));
            preparedStatement2.setInt(4, (int) inventoryHistoryData.getOrDefault("Cups (Regular Hot)", 0));
            preparedStatement2.setInt(5, (int) inventoryHistoryData.getOrDefault("Grass Jelly", 0));
            preparedStatement2.setInt(6, (int) inventoryHistoryData.getOrDefault("Crystal Jelly", 0));
            preparedStatement2.setInt(7, (int) inventoryHistoryData.getOrDefault("Mango Milk Cream", 0));
            preparedStatement2.setInt(8, (int) inventoryHistoryData.getOrDefault("Black Sugars", 0));
            preparedStatement2.setInt(9, (int) inventoryHistoryData.getOrDefault("Aloe Vera Bits", 0));
            preparedStatement2.setInt(10, (int) inventoryHistoryData.getOrDefault("Straws (Jumbo)", 0));
            preparedStatement2.setInt(11, (int) inventoryHistoryData.getOrDefault("Brown Sugar", 0));
            preparedStatement2.setInt(12, (int) inventoryHistoryData.getOrDefault("Black Sugar", 0));
            preparedStatement2.setInt(13, (int) inventoryHistoryData.getOrDefault("Lids (Dome)", 0));
            preparedStatement2.setInt(14, (int) inventoryHistoryData.getOrDefault("Strawberry Milk Cream", 0));
            preparedStatement2.setInt(15, (int) inventoryHistoryData.getOrDefault("Condiment Station Supplies", 0));
            preparedStatement2.setInt(16, (int) inventoryHistoryData.getOrDefault("Matcha", 0));
            preparedStatement2.setInt(17, (int) inventoryHistoryData.getOrDefault("Fresh Milk", 0));
            preparedStatement2.setInt(18, (int) inventoryHistoryData.getOrDefault("Tapioca Pearls (Boba)", 0));
            preparedStatement2.setInt(19, (int) inventoryHistoryData.getOrDefault("Tiger Pearls", 0));
            preparedStatement2.setInt(20, (int) inventoryHistoryData.getOrDefault("Cream Mousse", 0));
            preparedStatement2.setInt(21, (int) inventoryHistoryData.getOrDefault("Taro", 0));
            preparedStatement2.setInt(22, (int) inventoryHistoryData.getOrDefault("Red Beans", 0));
            preparedStatement2.setInt(23, (int) inventoryHistoryData.getOrDefault("Pudding", 0));
            preparedStatement2.setInt(24, (int) inventoryHistoryData.getOrDefault("Mochi", 0));
            preparedStatement2.setInt(25, (int) inventoryHistoryData.getOrDefault("Jasmine Green Tea Leaves", 0));
            preparedStatement2.setInt(26, (int) inventoryHistoryData.getOrDefault("Passion Fruit Tea Leaves", 0));
            preparedStatement2.setInt(27, (int) inventoryHistoryData.getOrDefault("Lychee Jelly", 0));
            preparedStatement2.setInt(28, (int) inventoryHistoryData.getOrDefault("Oat Milk", 0));
            preparedStatement2.setInt(29, (int) inventoryHistoryData.getOrDefault("Strawberry Mango", 0));
            preparedStatement2.setInt(30, (int) inventoryHistoryData.getOrDefault("Oolong Tea Leaves", 0));
            preparedStatement2.setInt(31, (int) inventoryHistoryData.getOrDefault("Straws (Regular)", 0));
            preparedStatement2.setInt(32, (int) inventoryHistoryData.getOrDefault("Lids (Flat)", 0));
            preparedStatement2.setInt(33, (int) inventoryHistoryData.getOrDefault("Napkins (Regular)", 0));
            preparedStatement2.setInt(34, (int) inventoryHistoryData.getOrDefault("To-Go Bags (Small)", 0));
            preparedStatement2.setInt(35, (int) inventoryHistoryData.getOrDefault("Cups (Regular)", 0));

            // Execute the SQL statement
            preparedStatement.executeUpdate();
            preparedStatement2.executeUpdate();
            updateStmt.executeBatch();
            conn.commit();

            for(Map.Entry<String, Object> entry : inventoryHistoryData.entrySet()){
                System.out.println(entry.getKey() + ", Value: " + entry.getValue());
            }
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
     * Creates a (drink, price) map by loading it from database
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


    /**
     * Loading a certain type of drink to a array list
     * @param type the type of the drink
     * @return the array list with the drinks loaded from database
     */
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
