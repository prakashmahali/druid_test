1:
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.config.ConsumerProps;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @KafkaListener(id = "myContainer", topics = "your-topic-name")
    public void listen(String message) {
        // Process the received JSON message and add it to an array
    }

    // Create a Kafka consumer factory
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        consumerProps.put(ConsumerProps.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    // Create a Kafka listener container
    @Bean
    public ConcurrentMessageListenerContainer<String, String> kafkaListenerContainer() {
        ContainerProperties containerProps = new ContainerProperties("your-topic-name");
        containerProps.setMessageListener(new KafkaMessageListener());

        return new ConcurrentMessageListenerContainer<>(consumerFactory(), containerProps);
    }
}

2: import org.springframework.stereotype.Component;

@Component
public class KafkaMessageListener {

    private List<String> jsonRecords = new ArrayList<>();

    @KafkaListener(id = "myContainer", topics = "your-topic-name")
    public void listen(String message) {
        // Process the received JSON message and add it to the list
        jsonRecords.add(message);
    }

    // Add a method to get the accumulated JSON records
    public List<String> getJsonRecords() {
        return jsonRecords;
    }
}


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class FileWriterScheduler {

    private final KafkaMessageListener kafkaMessageListener;

    public FileWriterScheduler(KafkaMessageListener kafkaMessageListener) {
        this.kafkaMessageListener = kafkaMessageListener;
    }

    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void writeJsonRecordsToFile() {
        List<String> jsonRecords = kafkaMessageListener.getJsonRecords();

        if (!jsonRecords.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = dateFormat.format(new Date());

            try (FileWriter fileWriter = new FileWriter("output_" + timestamp + ".json")) {
                for (String record : jsonRecords) {
                    fileWriter.write(record);
                    fileWriter.write("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            jsonRecords.clear();
        }
    }
}

