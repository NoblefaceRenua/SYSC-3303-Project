import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Random;

/**
 * The DroneSystem class represents a drone in the firefighting system.
 * Each drone can process fire incident events, transition through different states,
 * and interact with the Scheduler to receive and complete tasks.
 */
class DroneSystem implements Runnable{
    private int travel_seconds_spent;
    private int pour_time; // in seconds
    private boolean stuck;
    private int agent_level_sensor; // indicates the water level
    private boolean arrival_sensor; // indicates if the drone is above the fire incident zone
    // private int SPEED = 40; // the speed of the drone while flying over to the incident fire zone in m/s
    private int Id; // the ID of the drone
    private boolean nozzle;
    private boolean empty = false;
    private enum droneStatus {EMPTY, FULL, CRASH, ARRIVED};
    private droneStatus currStatus;
    private Message currentEvent = null;
    private Scheduler scheduler;
    private HashMap<String, DroneState> states;
    private DroneState currentState;
    private DatagramSocket socket;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private String confirm_finish;

    /** ANSI color codes for console output formatting. */
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";


    /**
     * Constructs a DroneSystem instance with an assigned ID and reference to the Scheduler.
     *
     * @param id The unique identifier for the drone.
     * @param port The port the drone listens on for new messages.
     */
    public DroneSystem(int id, int port){
        travel_seconds_spent = 0;
        stuck = false;
        agent_level_sensor = 20; // the water level at the start in litres
        this.Id = id;
        pour_time = 0;
        this.currStatus = droneStatus.EMPTY;
        this.scheduler = scheduler;
        states = new HashMap<>();
        addState("Not Ready", new NotReadyState());
        addState("Ready", new DroneReadyState());
        addState("Flying", new FlyingState());
        addState("On Site", new OnSiteState());
        addState("Stuck", new DroneStuckState());
        addState("Empty tank", new TankEmptyState());

        // initialize socket on port 6000
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        setState("Not Ready");
    }

    /**
     * Sets the current state of the drone and executes its behavior.
     *
     * @param state The name of the state to transition to.
     */
    public void setState(String state){
        this.currentState = states.get(state);
        currentState.handleStateChanged(this); // run the state
    }

