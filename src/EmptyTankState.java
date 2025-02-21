public class EmptyTankState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("Drone " + drone.getId() + ": Empty tank, returning to base");
        drone.returnToBase();
        drone.refillTank();
    }

    /**
     *
     */
    @Override
    public void displayState() {

    }
}