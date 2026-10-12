public class PassengerPlane extends Plane {
    public PassengerPlane(int id, Airport airport) {
        super(id, airport, false);
    }

    @Override
    public void run() {
        airport.requestLanding(this);
        airport.coastAndDock(this);
        
        airport.disembarkPassengers(this);
        
        // FIX: Change refuelPlane(this) to requestRefueling(this)
        airport.requestRefueling(this); 
        
        airport.embarkPassengersAndClean(this);
        airport.requestTakeoff(this);
    }
}