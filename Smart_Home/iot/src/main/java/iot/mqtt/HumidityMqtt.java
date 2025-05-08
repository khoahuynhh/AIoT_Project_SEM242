package iot.mqtt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import iot.config.JsonConverter;
import iot.model.Humidity;
import iot.service.SensorRecordingService;
import jakarta.annotation.PostConstruct;

@Component
public class HumidityMqtt {

    @Autowired(required = false)
    private MqttClient mqttClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SensorRecordingService sensorRecordingService;

    @Autowired
    private JsonConverter jsonConverter;

    @PostConstruct
    public void subscribe() throws MqttException {
        try {
            // Subscribe to the topic "temperature/device_id"
            mqttClient.subscribe("khoahuynh/feeds/V2", 1, (topic, message) -> {
                String payload = new String(message.getPayload());

                // SubscribedTemperatureData subscribedTemperatureData =
                // jsonConverter.getObjectMapper()
                // .readValue(payload, SubscribedTemperatureData.class);
                // String deviceId = subscribedTemperatureData.getDevice_id();
                Double humidityValue = Double.parseDouble(payload);

                // System.out.println("Device ID: " + deviceId);
                // System.out.println("Temperature: " + temperatureValue);
                Humidity humidity = Humidity.builder()
                        .unit("%")
                        .value(humidityValue)
                        .build();
                humidity.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                // System.out.println("Created date: " + temperature.getCreatedDate());
                // // Store the temperature in db
                sensorRecordingService.recordHumidity(humidity, 1L);

                // // // Store in Redis
                String key = "humidity:" + "1";
                redisTemplate.opsForValue().set(key, jsonConverter.getObjectMapper()
                        .writeValueAsString(humidity));

            });
        } catch (Exception e) {
            System.out.println("Error subscribing to topic: " + e.getMessage());
        }
    }
}