

@Scheduled(fixedRate = 300000) // 300000 milliseconds = 5 minutes
public void combineTempFiles() {
    // Your combine logic here
}



String tempFileDirectory = "/path/to/temp/files/"; // Specify the directory where temporary files are located
String combinedFileName = "/path/to/combined/file/combined_file.txt"; // Specify the location for the combined file

try (BufferedWriter writer = new BufferedWriter(new FileWriter(combinedFileName))) {
    // List all files in the temp file directory
    File[] tempFiles = new File(tempFileDirectory).listFiles();

    if (tempFiles != null) {
        for (File tempFile : tempFiles) {
            // Read and combine the contents of each temp file
            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            // Optionally, delete the temp file after combining
            tempFile.delete();
        }
    }
} catch (IOException e) {
    // Handle exceptions
}


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileCombinationService {

    @Value("${temp.file.directory}") // Load the directory path from application.properties
    private String tempFileDirectory;

    @Scheduled(fixedRate = 300000) // 300000 milliseconds = 5 minutes
    public void combineTempFiles() {
        try {
            List<Path> tempFiles = Files.list(Paths.get(tempFileDirectory)).collect(Collectors.toList());

            if (tempFiles.isEmpty()) {
                // No temporary files found
                return;
            }

            String combinedFileName = "/path/to/combined/file/combined_file.txt"; // Specify the location for the combined file

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(combinedFileName, true))) {
                for (Path tempFilePath : tempFiles) {
                    // Read and combine the contents of each temp file
                    List<String> lines = Files.readAllLines(tempFilePath);
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

            // Optionally, delete the temporary files after combining
            for (Path tempFilePath : tempFiles) {
                Files.delete(tempFilePath);
            }
        } catch (IOException e) {
            // Handle exceptions
        }
    }
}
