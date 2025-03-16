/**
 * The FlyingState class represents a state where the drone is flying to the fire incident zone.
 * The drone remains in this state until it arrives at the designated location.
 */
public class FlyingState implements DroneState{
    /**
     * Handles the transition to the flying state.
     * The drone simulates flight time before reaching the destination.
     *
     * @param drone The drone instance entering the flying state.
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("[Drone " + drone.getId() + "]: Flying to incident zone");
        drone.fly();
    }

    /**
     * Displays the current state of the drone as "Flying".
     */
    @Override
    public void displayState() {

    }
}
