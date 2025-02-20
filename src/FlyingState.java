public class FlyingState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        drone.fly();
    }

    /**
     *
     */
    @Override
    public void displayState() {

    }
}
