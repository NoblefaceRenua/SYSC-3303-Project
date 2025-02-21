public class OnSiteState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("Drone " + drone.getId() + ": Arrived at fire incident");
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