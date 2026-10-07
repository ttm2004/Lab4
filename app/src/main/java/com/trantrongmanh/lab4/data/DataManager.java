package com.trantrongmanh.lab4.data;

import com.trantrongmanh.lab4.R;
import com.trantrongmanh.lab4.model.CartItem;
import com.trantrongmanh.lab4.model.Category;
import com.trantrongmanh.lab4.model.FoodItem;
import com.trantrongmanh.lab4.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton DataManager — holds all dummy data and cart state.
 */
public class DataManager {

    private static DataManager instance;

    private List<Category> categories;
    private List<FoodItem> foodItems;
    private List<CartItem> cartItems;
    private List<User> users;
    private User currentUser;
    private int nextCategoryId = 10;
    private int nextFoodId = 100;

    private DataManager() {
        initUsers();
        initCategories();
        initFoodItems();
        cartItems = new ArrayList<>();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // ── Users ────────────────────────────────────────────────────────────────

    private void initUsers() {
        users = new ArrayList<>();
        users.add(new User(1, "admin", "123456", "Trần Trọng Mạnh",
                "admin@food.com", "0901234567", "123 Nguyễn Huệ, Q1, TP.HCM",
                R.drawable.ic_default_avatar));
        users.add(new User(2, "user", "123456", "Nguyễn Văn An",
                "user@food.com", "0912345678", "456 Lê Lợi, Q3, TP.HCM",
                R.drawable.ic_default_avatar));
    }

    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                currentUser = u;
                return u;
            }
        }
        return null;
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { currentUser = user; }
    public void logout() { currentUser = null; }

    // ── Categories ───────────────────────────────────────────────────────────

    private void initCategories() {
        categories = new ArrayList<>();
        categories.add(new Category(1, "Burger", "Các loại burger thơm ngon", R.drawable.ic_burger));
        categories.add(new Category(2, "Pizza", "Pizza Ý chuẩn vị", R.drawable.ic_pizza));
        categories.add(new Category(3, "Sushi", "Sushi Nhật Bản tươi sống", R.drawable.ic_sushi));
        categories.add(new Category(4, "Pasta", "Mì Ý đa dạng sốt", R.drawable.ic_pasta));
        categories.add(new Category(5, "Drinks", "Đồ uống giải khát", R.drawable.ic_drink));
        categories.add(new Category(6, "Dessert", "Tráng miệng ngọt ngào", R.drawable.ic_dessert));
    }

    public List<Category> getCategories() { return categories; }

    public void addCategory(Category category) {
        category.setId(nextCategoryId++);
        categories.add(category);
    }

    public void updateCategory(Category updated) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == updated.getId()) {
                categories.set(i, updated);
                return;
            }
        }
    }

    public void deleteCategory(int categoryId) {
        categories.removeIf(c -> c.getId() == categoryId);
        // Also remove associated food items
        foodItems.removeIf(f -> f.getCategoryId() == categoryId);
    }

    public Category getCategoryById(int id) {
        for (Category c : categories) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    // ── Food Items ───────────────────────────────────────────────────────────

    private void initFoodItems() {
        foodItems = new ArrayList<>();

        // Burger
        foodItems.add(new FoodItem(1, "Classic Burger", "Burger bò Mỹ kinh điển với rau sống và phô mai", 65000, R.drawable.food_burger_classic, 1, 4.5f, 15));
        foodItems.add(new FoodItem(2, "BBQ Bacon Burger", "Burger BBQ thịt xông khói giòn rụm", 85000, R.drawable.food_burger_bbq, 1, 4.7f, 20));
        foodItems.add(new FoodItem(3, "Chicken Burger", "Burger gà chiên giòn sốt mayo", 70000, R.drawable.food_burger_chicken, 1, 4.3f, 15));

        // Pizza
        foodItems.add(new FoodItem(4, "Margherita Pizza", "Pizza cổ điển với cà chua và mozzarella", 120000, R.drawable.food_pizza_margherita, 2, 4.6f, 25));
        foodItems.add(new FoodItem(5, "Pepperoni Pizza", "Pizza xúc xích pepperoni đặc biệt", 140000, R.drawable.food_pizza_pepperoni, 2, 4.8f, 25));
        foodItems.add(new FoodItem(6, "BBQ Chicken Pizza", "Pizza gà BBQ thơm lừng", 135000, R.drawable.food_pizza_bbq, 2, 4.5f, 25));

        // Sushi
        foodItems.add(new FoodItem(7, "Salmon Sushi", "Sushi cá hồi tươi nhập khẩu", 95000, R.drawable.food_sushi_salmon, 3, 4.9f, 10));
        foodItems.add(new FoodItem(8, "Tuna Roll", "Cuộn cá ngừ kiểu Nhật", 85000, R.drawable.food_sushi_tuna, 3, 4.7f, 10));
        foodItems.add(new FoodItem(9, "Dragon Roll", "Dragon Roll đặc sắc với bơ và tôm", 110000, R.drawable.food_sushi_dragon, 3, 4.8f, 15));

        // Pasta
        foodItems.add(new FoodItem(10, "Spaghetti Carbonara", "Mì Ý carbonara kem trứng thơm béo", 95000, R.drawable.food_pasta_carbonara, 4, 4.6f, 20));
        foodItems.add(new FoodItem(11, "Penne Arrabbiata", "Mì ống sốt cà chua cay", 85000, R.drawable.food_pasta_penne, 4, 4.4f, 20));

        // Drinks
        foodItems.add(new FoodItem(12, "Coca Cola", "Nước ngọt Coca Cola lon 330ml", 25000, R.drawable.food_drink_cola, 5, 4.2f, 2));
        foodItems.add(new FoodItem(13, "Fresh Orange Juice", "Nước cam tươi ép nguyên chất", 45000, R.drawable.food_drink_orange, 5, 4.7f, 5));
        foodItems.add(new FoodItem(14, "Iced Matcha Latte", "Trà xanh matcha lạnh kem tươi", 55000, R.drawable.food_drink_matcha, 5, 4.8f, 5));

        // Dessert
        foodItems.add(new FoodItem(15, "Chocolate Lava Cake", "Bánh chocolate nhân chảy ấm nóng", 65000, R.drawable.food_dessert_lava, 6, 4.9f, 15));
        foodItems.add(new FoodItem(16, "Cheesecake", "Bánh phô mai New York kinh điển", 75000, R.drawable.food_dessert_cheesecake, 6, 4.8f, 10));

        // Update category item counts
        for (Category c : categories) {
            int count = 0;
            for (FoodItem f : foodItems) {
                if (f.getCategoryId() == c.getId()) count++;
            }
            c.setItemCount(count);
        }
    }

    public List<FoodItem> getFoodItems() { return foodItems; }

    public List<FoodItem> getFoodItemsByCategory(int categoryId) {
        List<FoodItem> result = new ArrayList<>();
        for (FoodItem f : foodItems) {
            if (f.getCategoryId() == categoryId) result.add(f);
        }
        return result;
    }

    public FoodItem getFoodItemById(int id) {
        for (FoodItem f : foodItems) {
            if (f.getId() == id) return f;
        }
        return null;
    }

    public void addFoodItem(FoodItem item) {
        item.setId(nextFoodId++);
        foodItems.add(item);
        updateCategoryCount(item.getCategoryId());
    }

    public void updateFoodItem(FoodItem updated) {
        for (int i = 0; i < foodItems.size(); i++) {
            if (foodItems.get(i).getId() == updated.getId()) {
                foodItems.set(i, updated);
                return;
            }
        }
    }

    public void deleteFoodItem(int foodId) {
        FoodItem toRemove = getFoodItemById(foodId);
        if (toRemove != null) {
            int catId = toRemove.getCategoryId();
            foodItems.remove(toRemove);
            updateCategoryCount(catId);
        }
    }

    private void updateCategoryCount(int categoryId) {
        Category cat = getCategoryById(categoryId);
        if (cat != null) {
            int count = 0;
            for (FoodItem f : foodItems) {
                if (f.getCategoryId() == categoryId) count++;
            }
            cat.setItemCount(count);
        }
    }

    public List<FoodItem> searchFoodItems(String query) {
        List<FoodItem> result = new ArrayList<>();
        String lower = query.toLowerCase();
        for (FoodItem f : foodItems) {
            if (f.getName().toLowerCase().contains(lower) ||
                    f.getDescription().toLowerCase().contains(lower)) {
                result.add(f);
            }
        }
        return result;
    }

    // ── Cart ─────────────────────────────────────────────────────────────────

    public List<CartItem> getCartItems() { return cartItems; }

    public void addToCart(FoodItem foodItem) {
        for (CartItem item : cartItems) {
            if (item.getFoodItem().getId() == foodItem.getId()) {
                item.increaseQuantity();
                return;
            }
        }
        cartItems.add(new CartItem(foodItem, 1));
    }

    public void removeFromCart(int foodId) {
        cartItems.removeIf(item -> item.getFoodItem().getId() == foodId);
    }

    public void updateCartItemQuantity(int foodId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getFoodItem().getId() == foodId) {
                if (quantity <= 0) {
                    removeFromCart(foodId);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public void clearCart() { cartItems.clear(); }

    public int getCartItemCount() {
        int count = 0;
        for (CartItem item : cartItems) count += item.getQuantity();
        return count;
    }

    public double getCartTotal() {
        double total = 0;
        for (CartItem item : cartItems) total += item.getTotalPrice();
        return total;
    }
}
