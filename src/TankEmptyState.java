public class TankEmptyState implements DroneState{
    /**
     * @param drone The drone instance that is changing to this state.
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("Drone " + drone.getId() + ": Empty tank");
        drone.returnToBase();
    }

    /**
     *
     */
    @Override
    public void displayState() {

    }
}
