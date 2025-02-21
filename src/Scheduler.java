import java.util.*;

/**
 * The Scheduler class is responsible for managing and assigning fire incident events to available drones.
 * Events are stored in a priority queue, where higher-severity events are processed first.
 * The scheduler assigns events to drones that are available for dispatch.
 */
class Scheduler {
    protected PriorityQueue<Message> eventQueue;
    protected List<DroneSystem> drones = new ArrayList<>();
    private SchedulerState state;

    /**
     * Constructs a Scheduler instance.
     * Initializes the priority queue with a comparator that prioritizes events by severity,
     * then by timestamp in case of equal severity.
     */
    public Scheduler() {
        // Custom comparator to compare events by severity first, then by timestamp
        eventQueue = new PriorityQueue<>(new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                // Compare severity first
                int severityComparison = Integer.compare(getSeverityPriority(m1), getSeverityPriority(m2));
                if (severityComparison != 0) {
                    return severityComparison;
                }

                // If severity is the same, compare by timestamp
                return Integer.compare(m1.getTimestamp(), m2.getTimestamp());
            }
        });

        this.state = new IdleState(); // Start in Idle state
    }

    /**
     * Adds a drone to the scheduler's list of drones.
     *
     * @param drone The drone system to be added.
     */
    public void addDrone(DroneSystem drone) {
        drones.add(drone);
    }

    /**
     * Sets the state of the scheduler.
     *
     * @param state of the scheduler
     */
    public synchronized void setState(SchedulerState state) {
        this.state = state;
    }

    /**
     * Return drone in the scheduler's list of drones.
     *
     * @return list of the drones in the scheduler
     */
    public List<DroneSystem> getDroneList(){
        return drones;
    }

    /**
          * Adds an event to the priority queue and attempts to assign it to an available drone.
          * Notifies all waiting threads that a new event is available.
          *
          * @param event The fire incident message to be added to the queue.
          */
    public synchronized void addEvent(Message event) {
        state.addEvent(this, event);
    }


    /**
     * Checks if all drones are currently occupied.
     *
     * @return true if all drones are busy, false otherwise.
     */
    public boolean allDronesFull() {
        return drones.stream().allMatch(drone -> !drone.isAvailable());
    }

    /**
     * Checks if all drones are currently occupied.
     *
     * @return priority queue of the scheduler.
     */
    public PriorityQueue<Message> getEventQueue() {
        return eventQueue;
    }


    /**
     * Determines the priority of an event based on its severity level.
     * Lower values indicate higher priority.
     *
     * @param event The fire incident message.
     * @return An integer representing the priority (1 = High, 2 = Moderate, 3 = Low, 4 = Default).
     */
    private static int getSeverityPriority(Message event) {
        return switch (event.getSeverity()) {
            case "High" -> 1;
            case "Moderate" -> 2;
            case "Low" -> 3;
            default -> 4;
        };
    }
}