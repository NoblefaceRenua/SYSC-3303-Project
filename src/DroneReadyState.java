/**
 * The DroneReadyState class represents a state where the drone is ready to be scheduled for an event.
 * In this state, the drone waits for an event assignment from the scheduler.
 */
public class DroneReadyState implements DroneState{
    /**
     * Handles the transition to the ready state.
     * The drone waits until it receives an event from the scheduler.
     *
     * @param drone The drone instance entering the ready state.
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("[Drone " + drone.getId() + "] Ready and waiting for an event.");

//        synchronized (drone) {
//            while (drone.getCurrentEvent() == null) {  // Check if there's an event
//                try {
//                    drone.wait();  // Wait until an event is assigned
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        }
        // wait until a datagram packet is sent to the drones port
        drone.sendReceive();

        // drone.receiveEvent();  // Process the event after assignment

    }

    /**
     * Displays the current state of the drone as "Ready".
     */
    @Override
    public void displayState() {
    }
}