    public void sendReceive(){
        byte[] data = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);
        try {
            socket.receive(receivePacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // this is to split up the info from the received message
        String received = new String(receivePacket.getData());
        received = received.substring(7, received.length() - 2);
        String[] info = received.split(",");
        for (String s : info) {
            s = s.substring(s.indexOf("=") + 1, s.length() - 1);
        }

        // generate the currentEvent
        currentEvent = new Message(Integer.parseInt(info[0]), Integer.parseInt(info[1]), info[2], info[3]);

        // the drone should just run as usual without thinking of whether the zone has been cleared b
        // but based on the severity the reply should be different depending on the amount of water it had
        receiveEvent();

        run();

        byte[] reply = confirm_finish.getBytes();
        sendPacket = new DatagramPacket(reply, reply.length, receivePacket.getAddress(), receivePacket.getPort());
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(confirm_finish + RESET);
    }

    /**
     * Adds a new state to the drone's state map.
     *
     * @param state The name of the state.
     * @param newState The state object to be added.
     */
    public void addState(String state, DroneState newState){
        states.put(state, newState);
    }

    /**
     * Checks if the drone is available for assignment.
     *
     * @return True if the drone is available, false otherwise.
     */
    public synchronized boolean isAvailable() {
        return currStatus == droneStatus.EMPTY;
    }

    /**
     * Assigns an event to the drone and updates its status.
     *
     * @param event The fire incident event to be assigned.
     */
    public synchronized void assignEvent(Message event) {
        this.currentEvent = event;
        currStatus = droneStatus.FULL;
        System.out.println("[Drone " + getId() + "] " + "Assigned event: " + event);
        this.notifyAll();  // Wake up this drone to process the event
    }

    /**
     * Retrieves the unique ID of the drone.
     *
     * @return The drone's ID.
     */
    public int getId(){
        return Id;
    }

    /**
     * Retrieves the currently assigned event.
     *
     * @return The event assigned to the drone.
     */
    public synchronized Message getCurrentEvent() {
        return currentEvent;
    }

    /**
     * Receives the currently assigned event.
     *
     */
    public synchronized void receiveEvent() {

        while (currentEvent == null) {
            try {
                wait();  // Wait until assigned an event
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


        // Simulate event processing
        switch(currentEvent.getEventType().toUpperCase()){
            case "DRONE_CRASH":
                System.out.println(RED + "[Drone " + getId() + "] " + "Processing event: " + currentEvent + RESET);
                boolean result = makeUnavailable();
                setConfirm_finish("[Drone " + getId() + "] " + "Status: Crashed");
                break;
            default:
                System.out.println(YELLOW + "[Drone " + getId() + "] " + "Processing event: " + currentEvent + RESET);
        }

    }

    public void setConfirm_finish(String confirm_finish){
        this.confirm_finish = confirm_finish;
    }

    /**
     * The main operational loop of the drone, handling event assignment and execution.
     */
    @Override
    public void run() {
        while (true) {

            setState("Ready");


            // case where the drone does not crash
            if (currStatus != droneStatus.CRASH){
                // fly to the destination
                setState("Flying");


                setState("On Site");


                // Event processing done
                synchronized (this) {
                    currentEvent = null;
                    currStatus = droneStatus.EMPTY;
                    System.out.println(GREEN + "[Drone " + getId() + "] " + "Finished event, ready for new assignment." + RESET + "\n");
                }
                setState("Not Ready");

                synchronized (scheduler) {
                    scheduler.notifyAll();  // Notify scheduler that a drone is available
                }

            } else {
                System.out.println(RED + "[Drone " + getId() + "] " + "CRASHED, Cannot perform more task." + RESET);
                setState("Stuck");
                break;
            }


        }


    }

    /**
     * Simulates the drone returning to its base after completing a task.
     */
    public void returnToBase(){
        // simulate return flight time
        try {
            Thread.sleep(1500);  // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Refills the drone's water tank to full capacity.
     */
    public void refillTank(){
        try {
            Thread.sleep(1000);  // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        agent_level_sensor = 100;
    }

    /**
     * Marks the drone as unavailable due to a crash.
     *
     * @return True if the drone is marked as crashed.
     */
    public boolean makeUnavailable(){
        currStatus = droneStatus.CRASH;
        return true;
    }

    /**
     * Simulates the drone flying to the fire incident zone.
     */
    public void fly(){
        try {
            Thread.sleep(1500);  // Simulate flight time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    /**
     * Simulates the drone extinguishing a fire by releasing water.
     */
    public void pour(){

        // quench the fire
        try {
            Thread.sleep(3000);  // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        nozzle = true;
        while(agent_level_sensor != 0){
            pour_time++;
            agent_level_sensor--; // pour the water on the fires
//            status = "Dousing flames....";
            currentEvent.reduceFire();
            // if the fire is quenched then return to base for re-assignment
            if (currentEvent.getFireLevel() == 0){
                setConfirm_finish("[Drone " + getId() + "] " + "Status: Fire quenched, returning to base for refill");
                break;
            } // if tank is empty return to base for refill
            else if (agent_level_sensor == 0){
                setConfirm_finish("[Drone " + getId() + "] " + "Status: Tank empty, returning for refill");
                empty = true;
            }
        }
        travel_seconds_spent = 0; // reset the seconds
        arrival_sensor = false;
        nozzle = false; // close the nozzle
        setState("Empty tank");
    }

    /**
     * Retrieves the current water level in the drone's tank.
     *
     * @return The remaining water level.
     */
    public int getWaterLevel(){
        return this.agent_level_sensor;
    }

    /**
     * Checks if the drone has arrived at the fire incident zone.
     *
     * @return True if the drone has arrived, false otherwise.
     */
    public boolean getArrivalSensor(){
        return this.arrival_sensor;
    }

    /**
     * Checks if the nozzle is currently open.
     *
     * @return True if the nozzle is open, false otherwise.
     */
    public boolean getNozzle(){
        return this.nozzle;
    }

    /**
     * Retrieves the total travel time spent flying to the incident location.
     *
     * @return The travel time in seconds.
     */
    public int getTravel_seconds_spent(){
        return this.travel_seconds_spent;
    }

    public static void main(String[] args) {
        DroneSystem drone1 = new DroneSystem(1, 6000);
        DroneSystem drone2 = new DroneSystem(2, 6100);
        DroneSystem drone3 = new DroneSystem(3, 6200);
        DroneSystem drone4 = new DroneSystem(4, 6300);
        DroneSystem drone5 = new DroneSystem(5, 6400);

        Thread d1 = new Thread(drone1);
        Thread d2 = new Thread(drone2);
        Thread d3 = new Thread(drone3);
        Thread d4 = new Thread(drone4);
        Thread d5 = new Thread(drone5);

        d1.start();
        d2.start();
        d3.start();
        d4.start();
        d5.start();
    }

}