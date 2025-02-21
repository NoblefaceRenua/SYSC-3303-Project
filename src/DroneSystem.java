import java.util.HashMap;
import java.util.Random;

class DroneSystem implements Runnable{
    private int travel_seconds_spent;
    private int pour_time; // in seconds
    private boolean stuck;
    private int agent_level_sensor; // indicates the water level
    private boolean arrival_sensor; // indicates if the drone is above the fire incident zone
    private int SPEED = 40; // the speed of the drone while flying over to the incident fire zone in m/s
    private int Id; // the ID of the drone
    private boolean nozzle;
    private boolean empty = false;
    private enum droneStatus {EMPTY, FULL, CRASH, ARRIVED};
    private droneStatus currStatus;
    private Message currentEvent = null;
    private Scheduler scheduler;
    private HashMap<String, DroneState> states;
    private DroneState currentState;

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";


    public DroneSystem(Scheduler scheduler, int id){
        travel_seconds_spent = 0;
        stuck = false;
        agent_level_sensor = 100; // the water level at the start
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
        currentState = states.get("Not Ready");
    }

    public void setState(String state){
        this.currentState = states.get(state);
        currentState.handleStateChanged(this); // run the state
    }

    public void addState(String state, DroneState newState){
        states.put(state, newState);
    }

    public synchronized boolean isAvailable() {
        return currStatus == droneStatus.EMPTY;
    }

    public synchronized void assignEvent(Message event) {
        this.currentEvent = event;
        currStatus = droneStatus.FULL;
        System.out.println("[Drone " + getId() + "] " + "Assigned event: " + event);
        this.notifyAll();  // Wake up this drone to process the event
    }

    public int getId(){
        return Id;
    }

    public synchronized Message getCurrentEvent() {
        return currentEvent;
    }

    public synchronized void receiveEvent() {
        //if (!stuck && !empty){
            while (currentEvent == null) {
                try {
                    wait();  // Wait until assigned an event
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        //}

        // Simulate event processing
        switch(currentEvent.getEventType().toUpperCase()){
            case "DRONE_CRASH":
                System.out.println(RED + "[Drone " + getId() + "] " + "Processing event: " + currentEvent + RESET);
                boolean result = makeUnavailable();
                break;
            default:
                System.out.println(YELLOW + "[Drone " + getId() + "] " + "Processing event: " + currentEvent + RESET);
        }

    }

    /**
     *
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

    public void returnToBase(){
        // simulate return flight time
        try {
            Thread.sleep(1500);  // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public void refillTank(){
        try {
            Thread.sleep(1000);  // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        agent_level_sensor = 100;
    }

    public boolean makeUnavailable(){
        currStatus = droneStatus.CRASH;
        return true;
    }

    public void fly(){
        try {
            Thread.sleep(1500);  // Simulate flight time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }


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
            if (agent_level_sensor == 0){
                empty = true;
            }
        }
        travel_seconds_spent = 0; // reset the seconds
        arrival_sensor = false;
        nozzle = false; // close the nozzle

    }

    public int getWaterLevel(){
        return this.agent_level_sensor;
    }


    public boolean getArrivalSensor(){
        return this.arrival_sensor;
    }

    public boolean getNozzle(){
        return this.nozzle;
    }

    public int getTravel_seconds_spent(){
        return this.travel_seconds_spent;
    }

}