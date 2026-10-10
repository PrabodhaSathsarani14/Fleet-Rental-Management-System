
package lk.ijse.fleet;

public class RentalAgreement {
    private static int idCounter = 1001;

    private String agreementId;
    private Vehicle vehicle;
    private Customer customer;
    private int rentalDays;
    private double totalCost;
    private int daysElapsed;

    public RentalAgreement(Vehicle vehicle, Customer customer, int rentalDays) {
        this.agreementId = "AGR-" + idCounter++;
        this.vehicle = vehicle;
        this.customer = customer;
        this.rentalDays = rentalDays;
        this.daysElapsed = 0;

        double subtotal = vehicle.calculateRentalCost(rentalDays);
        this.totalCost = customer.applyDiscount(subtotal);
    }

    public String getAgreementId() { 
        return agreementId; 
    }
    public Vehicle getVehicle() { 
        return vehicle; 
    }
    public Customer getCustomer() { 
        return customer; 
    }
    public int getRentalDays() { 
        return rentalDays; 
    }
    public double getTotalCost() { 
        return totalCost; 
    }

    public int getDaysElapsed() { 
        return daysElapsed; 
    }
    public void setDaysElapsed(int daysElapsed) { 
        this.daysElapsed = daysElapsed; 
    }

    @Override
    public String toString() {
        return "========================================================\n" +
               "RENTAL AGREEMENT RECEIPT\n" +
               "========================================================\n" +
               "Agreement ID : " + agreementId + "\n" +
               "Vehicle      : " + vehicle.getVehicleId() + " - " + vehicle.getBrand() + " " + vehicle.getModel() + "\n" +
               "Customer     : " + customer.getCustomerId() + " - " + customer.getFullName() + "\n" +
               "Rental Days  : " + rentalDays + "\n" +
               String.format("Total Cost   : %,.2f\n", totalCost) +
               "========================================================";
    }
}