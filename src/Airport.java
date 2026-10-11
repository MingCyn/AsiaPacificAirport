import java.util.Random;
import java.util.concurrent.Semaphore;

public class Airport {
    // 3 Ground Permits with Fairness enabled (FIFO)
    private final Semaphore groundCapacity = new Semaphore(3, true);
    // 1 Runway Permit (Mutex)
    private final Semaphore runway = new Semaphore(1, true);
    // 1 Refuel Truck Permit
    private final Semaphore refuelTruck = new Semaphore(1, true);

    private final boolean[] gateAvailable = new boolean[]{true, true, true};
    private final Random rand = new Random();

    // 1. Request Landing
    public void requestLanding(Plane plane) {
        System.out.println("Plane-" + plane.getPlaneId() + ": Requesting Landing");

        if (plane.isEmergency()) {
            System.out.println("Plane-" + plane.getPlaneId() + ": MAYDAY! Low fuel emergency landing!");
        }

        // Try to acquire ground capacity permit
        if (!groundCapacity.tryAcquire()) {
            System.out.println("ATC: Landing Permission Denied for Plane-" + plane.getPlaneId() + ", Airport Full");
            try {
                groundCapacity.acquire(); // Blocks until a gate/ground permit opens up
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Acquire Runway permit
        try {
            runway.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Assign Available Gate
        synchronized (gateAvailable) {
            for (int i = 0; i < 3; i++) {
                if (gateAvailable[i]) {
                    plane.setAssignedGate(i + 1);
                    gateAvailable[i] = false;
                    break;
                }
            }
        }

        System.out.println("ATC: Landing permission granted for Plane-" + plane.getPlaneId());
        System.out.println("ATC: Gate-" + plane.getAssignedGate() + " assigned for Plane-" + plane.getPlaneId());
        System.out.println("Plane-" + plane.getPlaneId() + ": Landing");
    }

    // 2. Coast and Dock
    public void coastAndDock(Plane plane) {
        try {
            Thread.sleep(1000); // Landing roll
        } catch (InterruptedException e) {}

        runway.release(); // Runway is now free for other planes
        System.out.println("Plane-" + plane.getPlaneId() + ": Coasting to Gate-" + plane.getAssignedGate());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        System.out.println("Plane-" + plane.getPlaneId() + ": Docked at Gate-" + plane.getAssignedGate());
    }

    // 3. Disembark Passengers
    public void disembarkPassengers(Plane plane) {
        int passengersOut = rand.nextInt(40) + 10;
        System.out.println("Plane-" + plane.getPlaneId() + "'s Passengers: " 
                           + passengersOut + " disembarking out of Plane-" + plane.getPlaneId());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {}
    }

    // 4. Refueling using Refuel Truck Semaphore (Only after disembarking)
    public void refuelPlane(Plane plane) {
        System.out.println("Plane-" + plane.getPlaneId() + ": Waiting for RefuelTruck at Gate-" + plane.getAssignedGate());
        try {
            refuelTruck.acquire(); // Acquire single truck permit
            System.out.println("RefuelTruck: Refueling Plane-" + plane.getPlaneId() + " at Gate-" + plane.getAssignedGate());
            Thread.sleep(2000); // Refueling time
            System.out.println("RefuelTruck: Refueling complete for Plane-" + plane.getPlaneId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            refuelTruck.release(); // Release truck permit for next plane
        }
    }

    // 5. Embark Passengers & Clean
    public void embarkPassengersAndClean(Plane plane) {
        int passengersIn = rand.nextInt(40) + 10;
        System.out.println("Plane-" + plane.getPlaneId() + ": Refilling supplies and cleaning...");
        System.out.println("Plane-" + plane.getPlaneId() + "'s Passengers: " 
                           + passengersIn + " embarking onto Plane-" + plane.getPlaneId());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {}
    }

    // 6. Request Takeoff
    public void requestTakeoff(Plane plane) {
        System.out.println("Plane-" + plane.getPlaneId() + ": Requesting Taking off");

        try {
            runway.acquire(); // Acquire Runway permit for takeoff
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        synchronized (gateAvailable) {
            gateAvailable[plane.getAssignedGate() - 1] = true; // Free gate
        }

        System.out.println("ATC: Taking-off is granted for Plane-" + plane.getPlaneId() + ". Runway is free.");
        System.out.println("Plane-" + plane.getPlaneId() + ": Undocking from Gate-" + plane.getAssignedGate() + " and taking off.");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        System.out.println("Plane-" + plane.getPlaneId() + ": Taking off... Airborne!");
        
        runway.release();         // Free Runway
        groundCapacity.release(); // Free Ground/Gate capacity permit
    }
}