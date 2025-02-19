public class DroneReadyState implements DroneState{
    /**
     * @param drone
     */
    @Override
    public void handleStateChanged(DroneSystem drone) {

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

    /**
     * @param drone
     */
    @Override
    public void handleDroneStuck(DroneSystem drone) {

    }

    /**
     * @param drone
     */
    @Override
    public void handleEmpty(DroneSystem drone) {

    }
}