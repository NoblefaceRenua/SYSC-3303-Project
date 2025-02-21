import java.io.*;
import java.util.*;

/**
 * The FireIncidentSubsystem reads the fire incidents from a file and sends them
 * to the scheduler through a thread-safe queue.
 */
public class FireIncidentSubsystem implements Runnable {
    private final Map<Integer, List<Message>> eventMap; // Maps timestamp → list of events
    private final String file_Path; // File path containing fire incidents
    private final String zone_path; // File path containing zone information
    private Map<Integer, Zone> zoneMap = new HashMap<>();
    private Scheduler scheduler;
    private int maxTime = 0; // maxTime is the maximum time for simulation

    /**
     * Constructs the FireIncidentSubsystem.
     *
     *
     * @param file_Path The path to the input file containing fire incidents.
     */
    public FireIncidentSubsystem(Scheduler scheduler, String file_Path, String zone_path) {
        this.scheduler = scheduler;
        this.file_Path = file_Path;
        this.zone_path = zone_path;
        this.eventMap = new HashMap<>();
    }


    @Override
    public void run() {
        loadZones();
        loadFireIncidents();

        int count = 0;
        while (count <= maxTime) {
            // Simulate time passage by incrementing every 500ms
            try {
                Thread.sleep(500);
                count++;

                // If the current time matches any event timestamp, send those events to the scheduler
                if (eventMap.containsKey(count)) {
                    List<Message> events = eventMap.get(count);
                    for (Message event : events) {
                        scheduler.addEvent(event);  // Send event to scheduler
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to read the Zones CSV file
    private void loadZones() {
        try (BufferedReader br = new BufferedReader(new FileReader(zone_path))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                } // Skip header

                String[] values = line.split(",");

                // Parse zone ID (first value)
                int zoneID = Integer.parseInt(values[0].trim());

                // Extract (number;number) from values[1] and values[2]
                String[] startValues = values[1].replaceAll("[()]", "").split(";");
                String[] endValues = values[2].replaceAll("[()]", "").split(";");

                // Parse start and end values
                int start1 = Integer.parseInt(startValues[0].trim());
                int start2 = Integer.parseInt(startValues[1].trim());
                int end1 = Integer.parseInt(endValues[0].trim());
                int end2 = Integer.parseInt(endValues[1].trim());

                // Create Zone object
                Zone zone = new Zone(zoneID, start1, start2, end1, end2);
                zoneMap.put(zoneID, zone);
                //System.out.println(zone);
            }
            System.out.println("[FireIncidentSubsystem] Zones loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number: " + e.getMessage());
        }
    }

    // Method to read the Fire Incidents CSV file
    private void loadFireIncidents() {
        try (BufferedReader br = new BufferedReader(new FileReader(file_Path))) {
            br.readLine(); // Skip the first line

            String line;
            while ((line = br.readLine()) != null) { // Read each line from the file
                String[] parts = line.split(","); // Split using any whitespace

                // Validate that the line contains the expected four parts
                if (parts.length == 4) {
                    int timestamp = (int) convertTime(parts[0]);
                    int zoneID = Integer.parseInt(parts[1]);
                    String eventType = parts[2];
                    String severity = parts[3];

                    // Create a new FireIncident object
                    Message incident = new Message(timestamp, zoneID, eventType, severity);
                    //System.out.println(timestamp + " " + zoneID + " " + eventType + " " + severity);

                    // Store event in the map
                    eventMap.computeIfAbsent(timestamp, k -> new ArrayList<>()).add(incident);
                    //System.out.println("Fire Incident Subsystem: Sent " + incident);

                    // Get the event with the highest time
                    if (maxTime < timestamp){
                        maxTime = timestamp;
                    }
                } else {
                    System.out.println("Invalid line format: " + line);
                }
            }
            System.out.println("[FireIncidentSubsystem] Event loaded successfully." + "\n");
        } catch (IOException e) {
            System.err.println("Error reading the fire incident file: " + e.getMessage());
        }
    }

    public long convertTime(String time) {
        String[] parts = time.split(":");
        int i = (Integer.parseInt(parts[0]) * 3600 +
                Integer.parseInt(parts[1]) * 60 +
                Integer.parseInt(parts[2])) / 1000;
        return i; // Scale down for simulation
    }

    public Map<Integer, Zone> getZones() {
        return zoneMap;
    }

}