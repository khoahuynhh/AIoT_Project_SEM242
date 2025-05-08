package iot.mqtt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import iot.config.JsonConverter;
import iot.model.Light;
import iot.service.SensorRecordingService;
import jakarta.annotation.PostConstruct;

@Component
public class LightMqtt {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired(required = false)
    private MqttClient mqttClient;

    @Autowired
    private JsonConverter jsonConverter;

    @Autowired
    private SensorRecordingService sensorRecordingService;

    private final String[] colors = { "red", "green", "blue", "yellow", "purple", "cyan" };

    @PostConstruct
    public void subscribe() throws MqttException {
        try {
            // Subscribe to the topic "temperature/device_id"
            mqttClient.subscribe("khoahuynh/feeds/V3", 1, (topic, message) -> {
                String payload = new String(message.getPayload());

                // SubscribedTemperatureData subscribedTemperatureData =
                // jsonConverter.getObjectMapper()
                // .readValue(payload, SubscribedTemperatureData.class);
                // String deviceId = subscribedTemperatureData.getDevice_id();
                Double lightValue = Double.parseDouble(payload);
                System.out.println("Light value: " + lightValue);
                Light light = Light.builder()
                        .unit("%")
                        .value(lightValue)
                        .build();
                light.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                // // Store the temperature in db
                sensorRecordingService.recordLight(light, 1L);

                // // // Store in Redis
                String key = "light:" + "1";
                redisTemplate.opsForValue().set(key, jsonConverter.getObjectMapper()
                        .writeValueAsString(light));

            });
        } catch (Exception e) {
            System.out.println("Error subscribing to topic: " + e.getMessage());
        }
    }

    public void publishCommand(String topic, String message) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttClient.publish(topic, mqttMessage);
    }

}
