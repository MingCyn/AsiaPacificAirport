import java.util.Random;

public class EmergencyCase implements Runnable {
    private final Airport airport;
    private final Random rand = new Random();

    public EmergencyCase(Airport airport) {
        this.airport = airport;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 6; i++) {
            Plane plane;

            // Plane-3 simulates the emergency low fuel landing
            if (i == 3) {
                plane = new EmergencyPlane(i, airport);
            } else {
                plane = new PassengerPlane(i, airport);
            }

            new Thread(plane).start();

            try {
                // Arrival interval: 0, 1, or 2 seconds
                Thread.sleep(rand.nextInt(3) * 1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}