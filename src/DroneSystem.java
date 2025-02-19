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
    private String status;
    private int distance_from_fire;
    private enum droneStatus {EMPTY, FULL, CRASH};
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
        System.out.println("[Drone] Assigned event: " + event);
        notifyAll();  // Wake up this drone to process the event
    }

    /**
     *
     */
    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (!stuck && !empty){
                    // set state to ready
                    setState("Ready");
                }
                while (currentEvent == null) {
                    try {
                        wait();  // Wait until assigned an event
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            // Simulate event processing
            switch(currentEvent.getEventType().toUpperCase()){
                case "DRONE_CRASH":
                    System.out.println(RED + "[Drone] Processing event: " + currentEvent + RESET);
                    makeUnavailable();
                    break;
                case "DRONE_REQUEST":
                    System.out.println(YELLOW+ "[Drone] Processing event: " + currentEvent + RESET);
                    break;
                default:
                    System.out.println("[Drone] Processing event: " + currentEvent);
            }

            if (currStatus != droneStatus.CRASH){
                // fly to the destination
                fly();

                // quench the fire
                try {
                    Thread.sleep(3000);  // Simulate processing time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                setState("On Site");

                // Event processing done
                synchronized (this) {
                    currentEvent = null;
                    currStatus = droneStatus.EMPTY;
                    System.out.println(GREEN + "[Drone] Finished event, ready for new assignment." + RESET);
                }

                synchronized (scheduler) {
                    scheduler.notifyAll();  // Notify scheduler that a drone is available
                }

                // the drone should fly to the destination and douse the fire, if it is not stuck
//        if(!stuck){
//            fly(distance_from_fire);
//            pour();
//        }
            } else {
                System.out.println(RED + "[Drone] CRASHED, Cannot perform more task." + RESET);
                break;
            }


            }


    }

    public void makeUnavailable(){
        currStatus = droneStatus.CRASH;
    }

    public void fly(){
        try {
            Thread.sleep(1500);  // Simulate flight time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // change the state to flying
        currentState = states.get("Flying");
    }

    public void simulateStuck(){
        Random rand = new Random();
        stuck = rand.nextBoolean();

        // change the state to stuck
    }

    public void pour(){
        // change state to onSite
//        currentState = states.get("On Site");

        // quench the fire
        nozzle = true;
        while(agent_level_sensor != 0 && nozzle){
            pour_time++;
            agent_level_sensor--; // pour the water on the fires
//            status = "Dousing flames....";
            if (agent_level_sensor == 0){
                empty = true;
            }
        }
        status = "Drone: Fire cleared & returning to base....";
        travel_seconds_spent = 0; // reset the seconds
        arrival_sensor = false;
        nozzle = false; // close the nozzle

        // change the state to notReady
//        currentState = states.get("Not Ready");
    }


}