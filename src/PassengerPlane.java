public class PassengerPlane extends Plane {
    public PassengerPlane(int id, Airport airport) {
        super(id, airport, false);
    }

    @Override
    public void run() {
        airport.requestLanding(this);
        airport.coastAndDock(this);
        
        // Disembark passengers first
        airport.disembarkPassengers(this);
        
        // Acquire refuel truck ONLY after disembarking
        airport.refuelPlane(this);
        
        // Clean and embark passengers
        airport.embarkPassengersAndClean(this);
        
        // Takeoff
        airport.requestTakeoff(this);
    }
}