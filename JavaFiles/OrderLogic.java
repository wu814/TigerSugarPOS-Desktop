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
        Map<String, Object> inventoryHistoryData = new HashMap<>();

        
        inventoryHistoryData.put("order_timestamp", null);
        inventoryHistoryData.put("Sago", 0);
        inventoryHistoryData.put("Cups (XL)", 0);
        inventoryHistoryData.put("Cups (Regular Hot)", 0);
        inventoryHistoryData.put("Grass Jelly", 0);
        inventoryHistoryData.put("Crystal Jelly", 0);
        inventoryHistoryData.put("Mango Milk Cream", 0);
        inventoryHistoryData.put("Black Sugars", 0);
        inventoryHistoryData.put("Aloe Vera Bits", 0);
        inventoryHistoryData.put("Straws (Jumbo)", 0);
        inventoryHistoryData.put("Brown Sugar", 0);
        inventoryHistoryData.put("Black Sugar", 0);
        inventoryHistoryData.put("Lids (Dome)", 0);
        inventoryHistoryData.put("Strawberry Milk Cream", 0);
        inventoryHistoryData.put("Condiment Station Supplies", 0);
        inventoryHistoryData.put("Matcha", 0);
        inventoryHistoryData.put("Fresh Milk", 0);
        inventoryHistoryData.put("Tapioca Pearls (Boba)", 0);
        inventoryHistoryData.put("Tiger Pearls", 0);
        inventoryHistoryData.put("Cream Mousse", 0);
        inventoryHistoryData.put("Taro", 0);
        inventoryHistoryData.put("Red Beans", 0);
        inventoryHistoryData.put("Pudding", 0);
        inventoryHistoryData.put("Mochi", 0);
        inventoryHistoryData.put("Jasmine Green Tea Leaves", 0);
        inventoryHistoryData.put("Passion Fruit Tea Leaves", 0);
        inventoryHistoryData.put("Lychee Jelly", 0);
        inventoryHistoryData.put("Oat Milk", 0);
        inventoryHistoryData.put("Strawberry Mango", 0);
        inventoryHistoryData.put("Oolong Tea Leaves", 0);
        inventoryHistoryData.put("Straws (Regular)", 0);
        inventoryHistoryData.put("Lids (Flat)", 0);
        inventoryHistoryData.put("Napkins (Regular)", 0);
        inventoryHistoryData.put("To-Go Bags (Small)", 0);
        inventoryHistoryData.put("Cups (Regular)", 0);

        


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

            //printing out each one of the ingredients and the amount of each one
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
                        if(inventoryCounts.get(ingredient) == 0) {
                            outOfStock.add(ingredient);
                            continue;
                        }
                         int currentHistoryCount = (int) inventoryHistoryData.get(ingredient);
                         inventoryHistoryData.put(ingredient, currentHistoryCount + 1);

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
                        int currentHistoryCount = (int) inventoryHistoryData.get("Fresh Milk");
                        inventoryHistoryData.put("Fresh Milk", currentHistoryCount + 1);
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
                    int currentHistoryCount = (int) inventoryHistoryData.get(cup_name);
                    inventoryHistoryData.put(cup_name, currentHistoryCount + 1);
                    updateStmt.setString(1, cup_name); 
                    updateStmt.addBatch();
                    continue;
                } else if(entry.getKey().equals("Extra Boba")) {
                    if(entry.getValue().equals("Added")) {
                        String boba_name = "Tapioca Pearls (Boba)";
                        if(inventoryCounts.get(boba_name) == 0) {
                            outOfStock.add(boba_name);
                            continue;
                        } else {
                            int currentHistoryCount = (int) inventoryHistoryData.get(boba_name);
                            inventoryHistoryData.put(boba_name, currentHistoryCount + 1);
                            updateStmt.setString(1, boba_name);
                            updateStmt.addBatch();
                        }
                    }
                } else if(entry.getKey().equals("Tiger Pearls")) {
                    String pearl_name = "Tiger Pearls";
                    if (entry.getValue().equals("Added")) {
                        if (inventoryCounts.get(pearl_name) == 0) {
                            outOfStock.add(pearl_name);
                            continue;
                        } else {
                            int currentHistoryCount = (int) inventoryHistoryData.get(pearl_name);
                            inventoryHistoryData.put(pearl_name, currentHistoryCount + 1);
                            updateStmt.setString(1, pearl_name);
                            updateStmt.addBatch();
                        }
                    }
                } else if(entry.getKey().equals("Cream Mousse")) {
                    String cream_name = "Cream Mousse";
                    if (entry.getValue().equals("Added")) {
                        if (inventoryCounts.get(cream_name) == 0) {
                            outOfStock.add(cream_name);
                            continue;
                        } else {
                            int currentHistoryCount = (int) inventoryHistoryData.get(cream_name);
                            inventoryHistoryData.put(cream_name, currentHistoryCount + 1);
                            updateStmt.setString(1, cream_name);
                            updateStmt.addBatch();
                        }
                    }
                } else if(entry.getKey().equals("Taro")) {
                    String taro_name = "Taro";
                    if (entry.getValue().equals("Added")) { // New Added conditional
                        if (inventoryCounts.get(taro_name) == 0) { // If out of stock, add to out of stock list.
                            outOfStock.add(taro_name);
                        }
                        int currentHistoryCount = (int) inventoryHistoryData.get(taro_name);
                        inventoryHistoryData.put(taro_name, currentHistoryCount + 1);
                        updateStmt.setString(1, taro_name);
                        updateStmt.addBatch();
                    }
                } else if(entry.getKey().equals("Red Bean")) {
                    String redbean_name = "Red Beans";
                    if (entry.getValue().equals("Added")) {
                        if (inventoryCounts.get(redbean_name) == 0) {
                            outOfStock.add(redbean_name);
                        }
                        int currentHistoryCount = (int) inventoryHistoryData.get(redbean_name);
                        inventoryHistoryData.put(redbean_name, currentHistoryCount + 1);
                        updateStmt.setString(1, redbean_name);
                        updateStmt.addBatch();
                    }
                } else if(entry.getKey().equals("Pudding")) {
                    String pudding_name = "Pudding";
                    if (entry.getValue().equals("Added")) {
                        if (inventoryCounts.get(pudding_name) == 0) {
                            outOfStock.add(pudding_name);
                        }
                        
                        int currentHistoryCount = (int) inventoryHistoryData.get(pudding_name);
                        inventoryHistoryData.put(pudding_name, currentHistoryCount + 1);
                        updateStmt.setString(1, pudding_name);
                        updateStmt.addBatch();
                    }

                } else if(entry.getKey().equals("Mochi")) {
                    String mochi_name = "Mochi";
                    if (entry.getValue().equals("Added")) { // New Added conditional
                        if (inventoryCounts.get(mochi_name) == 0) { // If out of stock, add to out of stock list.
                            outOfStock.add(mochi_name);
                        }
                        int currentHistoryCount = (int) inventoryHistoryData.get(mochi_name);
                        inventoryHistoryData.put(mochi_name, currentHistoryCount + 1);
                        updateStmt.setString(1, mochi_name);
                        updateStmt.addBatch();
                    }
                }
            }

            

            // if arraylist is empty, we can go, otherwise, we return the arraylist 
            if(outOfStock.size() != 0) {
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
            preparedStatement2.setInt(2, (int) inventoryHistoryData.get("Sago"));
            preparedStatement2.setInt(3, (int) inventoryHistoryData.get("Cups (XL)"));
            preparedStatement2.setInt(4, (int) inventoryHistoryData.get("Cups (Regular Hot)"));
            preparedStatement2.setInt(5, (int) inventoryHistoryData.get("Grass Jelly"));
            preparedStatement2.setInt(6, (int) inventoryHistoryData.get("Crystal Jelly"));
            preparedStatement2.setInt(7, (int) inventoryHistoryData.get("Mango Milk Cream"));
            preparedStatement2.setInt(8, (int) inventoryHistoryData.get("Black Sugars"));
            preparedStatement2.setInt(9, (int) inventoryHistoryData.get("Aloe Vera Bits"));
            preparedStatement2.setInt(10, (int) inventoryHistoryData.get("Straws (Jumbo)"));
            preparedStatement2.setInt(11, (int) inventoryHistoryData.get("Brown Sugar"));
            preparedStatement2.setInt(12, (int) inventoryHistoryData.get("Black Sugar"));
            preparedStatement2.setInt(13, (int) inventoryHistoryData.get("Lids (Dome)"));
            preparedStatement2.setInt(14, (int) inventoryHistoryData.get("Strawberry Milk Cream"));
            preparedStatement2.setInt(15, (int) inventoryHistoryData.get("Condiment Station Supplies"));
            preparedStatement2.setInt(16, (int) inventoryHistoryData.get("Matcha"));
            preparedStatement2.setInt(17, (int) inventoryHistoryData.get("Fresh Milk"));
            preparedStatement2.setInt(18, (int) inventoryHistoryData.get("Tapioca Pearls (Boba)"));
            preparedStatement2.setInt(19, (int) inventoryHistoryData.get("Tiger Pearls"));
            preparedStatement2.setInt(20, (int) inventoryHistoryData.get("Cream Mousse"));
            preparedStatement2.setInt(21, (int) inventoryHistoryData.get("Taro"));
            preparedStatement2.setInt(22, (int) inventoryHistoryData.get("Red Beans"));
            preparedStatement2.setInt(23, (int) inventoryHistoryData.get("Pudding"));
            preparedStatement2.setInt(24, (int) inventoryHistoryData.get("Mochi"));
            preparedStatement2.setInt(25, (int) inventoryHistoryData.get("Jasmine Green Tea Leaves"));
            preparedStatement2.setInt(26, (int) inventoryHistoryData.get("Passion Fruit Tea Leaves"));
            preparedStatement2.setInt(27, (int) inventoryHistoryData.get("Lychee Jelly"));
            preparedStatement2.setInt(28, (int) inventoryHistoryData.get("Oat Milk"));
            preparedStatement2.setInt(29, (int) inventoryHistoryData.get("Strawberry Mango"));
            preparedStatement2.setInt(30, (int) inventoryHistoryData.get("Oolong Tea Leaves"));
            preparedStatement2.setInt(31, (int) inventoryHistoryData.get("Straws (Regular)"));
            preparedStatement2.setInt(32, (int) inventoryHistoryData.get("Lids (Flat)"));
            preparedStatement2.setInt(33, (int) inventoryHistoryData.get("Napkins (Regular)"));
            preparedStatement2.setInt(34, (int) inventoryHistoryData.get("To-Go Bags (Small)"));
            preparedStatement2.setInt(35, (int) inventoryHistoryData.get("Cups (Regular)"));

            // Execute the SQL statement
            preparedStatement.executeUpdate();
            preparedStatement2.executeUpdate();
            updateStmt.executeBatch();
            conn.commit();

            for (Map.Entry<String, Object> entry : inventoryHistoryData.entrySet()) {
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
