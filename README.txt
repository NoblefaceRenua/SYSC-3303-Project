Files:
- DroneSystem.java: Contains information about each drone, as well as the drone id to differentiate between each of them.
- FireIncidentSubsystem.java: Used to read fire incidents from a file and send them to the scheduler class
- Message.java: Contains information about each fire incident, constructed from a specific incident from the csv files. Sent from the Fire Incident Subsystem to the Scheduler, which assigns each message to an idle drone
- Scheduler.java: Used to handle various fire incidents and assign them to different drones. 
- Zone.java: Indicates the zone at which a fire incident occurs.
- Sample_event_file.csv: Contains different sample events for testing purposes
- sample_zone_file.csv: Contains the zones and their parameters
- DroneState: Interface for the drone states
- DroneReadyState: Drone state indicating that the drone is ready to be scheduled
- DronestuckState: Drone state indicating that the drone has crashed or is stuck due to an error
- OnSiteState: Drone state indicating that the drone has arrived at the fire incident zone
- NotReadyState: Drone state indicating that the drone is not ready to be scheduled
- FlyingState: Drone state indicating that the drone is flying to the fire incident zone
- EmptyTankState: Drone state indicating that the drone's tank has been emptied

Setup instructions:
Navigate to the Main.java file and run this file. The output will be displayed on the terminal, indicating that the files have been loaded successfully, when each event has been received by the scheduler, as well as when each drone has been assigned an event and when each event has been completed by a drone.