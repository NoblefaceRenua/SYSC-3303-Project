public class Main {
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();

        DroneSystem drone1 = new DroneSystem(scheduler, 1);
        FireIncidentSubsystem fireIncident = new FireIncidentSubsystem(scheduler,"Sample_event_file.csv", "sample_zone_file.csv");
        scheduler.addDrone(drone1);

        Thread fireIncidentThread = new Thread(fireIncident, "FireIncident");
        Thread droneSubsystemThread1 = new Thread(drone1, "Drone 1");

        fireIncidentThread.start();
        droneSubsystemThread1.start();

    }
}
