public class DroneReadyState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {
        drone.receiveEvent();

//        drone.assignEvent(); // get message from scheduler

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
