/**
 * The EmptyTankState class represents a state where the drone has run out of water while attempting to extinguish a fire.
 * In this state, the drone must return to base to refill its tank before continuing operations.
 */
public class EmptyTankState implements DroneState{
    /**
     * Handles the transition to the empty tank state.
     * The drone returns to base and refills its tank.
     *
     * @param drone The drone instance entering the empty tank state.
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("Drone " + drone.getId() + ": Empty tank, returning to base");
        drone.returnToBase();
        drone.refillTank();
    }

    /**
     * Displays the current state of the drone as "Empty Tank".
     */
    @Override
    public void displayState() {

    }
}