public class EmergencyPlane extends Plane {
    public EmergencyPlane(int id, Airport airport) {
        super(id, airport, true);
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