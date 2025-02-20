public class NotReadyState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("Drone: Not Ready");
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