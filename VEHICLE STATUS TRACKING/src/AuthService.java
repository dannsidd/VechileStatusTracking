import java.util.HashMap;
import java.util.Map;

public class AuthService {

    // This is just a mock database of users.
    private static final Map<String, String> userDatabase = new HashMap<>();

    static {
        // Predefined users for authentication. You can add more here.
        userDatabase.put("admin", "admin123"); // Admin password
        userDatabase.put("user", "user123");   // User password
    }

    // Method to authenticate based on role and password
    public static User authenticate(String role, String password) {
        String storedPassword = userDatabase.get(role);

        if (storedPassword != null && storedPassword.equals(password)) {
            return new User(role, password);
        }
        return null; // Return null if authentication fails
    }
}
