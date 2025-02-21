public class DroneStuckState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        // notify scheduler
        System.out.println("Drone " + drone.getId()  + ": Crashed");
    }

    /**
     *
     */
    @Override
    public void displayState() {

    }
}