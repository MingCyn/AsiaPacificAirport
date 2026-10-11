public class Airport {
    private static final int MAX_GROUND_CAPACITY = 3;
    private static final int TOTAL_GATES = 3;

    private int planesOnGround = 0;
    private boolean runwayBusy = false;
    private boolean[] gateAvailable = new boolean[]{true, true, true}; // 3 Gates
    private boolean refuelTruckBusy = false; // Only 1 refueling truck

    // 1. Request Landing
    public synchronized int requestLanding(int planeId, boolean isEmergency) {
        // Plane waits if ground is full, OR runway is busy, OR if a non-emergency plane is waiting while emergency is flagged
        while (planesOnGround >= MAX_GROUND_CAPACITY || runwayBusy) {
            if (planesOnGround >= MAX_GROUND_CAPACITY) {
                System.out.println("ATC: Landing Permission Denied for Plane-" + planeId + ", Airport Full");
            } else if (runwayBusy) {
                System.out.println("ATC: Landing Permission Denied for Plane-" + planeId + ", Runway Busy");
            }

            try {
                wait(); // Wait for ground space or runway clear
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Reserve Runway & Ground Capacity
        runwayBusy = true;
        planesOnGround++;

        // Find available gate
        int assignedGate = -1;
        for (int i = 0; i < TOTAL_GATES; i++) {
            if (gateAvailable[i]) {
                assignedGate = i + 1; // Gate numbers: 1, 2, 3
                gateAvailable[i] = false;
                break;
            }
        }

        if (isEmergency) {
            System.out.println("Plane-" + planeId + ": MAYDAY! Low fuel emergency landing requested!");
            System.out.println("ATC: EMERGENCY Landing granted for Plane-" + planeId);
        } else {
            System.out.println("Plane-" + planeId + ": Requesting Landing");
            System.out.println("ATC: Landing permission granted for Plane-" + planeId);
        }
        
        System.out.println("ATC: Gate-" + assignedGate + " assigned for Plane-" + planeId);
        System.out.println("Plane-" + planeId + ": Landing");

        return assignedGate;
    }

    // 2. Clear Runway after landing
    public synchronized void clearRunwayAfterLanding(int planeId, int gateNo) {
        runwayBusy = false;
        System.out.println("Plane-" + planeId + ": Coasting to Gate-" + gateNo);
        notifyAll(); // Signal other planes waiting to land/take off
    }

    // 3. Request Refueling Truck (1 Truck Shared)
    public synchronized void acquireRefuelTruck(int planeId) {
        while (refuelTruckBusy) {
            try {
                wait(); // Wait for truck to become free
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        refuelTruckBusy = true;
        System.out.println("Plane-" + planeId + ": Refueling truck assigned and refueling started.");
    }

    public synchronized void releaseRefuelTruck(int planeId) {
        refuelTruckBusy = false;
        System.out.println("Plane-" + planeId + ": Refueling completed. Truck released.");
        notifyAll(); // Signal other docked planes waiting for fuel
    }

    // 4. Request Take-Off Permission
    public synchronized void requestTakeoff(int planeId, int gateNo) {
        System.out.println("Plane-" + planeId + ": Requesting Taking off");
        while (runwayBusy) {
            try {
                wait(); // Wait until runway clears
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        runwayBusy = true;
        gateAvailable[gateNo - 1] = true; // Release gate
        System.out.println("ATC: Taking-off is granted for Plane-" + planeId + ". Runway is free.");
        System.out.println("Plane-" + planeId + ": Undocking from Gate-" + gateNo + " and coasting to runway");
    }

    // 5. Clear Runway after Take-Off
    public synchronized void completeTakeoff(int planeId) {
        System.out.println("Plane-" + planeId + ": Taking off... Airborne!");
        planesOnGround--;
        runwayBusy = false;
        notifyAll(); // Wakes up planes waiting to land (Airport space freed up)
    }
}