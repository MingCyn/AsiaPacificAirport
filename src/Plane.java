// Abstract base class representing Polymorphism
public abstract class Plane implements Runnable {
    protected int id;
    protected Airport airport;
    protected boolean isEmergency;
    protected int assignedGate = -1;

    public Plane(int id, Airport airport, boolean isEmergency) {
        this.id = id;
        this.airport = airport;
        this.isEmergency = isEmergency;
    }

    public int getPlaneId() { return id; }
    public boolean isEmergency() { return isEmergency; }
    public int getAssignedGate() { return assignedGate; }
    public void setAssignedGate(int gate) { this.assignedGate = gate; }

    @Override
    public abstract void run();
}