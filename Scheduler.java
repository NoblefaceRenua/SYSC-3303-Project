import java.util.*;

class Scheduler {
    private PriorityQueue<Event> eventQueue;
    private List<DroneSubsystem> drones = new ArrayList<>();

    public Scheduler() {
        // Higher severity events are processed first, then earliest event if severity is equal
        eventQueue = new PriorityQueue<>(Comparator
                .comparingInt(Scheduler::getSeverityPriority)
                .thenComparingLong(Event::getTime));
    }

    public void addDrone(DroneSubsystem drone) {
        drones.add(drone);
    }

    //
    public synchronized void addEvent(Event event) {
        eventQueue.add(event);
        System.out.println("[Scheduler] Received event: " + event);
        assignEventToDrone(); // Try to assign an event immediately
    }

    private void assignEventToDrone() {
        for (DroneSubsystem drone : drones) {
            if (drone.isAvailable() && !eventQueue.isEmpty()) {
                Event nextEvent = eventQueue.poll();  // Get the highest-severity available event
                System.out.println("[Scheduler] Assigning event to drone: " + nextEvent);
                drone.handleEvent(nextEvent);
            }
        }
    }

    public void notifyDroneAvailable(DroneSubsystem drone) {
        System.out.println("[Scheduler] Drone is now available: " + drone.getId());
        assignEventToDrone(); // Try assigning the next event now that a drone is free
    }

    private static int getSeverityPriority(Event event) {
        return switch (event.getSeverity()) {
            case "HIGH" -> 1;
            case "MODERATE" -> 2;
            case "LOW" -> 3;
            default -> 4;
        };
    }
}
