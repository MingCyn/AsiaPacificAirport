import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Airport {
    // 3 Ground Permits (Fairness / FIFO enabled)
    private final Semaphore groundCapacity = new Semaphore(3, true);
    // 1 Runway Permit (Mutex)
    private final Semaphore runway = new Semaphore(1, true);

    // Queue for planes waiting for the RefuelTruck
    private final LinkedList<Plane> refuelQueue = new LinkedList<>();
    private Plane currentRefuelingPlane = null;

    private final boolean[] gateAvailable = new boolean[]{true, true, true};
    private final Random rand = new Random();

    // --- LANDING MONITORS ---
    public void requestLanding(Plane plane) {
        System.out.println("Plane-" + plane.getPlaneId() + ": Requesting Landing");

        if (plane.isEmergency()) {
            System.out.println("Plane-" + plane.getPlaneId() + ": MAYDAY! Low fuel emergency landing!");
        }

        // Acquire Ground Permit (Max 3 planes)
        if (!groundCapacity.tryAcquire()) {
            System.out.println("ATC: Landing Permission Denied for Plane-" + plane.getPlaneId() + ", Airport Full");
            try {
                groundCapacity.acquire(); // Blocks in FIFO queue until space opens
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Acquire Runway Permit
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

    public void coastAndDock(Plane plane) {
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        runway.release(); // Runway freed for other landings/takeoffs
        System.out.println("Plane-" + plane.getPlaneId() + ": Coasting to Gate-" + plane.getAssignedGate());

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        System.out.println("Plane-" + plane.getPlaneId() + ": Docked at Gate-" + plane.getAssignedGate());
    }

    // --- PASSENGER & REFUELING MONITORS ---
    public void disembarkPassengers(Plane plane) {
        int passengersOut = rand.nextInt(40) + 10;
        System.out.println("Plane-" + plane.getPlaneId() + "'s Passengers: " 
                           + passengersOut + " disembarking out of Plane-" + plane.getPlaneId());
        try { Thread.sleep(1500); } catch (InterruptedException e) {}
    }

    public synchronized void requestRefueling(Plane plane) {
        refuelQueue.addLast(plane);
        System.out.println("Plane-" + plane.getPlaneId() + ": Waiting for RefuelTruck at Gate-" + plane.getAssignedGate());
        notifyAll(); // Signal the RefuelTruck thread

        // Plane thread waits until RefuelTruck finishes refueling it
        while (currentRefuelingPlane != plane) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Called ONLY by RefuelTruck Thread
    public synchronized Plane truckGetNextTask() throws InterruptedException {
        while (refuelQueue.isEmpty()) {
            wait(); // Truck sits in WAITING state until a plane finishes disembarking
        }
        currentRefuelingPlane = refuelQueue.removeFirst();
        return currentRefuelingPlane;
    }

    // Called ONLY by RefuelTruck Thread
    public synchronized void truckFinishTask(Plane plane) {
        currentRefuelingPlane = null;
        notifyAll(); // Wake up the plane thread whose refueling is done
    }

    public void embarkPassengersAndClean(Plane plane) {
        int passengersIn = rand.nextInt(40) + 10;
        System.out.println("Plane-" + plane.getPlaneId() + ": Refilling supplies and cleaning...");
        System.out.println("Plane-" + plane.getPlaneId() + "'s Passengers: " 
                           + passengersIn + " embarking onto Plane-" + plane.getPlaneId());
        try { Thread.sleep(1500); } catch (InterruptedException e) {}
    }

    // --- TAKEOFF MONITORS ---
    public void requestTakeoff(Plane plane) {
        System.out.println("Plane-" + plane.getPlaneId() + ": Requesting Taking off");

        try {
            runway.acquire(); // Acquire runway for takeoff
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        synchronized (gateAvailable) {
            gateAvailable[plane.getAssignedGate() - 1] = true; // Free gate
        }

        System.out.println("ATC: Taking-off is granted for Plane-" + plane.getPlaneId() + ". Runway is free.");
        System.out.println("Plane-" + plane.getPlaneId() + ": Undocking from Gate-" + plane.getAssignedGate() + " and taking off.");

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        System.out.println("Plane-" + plane.getPlaneId() + ": Taking off... Airborne!");

        runway.release();         // Free Runway
        groundCapacity.release(); // Free Ground Space
    }
}