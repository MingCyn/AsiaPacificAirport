public class Main {
    public static void main(String[] args) {
        System.out.println("=== ASIA PACIFIC AIRPORT ===\n");

        Airport airport = new Airport();

        // Start Truck Thread
        Thread truckThread = new Thread(new RefuelTruck(airport));
        truckThread.setDaemon(true);
        truckThread.start();

        // Start Simulation Thread
        Thread simulation = new Thread(new EmergencyCase(airport));
        simulation.start();
    }
}