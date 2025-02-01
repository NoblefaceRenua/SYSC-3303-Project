Files:
- DroneSystem.java: Contains information about each drone, as well as the drone id to differentiate between each of them.
- FireIncidentSubsystem.java: Used to read fire incidents from a file and send them to the scheduler class
- Message.java: Contains information about each fire incident, constructed from a specific incident from the csv files. Sent from the FireIncidentSubsytem to the Scheduler, which assigns each message to an idle drone
- Scheduler.java: Used to handle various fire incidents and assign them to different drones. 
- Zone.java: Indicates the zone at which a fire incident occurs.



Setup instructions:
Navigate to the Main.java file and run this file. The output will be displayed on the terminal, indicating that the files have been loaded successfully, when each event has been received by the scheduler, as well as when each drone has been assigned an event and when each event has been completed by a drone.