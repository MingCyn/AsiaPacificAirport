public class RefuelTruck implements Runnable {
    private final Airport airport;

    public RefuelTruck(Airport airport) {
        this.airport = airport;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Truck thread waits for a plane that needs refueling
                Plane plane = airport.truckGetNextTask();

                // RefuelTruck ONLY outputs its own actions!
                System.out.println("RefuelTruck: Driving to Gate-" + plane.getAssignedGate() 
                                   + " for Plane-" + plane.getPlaneId());
                
                Thread.sleep(2000); // Refueling process

                System.out.println("RefuelTruck: Refueling completed for Plane-" + plane.getPlaneId());

                // Signal airport monitor that refueling is done
                airport.truckFinishTask(plane);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}