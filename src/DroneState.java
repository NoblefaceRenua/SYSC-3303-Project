/**
 * The DroneState interface defines the behavior for different states a drone can be in.
 * Implementing classes represent specific operational states of the drone, 
 * such as being ready, flying, on-site, or stuck due to an error.
 */
public interface DroneState {

    /**
     * Handles the actions that should occur when a drone transitions into this state.
     *
     * @param drone The drone instance that is changing to this state.
     */
    void handleStateChanged(DroneSystem drone);

    /**
     * Displays the current state of the drone.
     * This can include logging messages or updating UI elements to reflect the drone's status.
     */
    void displayState();
}