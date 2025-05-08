package iot.mqtt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import iot.config.JsonConverter;
import iot.model.Device;
import iot.model.Temperature;
import iot.service.SensorRecordingService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

//Class for subcribed data
@Getter
class SubscribedTemperatureData {
    private String device_id;
    private Double temperature;
    // Getters and setters
}

@Component
// public class TemperatureSubscriber {
public class TemperatureMqtt {

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
            mqttClient.subscribe("khoahuynh/feeds/V1", 1, (topic, message) -> {
                String payload = new String(message.getPayload());

                // SubscribedTemperatureData subscribedTemperatureData =
                // jsonConverter.getObjectMapper()
                // .readValue(payload, SubscribedTemperatureData.class);
                // String deviceId = subscribedTemperatureData.getDevice_id();
                Double temperatureValue = Double.parseDouble(payload);

                System.out.println("Temperature: " + temperatureValue);
                Temperature temperature = Temperature.builder()
                        .unit("Celsius")
                        .value(temperatureValue)
                        .build();
                temperature
                        .setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                // System.out.println("Created date: " + temperature.getCreatedDate());
                // // Store the temperature in db
                sensorRecordingService.recordTemperature(temperature, 1L);

                // // // Store in Redis
                String key = "temperature:" + "1";
                redisTemplate.opsForValue().set(key, jsonConverter.getObjectMapper()
                        .writeValueAsString(temperature));

            });
        } catch (Exception e) {
            System.out.println("Error subscribing to topic: " + e.getMessage());
        }
    }
}