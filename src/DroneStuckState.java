/**
 * The DroneStuckState class represents a state where the drone has crashed or is unable to continue operating.
 * The drone remains in this state permanently and cannot process new events.
 */
public class DroneStuckState implements DroneState{
    /**
     * Handles the transition to the stuck state.
     * The drone is marked as unavailable and cannot be scheduled for new tasks.
     *
     * @param drone The drone instance entering the stuck state.
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("Drone " + drone.getId()  + ": Crashed");
    }

    /**
     * Displays the current state of the drone as "Stuck".
     */
    @Override
    public void displayState() {

    }
}