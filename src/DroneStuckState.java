public class DroneStuckState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        // notify scheduler
        System.out.println("Drone: Crashed");
    }

    /**
     *
     */
    @Override
    public void displayState() {

    }
}
