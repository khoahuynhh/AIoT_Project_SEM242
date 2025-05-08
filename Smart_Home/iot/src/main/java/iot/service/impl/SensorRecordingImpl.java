package iot.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import iot.config.JsonConverter;
import iot.dao.DeviceDao;
import iot.dao.HumidityDao;
import iot.dao.LightDao;
import iot.dao.TemperatureDao;
import iot.model.Device;
import iot.model.Humidity;
import iot.model.Light;
import iot.model.Temperature;
import iot.service.SensorRecordingService;
import java.util.List;

@Service
public class SensorRecordingImpl implements SensorRecordingService {

    @Autowired
    private TemperatureDao temperatureDao;

    @Autowired
    private LightDao lightDao;

    @Autowired
    private HumidityDao humidityDao;

    @Autowired
    private JsonConverter jsonConverter;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final Logger logger = LogManager.getLogger(SensorRecordingImpl.class);

    // ########## Temperature ##########
    @Override
    public void recordTemperature(Temperature temperature, Long deviceId) throws Exception {
        try {
            // Check valid deviceId
            Device device = deviceDao.findById(deviceId).orElseThrow(() -> new Exception("Device not found"));
            temperature.setDevice(device);
            temperatureDao.save(temperature);
            logger.info("Record temperature successfully with deviceId:" + deviceId);
        } catch (Exception e) {
            logger.error("Error recording temperature: " + e.getMessage());
        }
    }

    @Override
    public Temperature getCurrentTemperature(Long deviceId) {
        try {
            String cache = redisTemplate.opsForValue().get("temperature:" + 1);
            Temperature temperature = null;
            if (cache == null) {
                System.out.println("Cache is null, get temperature from db");
                temperature = temperatureDao.getLatesTemperatureByDeviceId(deviceId);
            } else {
                temperature = jsonConverter.getObjectMapper().readValue(cache, Temperature.class);
            }
            return temperature;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Temperature> getTemperaturesByDate(String date) {
        return temperatureDao.findByCreatedDateOrderByTime(date);
    }

    // ########## Light ##########
    @Override
    public void recordLight(Light light, Long deviceId) throws Exception {
        try {
            // Check valid deviceId
            Device device = deviceDao.findById(deviceId).orElseThrow(() -> new Exception("Device not found"));
            light.setDevice(device);
            lightDao.save(light);
            logger.info("Record light sensor successfully with deviceId:" + deviceId);
        } catch (Exception e) {
            logger.error("Error recording light sensor: " + e.getMessage());
        }

    }

    @Override
    public Light getCurrentLight(Long deviceId) {
        try {
            String cache = redisTemplate.opsForValue().get("light:" + 1);
            Light light = null;
            if (cache == null) {
                System.out.println("Cache is null, get temperature from db");
                light = lightDao.getLatestLightByDeviceId(deviceId);
            } else {
                light = jsonConverter.getObjectMapper().readValue(cache, Light.class);
            }
            return light;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Light> getLightByDate(String date) {
        return lightDao.findByCreatedDateOrderByTime(date);
    }

    // ########## Humidity ##########
    @Override
    public void recordHumidity(Humidity humidity, Long deviceId) throws Exception {
        try {
            // Check valid deviceId
            Device device = deviceDao.findById(deviceId).orElseThrow(() -> new Exception("Device not found"));
            humidity.setDevice(device);
            humidityDao.save(humidity);
            logger.info("Record humidity successfully with deviceId:" + deviceId);
        } catch (Exception e) {
            logger.error("Error recording humidity : " + e.getMessage());
        }
    }

    @Override
    public Humidity getCurrentHumidity(Long deviceId) {
        try {
            String cache = redisTemplate.opsForValue().get("humidity:" + 1);
            Humidity humidity = null;
            if (cache == null) {
                System.out.println("Cache is null, get temperature from db");
                humidity = humidityDao.getLatestHumidityByDeviceId(deviceId);
            } else {
                humidity = jsonConverter.getObjectMapper().readValue(cache, Humidity.class);
            }
            return humidity;
        } catch (Exception e) {
            System.out.println("Error get humidity from cache: " + e.getMessage());
            return null;
        }
    }

    public List<Humidity> getHumidityByDate(String date) {
        return humidityDao.findByCreatedDateOrderByTime(date);
    }
}
