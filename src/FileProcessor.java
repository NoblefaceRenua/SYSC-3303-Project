import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileProcessor {
    public static void main(String[] args) {
        String filePath = "data.txt"; // Change this to your file path

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) { // Read each line
                String[] parts = line.split(","); // Split by comma

                if (parts.length == 4) { // Ensure there are 4 parts
                    String type = parts[0].trim();
                    String brand = parts[1].trim();
                    String color = parts[2].trim();
                    String category = parts[3].trim();

                    // Do different things with each part
                    System.out.println("Processing:");
                    System.out.println("Type: " + type);
                    System.out.println("Brand: " + brand);
                    System.out.println("Color: " + color);
                    System.out.println("Category: " + category);
                    System.out.println("-------------------------");
                } else {
                    System.out.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
