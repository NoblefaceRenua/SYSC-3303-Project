/**
 * The NotReadyState class represents a state where the drone is not ready to be scheduled.
 * This can be due to maintenance, refueling, or other internal constraints.
 */
public class NotReadyState implements DroneState{
    /**
     * Handles the transition to the not ready state.
     * The drone remains unavailable for scheduling.
     *
     * @param drone The drone instance entering the not ready state.
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("Drone " + drone.getId() + ": Not Ready");
        drone.refillTank();
    }

    /**
     * Displays the current state of the drone as "Not Ready".
     */
    @Override
    public void displayState() {

    }
}