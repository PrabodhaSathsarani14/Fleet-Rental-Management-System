package lk.ijse.fleet;

public class VehicleStatusMonitor implements Runnable {
    private final FleetManager fleetManager;

    public VehicleStatusMonitor(FleetManager fleetManager) {
        this.fleetManager = fleetManager;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500000); 
                synchronized (System.out) {
                    fleetManager.printFleetStatus();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}