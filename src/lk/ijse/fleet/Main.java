package lk.ijse.fleet;

import java.util.List;
import java.util.Scanner;

public class Main {

    private FleetManager fleetManager;
    private UserManager userManager;
    private Scanner scanner;
    private SystemUser loggedInUser;

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    // Prevent background messages from interrupting user input
    private String readInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    public void run() {

        scanner = new Scanner(System.in);
        fleetManager = new FleetManager();
        userManager = new UserManager();

        // Mission 3 - Vehicle Status Monitor
        Thread statusThread =
                new Thread(new VehicleStatusMonitor(fleetManager));

        statusThread.setDaemon(true);
        statusThread.start();

        // Mission 3 - Overdue Reminder Worker
        Thread overdueThread =
                new Thread(new OverdueReminderWorker(fleetManager));

        overdueThread.setDaemon(true);
        overdueThread.start();

        // Rental Event Listener
        fleetManager.setRentalEventListener(
                agreement -> System.out.println(
                        "[EVENT] New rental created: "
                                + agreement.getAgreementId()
                )
        );
        
        showAuthMenu();
    }

    private void showAuthMenu() {

        while (true) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("       FLEET RENTAL SYSTEM");
            System.out.println("=================================");
            System.out.println("[1] Login");
            System.out.println("[2] Register New User");
            System.out.println("[0] Exit");
            System.out.println("=================================");

            String input = readInput("Enter option: ");

            try {
                int option = Integer.parseInt(input);

                switch (option) {

                    case 1:
                        login();
                        break;

                    case 2:
                        registerUser();
                        break;

                    case 0:
                        System.out.println("Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid option.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void registerUser() {

        System.out.println();
        System.out.println("========== REGISTER ==========");

        String fullName = readInput("Enter full name: ");
        String username = readInput("Enter username: ");
        String password = readInput("Enter password: ");

        userManager.registerUser(username, password, fullName);
    }

    private void login() {

        System.out.println();
        System.out.println("============ LOGIN ============");

        String username = readInput("Username: ");
        String password = readInput("Password: ");

        SystemUser user = userManager.login(username, password);

        if (user != null) {

            loggedInUser = user;

            System.out.println();
            System.out.println("Login successful!");
            System.out.println("Welcome, " + loggedInUser.getFullName() + "!");

            showMainMenu();

        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private void showMainMenu() {

        while (loggedInUser != null) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("       FLEET RENTAL SYSTEM");
            System.out.println("=================================");
            System.out.println("[1] Add Vehicle");
            System.out.println("[2] Add Customer");
            System.out.println("[3] View Available Vehicles");
            System.out.println("[4] Create Rental Agreement");
            System.out.println("[5] Return Vehicle");
            System.out.println("[6] View Revenue Report");
            System.out.println("[7] View Vehicles by Fuel Type");
            System.out.println("[8] View Combined Fleet & Customer Report");
            System.out.println("[9] View All Customers");
            System.out.println("[10] View All Rental Agreements");
            System.out.println("[0] Logout");
            System.out.println("=================================");

            String input = readInput("Enter option: ");

            try {
                int option = Integer.parseInt(input);

                switch (option) {

                    case 1:
                        addVehicle();
                        break;

                    case 2:
                        addCustomer();
                        break;

                    case 3:
                        viewAvailableVehicles();
                        break;

                    case 4:
                        createRentalAgreement();
                        break;

                    case 5:
                        returnVehicle();
                        break;

                    case 6:
                        viewRevenueReport();
                        break;

                    case 7:
                        viewVehiclesByFuelType();
                        break;

                    case 8:
                        fleetManager.printAllReportables();
                        break;

                    case 9:
                        viewAllCustomers();
                        break;

                    case 10:
                        viewAllRentalAgreements();
                        break;

                    case 0:
                        loggedInUser = null;
                        System.out.println("Logged out successfully.");
                        break;

                    default:
                        System.out.println("Invalid option.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void addVehicle() {

        System.out.println();
        System.out.println("========== ADD VEHICLE ==========");

        String registrationNumber =
                readInput("Registration Number: ");

        String brand = readInput("Brand: ");
        String model = readInput("Model: ");

        try {

            double dailyRate = Double.parseDouble(
                    readInput("Daily Rate: ")
            );

            System.out.println();
            System.out.println("Select Vehicle Type:");
            System.out.println("[1] Car");
            System.out.println("[2] Van");
            System.out.println("[3] Truck");

            int vehicleType = Integer.parseInt(
                    readInput("Enter option: ")
            );

            System.out.println();
            System.out.println("Select Fuel Type:");
            System.out.println("[1] PETROL");
            System.out.println("[2] DIESEL");
            System.out.println("[3] ELECTRIC");
            System.out.println("[4] HYBRID");

            int fuelOption = Integer.parseInt(
                    readInput("Enter option: ")
            );

            FuelType fuelType;

            switch (fuelOption) {

                case 1:
                    fuelType = FuelType.PETROL;
                    break;

                case 2:
                    fuelType = FuelType.DIESEL;
                    break;

                case 3:
                    fuelType = FuelType.ELECTRIC;
                    break;

                case 4:
                    fuelType = FuelType.HYBRID;
                    break;

                default:
                    System.out.println("Invalid fuel type.");
                    return;
            }

            Vehicle vehicle;

            switch (vehicleType) {

                case 1:
                    vehicle = new Car(
                            registrationNumber,
                            brand,
                            model,
                            dailyRate,
                            fuelType
                    );
                    break;

                case 2:
                    int passengerCapacity = Integer.parseInt(
                            readInput("Passenger Capacity: ")
                    );

                    vehicle = new Van(
                            registrationNumber,
                            brand,
                            model,
                            dailyRate,
                            fuelType,
                            passengerCapacity
                    );
                    break;

                case 3:
                    double cargoCapacityTons = Double.parseDouble(
                            readInput("Cargo Capacity (tons): ")
                    );

                    vehicle = new Truck(
                            registrationNumber,
                            brand,
                            model,
                            dailyRate,
                            fuelType,
                            cargoCapacityTons
                    );
                    break;

                default:
                    System.out.println("Invalid vehicle type.");
                    return;
            }

            fleetManager.addVehicle(vehicle);

            System.out.println();
            System.out.println(
                    "Vehicle added successfully: " + vehicle.getVehicleId()
            );

        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered.");
        }
    }

    private void addCustomer() {

        System.out.println();
        System.out.println("========== ADD CUSTOMER ==========");

        String fullName = readInput("Full Name: ");
        String contactNumber = readInput("Contact Number: ");

        System.out.println();
        System.out.println("[1] Individual Customer");
        System.out.println("[2] Corporate Customer");

        try {

            int customerType = Integer.parseInt(
                    readInput("Enter customer type: ")
            );

            Customer customer;

            switch (customerType) {

                case 1:
                    customer = new IndividualCustomer(
                            fullName,
                            contactNumber,
                            0
                    );
                    break;

                case 2:
                    double creditLimit = Double.parseDouble(
                            readInput("Credit Limit: ")
                    );

                    customer = new CorporateCustomer(
                            fullName,
                            contactNumber,
                            creditLimit
                    );
                    break;

                default:
                    System.out.println("Invalid customer type.");
                    return;
            }

            fleetManager.addCustomer(customer);

            System.out.println();
            System.out.println(
                    "Customer added successfully: " + customer.getCustomerId()
            );

        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered.");
        }
    }

    private void viewAvailableVehicles() {

        System.out.println();
        System.out.println("======= AVAILABLE VEHICLES =======");

        List<Vehicle> vehicles = fleetManager.getAvailableVehicles();

        if (vehicles.isEmpty()) {
            System.out.println("No vehicles are currently available.");
            return;
        }

        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle);
        }
    }

    private void createRentalAgreement() {

        System.out.println();
        System.out.println("====== CREATE RENTAL AGREEMENT ======");

        String vehicleId = readInput("Vehicle ID: ");
        String customerId = readInput("Customer ID: ");

        try {

            int rentalDays = Integer.parseInt(
                    readInput("Rental Days: ")
            );

            if (rentalDays <= 0) {
                System.out.println("Rental days must be greater than 0.");
                return;
            }

            RentalAgreement agreement =
                    fleetManager.createRentalAgreement(
                            vehicleId,
                            customerId,
                            rentalDays
                    );

            if (agreement != null) {

                System.out.println();
                System.out.println("Rental agreement created successfully.");
                System.out.println(agreement);
            }

        } catch (
                VehicleNotAvailableException
                | InsufficientCreditException e
        ) {
            System.out.println("Rental failed: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number for rental days.");
        }
    }

    private void returnVehicle() {

        System.out.println();
        System.out.println("========= RETURN VEHICLE =========");

        String agreementId = readInput("Rental Agreement ID: ");

        fleetManager.returnVehicle(agreementId);
    }

    // Mission 4 - Revenue Report
    private void viewRevenueReport() {

        System.out.println();
        System.out.println("========== FLEET REVENUE REPORT ==========");

        double totalRevenue = fleetManager.generateRevenueReport();

        System.out.printf("Total Revenue: %,.2f%n", totalRevenue);

        System.out.println("==========================================");
    }

    // Mission 4 - Filter Vehicles by Fuel Type
    private void viewVehiclesByFuelType() {

        System.out.println();
        System.out.println("======= VEHICLES BY FUEL TYPE =======");
        System.out.println("[1] PETROL");
        System.out.println("[2] DIESEL");
        System.out.println("[3] ELECTRIC");
        System.out.println("[4] HYBRID");

        try {

            int fuelOption = Integer.parseInt(
                    readInput("Select fuel type: ")
            );

            FuelType fuelType;

            switch (fuelOption) {

                case 1:
                    fuelType = FuelType.PETROL;
                    break;

                case 2:
                    fuelType = FuelType.DIESEL;
                    break;

                case 3:
                    fuelType = FuelType.ELECTRIC;
                    break;

                case 4:
                    fuelType = FuelType.HYBRID;
                    break;

                default:
                    System.out.println("Invalid fuel type.");
                    return;
            }

            List<Vehicle> vehicles =
                    fleetManager.getVehiclesByFuelType(fuelType);

            System.out.println();
            System.out.println("Fuel Type: " + fuelType);
            System.out.println("-------------------------------------");

            if (vehicles.isEmpty()) {
                System.out.println("No vehicles found for this fuel type.");
                return;
            }

            for (Vehicle vehicle : vehicles) {
                System.out.println(vehicle);
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void viewAllCustomers() {

        System.out.println();
        System.out.println("========== ALL CUSTOMERS ==========");

        List<Customer> customers = fleetManager.getAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    private void viewAllRentalAgreements() {

        System.out.println();
        System.out.println("======= ALL RENTAL AGREEMENTS =======");

        List<RentalAgreement> agreements =
                fleetManager.getAllAgreements();

        if (agreements.isEmpty()) {
            System.out.println("No rental agreements found.");
            return;
        }

        for (RentalAgreement agreement : agreements) {
            System.out.println(agreement);
        }
    }
}