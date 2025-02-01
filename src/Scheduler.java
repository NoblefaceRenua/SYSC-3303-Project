import java.util.*;

/**
 * The Scheduler class is responsible for managing and assigning fire incident events to available drones.
 * Events are stored in a priority queue, where higher-severity events are processed first.
 * The scheduler assigns events to drones that are available for dispatch.
 */
class Scheduler {
    private PriorityQueue<Message> eventQueue;
    private List<DroneSystem> drones = new ArrayList<>();

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
     * Adds an event to the priority queue and attempts to assign it to an available drone.
     * Notifies all waiting threads that a new event is available.
     *
     * @param event The fire incident message to be added to the queue.
     */
    public synchronized void addEvent(Message event) {
        eventQueue.add(event);
        System.out.println(" ");
        System.out.println("[Scheduler] Received event: " + event);
        assignEventToDrone(); // Try to assign an event immediately
        notifyAll();
    }

    /**
     * Assigns an event from the queue to an available drone.
     * If no drones are available, the thread waits until one becomes free.
     */
    public synchronized void assignEventToDrone() {
        while (eventQueue.isEmpty() || allDronesFull()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Assign event to an available drone
        for (DroneSystem drone : drones) {
            if (drone.isAvailable()) {
                Message event = eventQueue.poll();  // Get highest-priority event
                if (event != null) {
                    drone.assignEvent(event);
                    notifyAll();  // Notify other waiting threads
                }
                break;
            }
        }
    }

    /**
     * Checks if all drones are currently occupied.
     *
     * @return true if all drones are busy, false otherwise.
     */
    private boolean allDronesFull() {
        return drones.stream().allMatch(drone -> !drone.isAvailable());
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