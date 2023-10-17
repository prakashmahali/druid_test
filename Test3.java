import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.FileWriter;
import java.io.IOException;

public class JsonFileWriter {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        // Configure the ObjectMapper to disable character escaping
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);

        try (FileWriter fileWriter = new FileWriter("output.json")) {
            // Create an object to serialize
            MyObject myObject = new MyObject("Hello, World!", "\\Escape\\Me\\");

            // Serialize the object to a JSON file
            objectMapper.writeValue(fileWriter, myObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MyObject {
    private String text1;
    private String text2;

    public MyObject(String text1, String text2) {
        this.text1 = text1;
        this.text2 = text2;
    }

    // Getter and setter methods for text1 and text2
}
