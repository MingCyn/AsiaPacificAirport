import java.util.Random;

public class EmergencyCase implements Runnable {
    private final Airport airport;
    private final Random rand = new Random();

    public EmergencyCase(Airport airport) {
        this.airport = airport;
    }

    @Override
    public void run() {
        // Total 6 planes attempting to land
        for (int i = 1; i <= 6; i++) {
            // Plane-3 simulates the emergency low-fuel landing scenario
            boolean isEmergency = (i == 3);

            Plane plane = new Plane(i, airport, isEmergency);
            new Thread(plane).start();

            try {
                // Random arrival interval: 0, 1, or 2 seconds (0 to 2000 ms)
                Thread.sleep(rand.nextInt(3) * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}