package lk.ijse.fleet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FleetManager {

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<RentalAgreement> agreements = new ArrayList<>();

    private RentalEventListener rentalEventListener;

    // ==================== MISSION 2 ====================

    public void addVehicle(Vehicle v) {
        vehicles.add(v);
    }

    public void addCustomer(Customer c) {
        customers.add(c);
    }

    public Vehicle findVehicleById(String id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId().equals(id)) {
                return vehicle;
            }
        }
        return null;
    }

    public Customer findCustomerById(String id) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    public void setRentalEventListener(RentalEventListener listener) {
        this.rentalEventListener = listener;
    }

    // Mission 4: Stream-based available vehicle list
    public List<Vehicle> getAvailableVehicles() {
        return vehicles.stream()
                .filter(v -> v.getStatus() == RentalStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    public RentalAgreement createRentalAgreement(
            String vehicleId,
            String customerId,
            int rentalDays)
            throws VehicleNotAvailableException, InsufficientCreditException {

        Vehicle vehicle = findVehicleById(vehicleId);

        if (vehicle == null) {
            System.out.println("Vehicle not found.");
            return null;
        }

        if (vehicle.getStatus() != RentalStatus.AVAILABLE) {
            throw new VehicleNotAvailableException(
                    "Vehicle is not available for rental."
            );
        }

        Customer customer = findCustomerById(customerId);

        if (customer == null) {
            System.out.println("Customer not found.");
            return null;
        }

        double subtotal = vehicle.calculateRentalCost(rentalDays);
        double cost = customer.applyDiscount(subtotal);

        if (customer instanceof CorporateCustomer corporateCustomer) {
            if (!corporateCustomer.hasSufficientCredit(cost)) {
                throw new InsufficientCreditException(
                        "Insufficient credit for this rental."
                );
            }
        }

        RentalAgreement agreement =
                new RentalAgreement(vehicle, customer, rentalDays);

        vehicle.setStatus(RentalStatus.RENTED);
        agreements.add(agreement);

        if (rentalEventListener != null) {
            rentalEventListener.onRentalCreated(agreement);
        }

        return agreement;
    }

    public void returnVehicle(String agreementId) {
        for (RentalAgreement agreement : agreements) {
            if (agreement.getAgreementId().equals(agreementId)) {
                agreement.getVehicle().setStatus(RentalStatus.AVAILABLE);
                System.out.println("Vehicle returned successfully.");
                return;
            }
        }

        System.out.println("Rental agreement not found.");
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public List<RentalAgreement> getAllAgreements() {
        return new ArrayList<>(agreements);
    }

    // ==================== MISSION 3 ====================

    public FleetSnapshot generateSnapshot() {
        return new FleetSnapshot();
    }

    public synchronized void printFleetStatus() {
        generateSnapshot().printSummary();
    }

    public synchronized void checkOverdueAgreements() {
        for (RentalAgreement agreement : agreements) {

            agreement.setDaysElapsed(
                    agreement.getDaysElapsed() + 1
            );

            if (agreement.getDaysElapsed() > agreement.getRentalDays()) {
                System.out.println(
                        "[OVERDUE ALERT] Agreement "
                                + agreement.getAgreementId()
                                + " has exceeded its "
                                + agreement.getRentalDays()
                                + "-day rental period."
                );
            }
        }
    }

    // Non-static inner class
    public class FleetSnapshot {

        private int availableCount;
        private int rentedCount;
        private double totalRevenue;

        public FleetSnapshot() {

            availableCount = 0;
            rentedCount = 0;
            totalRevenue = 0.0;

            for (Vehicle vehicle : vehicles) {
                if (vehicle.getStatus() == RentalStatus.AVAILABLE) {
                    availableCount++;
                } else if (vehicle.getStatus() == RentalStatus.RENTED) {
                    rentedCount++;
                }
            }

            for (RentalAgreement agreement : agreements) {
                totalRevenue += agreement.getTotalCost();
            }
        }

        public void printSummary() {
            System.out.printf(
                    "[FLEET STATUS] Available: %d Rented: %d Total Revenue So Far: %,.2f%n",
                    availableCount,
                    rentedCount,
                    totalRevenue
            );
        }
    }

    // ==================== MISSION 4 ====================

    public List<Vehicle> getVehiclesByFuelType(FuelType type) {
        return vehicles.stream()
                .filter(v -> v.getFuelType() == type)
                .collect(Collectors.toList());
    }

    // Optional Stream + Comparator method
    public List<Customer> getCustomersSortedByName() {
        return customers.stream()
                .sorted(Comparator.comparing(c -> c.getFullName()))
                .collect(Collectors.toList());
    }

    public double generateRevenueReport() {
        return agreements.stream()
                .mapToDouble(a -> a.getTotalCost())
                .sum();
    }

    public void printAllReportables() {

        List<Reportable> reportables = new ArrayList<>();

        reportables.addAll(vehicles);
        reportables.addAll(customers);

        System.out.println();
        System.out.println("========== FLEET & CUSTOMER REPORT ==========");

        if (reportables.isEmpty()) {
            System.out.println("No vehicles or customers found.");
            return;
        }

        for (Reportable item : reportables) {
            System.out.println(
                    item.getReportCategory() + ": " + item.getSummary()
            );
        }

        System.out.println("=============================================");
    }
}