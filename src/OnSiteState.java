public class OnSiteState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        drone.pour();
//        drone.setState("Not Ready");
    }

    /**
     *
     */
    @Override
    public void displayState() {

    }
}
