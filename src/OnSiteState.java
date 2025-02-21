/**
 * The OnSiteState class represents a state where the drone has arrived at the fire incident zone.
 * In this state, the drone prepares to extinguish the fire.
 */
public class OnSiteState implements DroneState{
    /**
     * Handles the transition to the on-site state.
     * The drone executes fire suppression actions such as pouring water.
     *
     * @param drone The drone instance entering the on-site state.
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("Drone " + drone.getId() + ": Arrived at fire incident");
        drone.pour();
//        drone.setState("Not Ready");
    }

    /**
     * Displays the current state of the drone as "On Site".
     */
    @Override
    public void displayState() {

    }
}