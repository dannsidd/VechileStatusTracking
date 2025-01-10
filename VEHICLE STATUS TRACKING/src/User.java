public class User {
    private String role; // either "admin" or "user"
    private String password; // password for authentication

    public User(String role, String password) {
        this.role = role;
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }
}
