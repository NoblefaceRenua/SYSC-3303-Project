public class DroneReadyState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        System.out.println("[Drone " + drone.getId() + "] Ready and waiting for an event.");

        synchronized (drone) {
            while (drone.getCurrentEvent() == null) {  // Check if there's an event
                try {
                    drone.wait();  // Wait until an event is assigned
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        drone.receiveEvent();  // Process the event after assignment

    }

    /**
     *
     */
    @Override
    public void displayState() {
//        System.out.println("Drone" + droneId + "status: Ready");
//        if (empty) {
//            System.out.println("Drone" + droneId + "water tank status: Empty");
//            System.out.println("Drone: refilling...");
//            empty = false;
//        }
//        System.out.println("Drone awaiting assignment");
    }
}