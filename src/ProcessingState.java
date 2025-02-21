public class ProcessingState implements SchedulerState {
    @Override
    public void addEvent(Scheduler scheduler, Message event) {
        synchronized (scheduler) {
            scheduler.getEventQueue().add(event);
            System.out.println("[Scheduler] Event added: " + event);
            assignEventToDrone(scheduler);
            scheduler.notifyAll();  // Wake up any waiting threads
        }
    }

    @Override
    public void assignEventToDrone(Scheduler scheduler) {

        synchronized (scheduler) {
            while (scheduler.getEventQueue().isEmpty() || scheduler.allDronesFull()) {
                try {
                    System.out.println("[Scheduler] No Event or Drone Available");
                    scheduler.wait();  // Wait until an event is added or a drone becomes available
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }


            while (!scheduler.eventQueue.isEmpty() || !scheduler.allDronesFull()) {
                for (DroneSystem drone : scheduler.drones) {
                    if (drone.isAvailable()) {
                        Message event = scheduler.eventQueue.poll();  // Get the next event
                        if (event != null) {
                            drone.assignEvent(event);
                            System.out.println("[Scheduler] Assigned event to Drone " + drone.getId());
                            scheduler.notifyAll();  // Wake up waiting threads
                        }
                    }
                }
            }


//           if (scheduler.eventQueue.isEmpty()) {
//               scheduler.setState(new IdleState());
//           } else {
//               scheduler.setState(new WaitingState());
//           }
        }
    }
}
