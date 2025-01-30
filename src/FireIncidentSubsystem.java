import java.io.*;
import java.util.concurrent.BlockingQueue;

/**
 * The FireIncidentSubsystem reads the fire incidents from a file and sends them
 * to the scheduler through a thread-safe queue.
 */
public class FireIncidentSubsystem implements Runnable {
    private final BlockingQueue<Message> eventQueue; // Queue for sending fire incidents
    private final String file_Path; // File path containing fire incidents


    /**
     * Constructs the FireIncidentSubsystem.
     *
     * @param eventQueue The queue used to send fire incidents to the scheduler.
     * @param file_Path The path to the input file containing fire incidents.
     */

    public FireIncidentSubsystem(BlockingQueue<Message> eventQueue, String file_Path) {
        this.eventQueue = eventQueue;
        this.file_Path = file_Path;
    }


        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new FileReader(file_Path))) {
                String line;
                while ((line = br.readLine()) != null) { // Read each line from the file
                    String[] parts = line.split("\\s+"); // Split using any whitespace

                    // Validate that the line contains the expected four parts
                    if (parts.length == 4) {
                        String timestamp = parts[0];
                        int zoneID = Integer.parseInt(parts[1]);
                        String eventType = parts[2];
                        String severity = parts[3];

                        // Create a new FireIncident object
                        Message incident = new Message(timestamp, zoneID, eventType, severity);

                        // Add incident to the event queue (blocks if queue is full)
                        eventQueue.put(incident);
                        System.out.println("Fire Incident Subsystem: Sent " + incident);
                    } else {
                        System.out.println("Invalid line format: " + line);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading the fire incident file: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("FireIncidentSubsystem thread interrupted.");
                Thread.currentThread().interrupt(); // Restore the interrupt status
            }

    }
}
