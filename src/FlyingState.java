public class FlyingState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("Drone: Flying to incident zone");
        drone.fly();
    }

    /**
     *
     */
    @Override
    public void displayState() {

    }
}
