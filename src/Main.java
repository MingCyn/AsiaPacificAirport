public class Main {
    public static void main(String[] args) {
        System.out.println("=== AIRPORT ATC SIMULATION STARTED ===\n");

        Airport airport = new Airport();
        
        // Use EmergencyCase instead of PlaneGenerator
        Thread simulationThread = new Thread(new EmergencyCase(airport));
        simulationThread.start();
    }
}