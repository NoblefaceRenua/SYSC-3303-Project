public class FlyingState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("Drone " + drone.getId() + ": Flying to incident zone");
        drone.fly();
    }

    /**
     *
     */
    @Override
    public void displayState() {

    }
}