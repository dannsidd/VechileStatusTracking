import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Create instance of the service class
        VehicleServiceImpl vehicleService = new VehicleServiceImpl();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Ask user to log in first
            System.out.println("\n===== Vehicle Status Tracking System =====");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Choose an option (1-2): ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline character left by nextInt()

            if (choice == 1) {
                // Ask for username and password to log in
                System.out.print("Enter username: ");
                String username = scanner.nextLine();

                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                // Authenticate the user
                String role = vehicleService.authenticateUser(username, password);

                // If authentication is successful
                if (role != null) {
                    System.out.println("Login successful! Welcome, " + role);

                    // Display menu based on role
                    if ("admin".equals(role)) {
                        adminMenu(vehicleService, scanner);
                    } else if ("user".equals(role)) {
                        userMenu(vehicleService, scanner);
                    }
                } else {
                    System.out.println("Invalid username or password. Please try again.");
                }
            } else if (choice == 2) {
                System.out.println("Exiting... Thank you for using the system.");
                break; // Exit the program
            } else {
                System.out.println("Invalid option. Please enter 1 or 2.");
            }
        }

        scanner.close(); // Close the scanner when the program is done
    }

    // Admin menu options
    private static void adminMenu(VehicleServiceImpl vehicleService, Scanner scanner) {
        while (true) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. Add New Vehicle");
            System.out.println("2. Update Vehicle Status");
            System.out.println("3. View Vehicle Status");
            System.out.println("4. Filter Vehicles by Status");
            System.out.println("5. Delete Vehicle");
            System.out.println("6. Logout");
            System.out.print("Choose an option (1-6): ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline character left by nextInt()

            switch (choice) {
                case 1:
                    vehicleService.promptUserForVehicle();
                    break;
                case 2:
                    vehicleService.promptUserForStatusUpdate();
                    break;
                case 3:
                    vehicleService.viewAllVehicles();
                    break;
                case 4:
                    System.out.print("Enter status to filter (ACTIVE, INACTIVE, IN_REPAIR, IN_TRANSIT): ");
                    String status = scanner.nextLine().toUpperCase(); // Admin input for status
                    vehicleService.viewVehiclesByStatus(status); // Call the filtering method
                    break;
                case 5:
                    System.out.print("Enter vehicle ID to delete: ");
                    int vehicleIdToDelete = scanner.nextInt();
                    vehicleService.deleteVehicle(vehicleIdToDelete);
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return; // Return to the login screen
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 6.");
            }
        }
    }

    // User menu options (limited access compared to Admin)
    private static void userMenu(VehicleServiceImpl vehicleService, Scanner scanner) {
        while (true) {
            System.out.println("\n===== User Menu =====");
            System.out.println("1. View All Vehicle Status");
            System.out.println("2. Filter Vehicles by Status");
            System.out.println("3. Logout");
            System.out.print("Choose an option (1-3): ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline character left by nextInt()

            switch (choice) {
                case 1:
                    vehicleService.viewAllVehicles(); // View all vehicles
                    break;
                case 2:
                    System.out.print("Enter status to filter (ACTIVE, INACTIVE, IN_REPAIR, IN_TRANSIT): ");
                    String status = scanner.nextLine().toUpperCase(); // User input for status
                    vehicleService.viewVehiclesByStatus(status); // Call the filtering method
                    break;
                case 3:
                    System.out.println("Logging out...");
                    return; // Return to the login screen
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 3.");
            }
        }
    }
}
