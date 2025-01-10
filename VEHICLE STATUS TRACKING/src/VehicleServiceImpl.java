import java.sql.*;
import java.util.Scanner;
import java.util.logging.Logger;

public class VehicleServiceImpl implements VehicleService {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/VehicleTrackingDB";  // Replace with your DB URL
    private static final String DB_USERNAME = "root";  // Replace with your MySQL username
    private static final String DB_PASSWORD = "mysql";  // Replace with your MySQL password

    private static final Logger logger = Logger.getLogger(VehicleServiceImpl.class.getName());

    public String authenticateUser(String username, String password) {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role"); // Return role (admin or user)
            } else {
                return null; // User not found or invalid credentials
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void addVehicle(String vehicleName, Status status) {
        String query = "INSERT INTO Vehicles (vehicle_name, status) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, vehicleName);
            stmt.setString(2, status.name());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Vehicle inserted successfully.");
            } else {
                System.out.println("Failed to insert vehicle.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error inserting vehicle.");
        }
    }

    @Override
    public void updateVehicleStatus(int vehicleId, Status newStatus) {
        String query = "UPDATE Vehicles SET status = ? WHERE vehicle_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus.name());
            stmt.setInt(2, vehicleId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Vehicle " + vehicleId + " updated to status " + newStatus);
                VehicleLogger.logStatusChange(vehicleId, Status.ACTIVE, newStatus);
                System.out.println("Vehicle status updated successfully.");
            } else {
                System.out.println("Failed to update vehicle status.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating vehicle status.");
        }
    }

    public void promptUserForVehicle() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter vehicle name: ");
        String vehicleName = scanner.nextLine();

        System.out.print("Enter vehicle status (ACTIVE, INACTIVE, IN_REPAIR, IN_TRANSIT): ");
        String statusInput = scanner.nextLine().toUpperCase();

        try {
            Status status = Status.valueOf(statusInput);
            addVehicle(vehicleName, status);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status entered.");
        }
    }

    public void promptUserForStatusUpdate() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter vehicle ID to update status: ");
        int vehicleId = scanner.nextInt();

        System.out.print("Enter new status (ACTIVE, INACTIVE, IN_REPAIR, IN_TRANSIT): ");
        String statusInput = scanner.next().toUpperCase();

        try {
            Status newStatus = Status.valueOf(statusInput);
            updateVehicleStatus(vehicleId, newStatus);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status entered.");
        }
    }

    // Method to view all vehicles and their statuses
    public void viewAllVehicles() {
        String sql = "SELECT vehicle_id, vehicle_name, status FROM Vehicles";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Print header
            System.out.println("\n===== Vehicle Statuses =====");
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-12s %-20s %-12s\n", "Vehicle ID", "Vehicle Name", "Status");
            System.out.println("------------------------------------------------------------");

            // Print vehicle details in table format
            while (rs.next()) {
                int vehicleId = rs.getInt("vehicle_id");
                String vehicleName = rs.getString("vehicle_name");
                String status = rs.getString("status");

                // Use printf to align columns
                System.out.printf("%-12d %-20s %-12s\n", vehicleId, vehicleName, status);
            }

            System.out.println("------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Error fetching vehicle data: " + e.getMessage());
        }
    }

    public void viewVehiclesByStatus(String status) {
        String sql = "SELECT vehicle_id, vehicle_name, status FROM Vehicles WHERE status = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);  // Set the status filter for the query
            try (ResultSet rs = stmt.executeQuery()) {
                // Print header
                System.out.println("\n===== Vehicles with Status: " + status + " =====");
                System.out.println("------------------------------------------------------------");
                System.out.printf("%-12s %-20s %-12s\n", "Vehicle ID", "Vehicle Name", "Status");
                System.out.println("------------------------------------------------------------");

                // Print vehicle details in table format
                while (rs.next()) {
                    int vehicleId = rs.getInt("vehicle_id");
                    String vehicleName = rs.getString("vehicle_name");
                    String vehicleStatus = rs.getString("status");

                    // Use printf to align columns
                    System.out.printf("%-12d %-20s %-12s\n", vehicleId, vehicleName, vehicleStatus);
                }

                System.out.println("------------------------------------------------------------");

            }
        } catch (SQLException e) {
            System.out.println("Error fetching vehicle data: " + e.getMessage());
        }
    }

    public void deleteVehicle(int vehicleId) {
        String deleteStatusChangeQuery = "DELETE FROM statuschangelog WHERE vehicle_id = ?";
        String deleteVehicleQuery = "DELETE FROM Vehicles WHERE vehicle_id = ?";
        Connection conn = null; // Declare conn outside the try block

        try {
            // Establish connection
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            // Start a transaction
            conn.setAutoCommit(false);

            // First, delete any rows in statuschangelog that reference this vehicle
            try (PreparedStatement stmt = conn.prepareStatement(deleteStatusChangeQuery)) {
                stmt.setInt(1, vehicleId);
                int rowsAffected = stmt.executeUpdate();
                System.out.println(rowsAffected + " status change(s) deleted for vehicle ID " + vehicleId);
            }

            // Now, delete the vehicle itself
            try (PreparedStatement stmt = conn.prepareStatement(deleteVehicleQuery)) {
                stmt.setInt(1, vehicleId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Vehicle with ID " + vehicleId + " deleted successfully.");
                } else {
                    System.out.println("Vehicle with ID " + vehicleId + " not found.");
                }
            }

            // Commit the transaction
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting vehicle.");
            try {
                // If something goes wrong, roll back the transaction
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        } finally {
            // Ensure the connection is closed
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException closeException) {
                closeException.printStackTrace();
            }
        }
    }
}
