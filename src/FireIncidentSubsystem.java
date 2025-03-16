import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The FireIncidentSubsystem is responsible for reading fire incidents and zone data from CSV files.
 * It processes the fire incidents and sends them to the scheduler at the correct simulated time.
 * This subsystem runs on a separate thread to simulate real-time event dispatching.
 */
public class FireIncidentSubsystem implements Runnable {
    private final Map<Integer, List<Message>> eventMap; // Maps timestamp → list of events
    private final String file_Path; // File path containing fire incidents
    private final String zone_path; // File path containing zone information
    private Map<Integer, Zone> zoneMap = new HashMap<>();
    private int maxTime = 0; // maxTime is the maximum time for simulation
    private DatagramSocket sendEventSocket;

    /**
     * Constructs the FireIncidentSubsystem.
     *
     * @param file_Path The path to the input file containing fire incidents.
     * @param zone_path The path to the input file containing zone information.
     */
    public FireIncidentSubsystem(String file_Path, String zone_path) {
        this.file_Path = file_Path;
        this.zone_path = zone_path;
        this.eventMap = new HashMap<>();

        try {
            sendEventSocket = new DatagramSocket();

        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * The main execution loop of the subsystem.
     * Loads zones and fire incidents from their respective files, then simulates time passage.
     * When the simulated time matches an event timestamp, the event is sent to the scheduler.
     */
    @Override
    public void run() {

        loadZones();
        loadFireIncidents();

        List<Integer> dronePorts = List.of(6000, 6100, 6200, 6300, 6400);
        sendZoneMap(getZones(), dronePorts);

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

                        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {

                            oos.writeObject(event);  // Serialize Message object
                            oos.flush();             // Ensure everything is written to the stream

                            byte[] msgBytes = bos.toByteArray();
                            DatagramPacket sendPacket = new DatagramPacket(msgBytes, msgBytes.length, InetAddress.getLocalHost(), 5000);

                            sendEventSocket.send(sendPacket);   // Send event to scheduler

                            System.out.println("[FireIncidentSubsystem] Sent event: " + event + "\n");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendZoneMap(Map<Integer, Zone> zoneMap, List<Integer> dronePorts) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress droneAddress = InetAddress.getByName("localhost");

            // Serialize the zone map
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(zoneMap);
            oos.flush();
            byte[] zoneMapData = bos.toByteArray();

            // Send the zone map to all drones
            for (int dronePort : dronePorts) {
                DatagramPacket packet = new DatagramPacket(
                        zoneMapData,
                        zoneMapData.length,
                        droneAddress,
                        dronePort
                );

                socket.send(packet);
                //System.out.println("[FireIncidentSubsystem] Sent zone map to Drone on port " + dronePort + "\n");
            }

        } catch (IOException e) {
            System.err.println("[FireIncidentSubsystem] Error sending zone map: " + e.getMessage());
        }
    }

    /**
     * Reads and loads zone data from the specified CSV file.
     * Populates the zoneMap with Zone objects.
     */
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

    /**
     * Reads and loads fire incident data from the specified CSV file.
     * Populates the eventMap with Message objects, mapping each event to its timestamp.
     */
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

    /**
     * Converts a time string (HH:MM:SS) into an integer representation in seconds.
     *
     * @param time The time string in HH:MM:SS format.
     * @return The converted time as an integer representing seconds.
     */
    public long convertTime(String time) {
        String[] parts = time.split(":");
        int i = (Integer.parseInt(parts[0]) * 3600 +
                Integer.parseInt(parts[1]) * 60 +
                Integer.parseInt(parts[2])) / 1000;
        return i; // Scale down for simulation
    }

    /**
     * Retrieves the map of zones loaded from the CSV file.
     *
     * @return A map containing zone IDs and their corresponding Zone objects.
     */
    public Map<Integer, Zone> getZones() {
        return zoneMap;
    }

    public static void main(String[] args) {
        FireIncidentSubsystem fireIncident = new FireIncidentSubsystem("Sample_event_file.csv", "sample_zone_file.csv");

        Thread fireIncidentThread = new Thread(fireIncident, "FireIncident");

        fireIncidentThread.start();

    }
}import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The FireIncidentSubsystem is responsible for reading fire incidents and zone data from CSV files.
 * It processes the fire incidents and sends them to the scheduler at the correct simulated time.
 * This subsystem runs on a separate thread to simulate real-time event dispatching.
 */
public class FireIncidentSubsystem implements Runnable {
    private final Map<Integer, List<Message>> eventMap; // Maps timestamp → list of events
    private final String file_Path; // File path containing fire incidents
    private final String zone_path; // File path containing zone information
    private Map<Integer, Zone> zoneMap = new HashMap<>();
    private int maxTime = 0; // maxTime is the maximum time for simulation
    private DatagramSocket sendEventSocket;

    /**
     * Constructs the FireIncidentSubsystem.
     *
     * @param file_Path The path to the input file containing fire incidents.
     * @param zone_path The path to the input file containing zone information.
     */
    public FireIncidentSubsystem(String file_Path, String zone_path) {
        this.file_Path = file_Path;
        this.zone_path = zone_path;
        this.eventMap = new HashMap<>();

        try {
            sendEventSocket = new DatagramSocket();

        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * The main execution loop of the subsystem.
     * Loads zones and fire incidents from their respective files, then simulates time passage.
     * When the simulated time matches an event timestamp, the event is sent to the scheduler.
     */
    @Override
    public void run() {
        List<Integer> dronePorts = List.of(6000, 6100, 6200, 6300, 6400);
        sendZoneMap(getZones(), dronePorts);

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

                        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {

                            oos.writeObject(event);  // Serialize Message object
                            oos.flush();             // Ensure everything is written to the stream

                            byte[] msgBytes = bos.toByteArray();
                            DatagramPacket sendPacket = new DatagramPacket(msgBytes, msgBytes.length, InetAddress.getLocalHost(), 5000);

                            sendEventSocket.send(sendPacket);   // Send event to scheduler

                            System.out.println("[FireIncidentSubsystem] Sent event: " + event + "\n");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendZoneMap(Map<Integer, Zone> zoneMap, List<Integer> dronePorts) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress droneAddress = InetAddress.getByName("localhost");

            // Serialize the zone map
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(zoneMap);
            oos.flush();
            byte[] zoneMapData = bos.toByteArray();

            // Send the zone map to all drones
            for (int dronePort : dronePorts) {
                DatagramPacket packet = new DatagramPacket(
                        zoneMapData,
                        zoneMapData.length,
                        droneAddress,
                        dronePort
                );

                socket.send(packet);
                //System.out.println("[FireIncidentSubsystem] Sent zone map to Drone on port " + dronePort + "\n");
            }

        } catch (IOException e) {
            System.err.println("[FireIncidentSubsystem] Error sending zone map: " + e.getMessage());
        }
    }

    /**
     * Reads and loads zone data from the specified CSV file.
     * Populates the zoneMap with Zone objects.
     */
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

    /**
     * Reads and loads fire incident data from the specified CSV file.
     * Populates the eventMap with Message objects, mapping each event to its timestamp.
     */
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

    /**
     * Converts a time string (HH:MM:SS) into an integer representation in seconds.
     *
     * @param time The time string in HH:MM:SS format.
     * @return The converted time as an integer representing seconds.
     */
    public long convertTime(String time) {
        String[] parts = time.split(":");
        int i = (Integer.parseInt(parts[0]) * 3600 +
                Integer.parseInt(parts[1]) * 60 +
                Integer.parseInt(parts[2])) / 1000;
        return i; // Scale down for simulation
    }

    /**
     * Retrieves the map of zones loaded from the CSV file.
     *
     * @return A map containing zone IDs and their corresponding Zone objects.
     */
    public Map<Integer, Zone> getZones() {
        return zoneMap;
    }

    public static void main(String[] args) {
        FireIncidentSubsystem fireIncident = new FireIncidentSubsystem("Sample_event_file.csv", "sample_zone_file.csv");

        Thread fireIncidentThread = new Thread(fireIncident, "FireIncident");

        fireIncidentThread.start();

    }
}
