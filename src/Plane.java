import java.util.Random;

public class Plane implements Runnable {
    private final int id;
    private final Airport airport;
    private final boolean isEmergency;
    private final Random rand = new Random();

    public Plane(int id, Airport airport, boolean isEmergency) {
        this.id = id;
        this.airport = airport;
        this.isEmergency = isEmergency;
    }

    @Override
    public void run() {
        // Step 1: Request Landing & Gate Allocation
        int gateNo = airport.requestLanding(id, isEmergency);

        try {
            // Touchdown simulation
            Thread.sleep(1000);
            airport.clearRunwayAfterLanding(id, gateNo);

            // Coasting & Docking
            Thread.sleep(1000);
            System.out.println("Plane-" + id + ": Docked at Gate-" + gateNo);

            // Step 2: Concurrent Gate Operations
            int disembarkingPassengers = rand.nextInt(51); // 0 to 50
            int embarkingPassengers = rand.nextInt(51);

            // Thread Task A: Passengers Disembarking & Embarking
            Thread passengerThread = new Thread(() -> {
                System.out.println("Plane-" + id + "'s Passengers: " + disembarkingPassengers + " disembarking out of Plane-" + id);
                try { Thread.sleep(1500); } catch (InterruptedException e) {}
                System.out.println("Plane-" + id + "'s Passengers: " + embarkingPassengers + " embarking onto Plane-" + id);
            });

            // Thread Task B: Refill Supplies & Cleaning
            Thread serviceThread = new Thread(() -> {
                System.out.println("Plane-" + id + ": Cleaning aircraft and refilling food/supplies...");
                try { Thread.sleep(2000); } catch (InterruptedException e) {}
                System.out.println("Plane-" + id + ": Supplies refilled and aircraft cleaned.");
            });

            // Thread Task C: Refueling (Requires Single Refuel Truck)
            Thread refuelThread = new Thread(() -> {
                airport.acquireRefuelTruck(id);
                try { Thread.sleep(2500); } catch (InterruptedException e) {}
                airport.releaseRefuelTruck(id);
            });

            // Run gate services concurrently
            passengerThread.start();
            serviceThread.start();
            refuelThread.start();

            // Wait for all 3 tasks to finish before departing
            passengerThread.join();
            serviceThread.join();
            refuelThread.join();

            // Step 3: Departure Phase
            airport.requestTakeoff(id, gateNo);
            Thread.sleep(1000); // Runway take-off run
            airport.completeTakeoff(id);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
