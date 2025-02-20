public class EmptyTankState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
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
