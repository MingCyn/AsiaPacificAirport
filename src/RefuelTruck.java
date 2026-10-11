public class RefuelTruck implements Runnable {
    private final Airport airport;

    public RefuelTruck(Airport airport) {
        this.airport = airport;
    }

    @Override
    public void run() {
        System.out.println("RefuelTruck Thread: Active and waiting for planes at gates.");
        // Serves as a dedicated background worker thread
        while (true) {
            try {
                Thread.sleep(1000); 
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}