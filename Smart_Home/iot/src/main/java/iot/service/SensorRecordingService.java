package iot.service;

import java.util.List;


import iot.dto.SensorDataDto;
import iot.model.Humidity;
import iot.model.Light;
import iot.model.Temperature;

public interface SensorRecordingService {
    public void recordTemperature(Temperature temperature, Long deviceId) throws Exception;

    public Temperature getCurrentTemperature(Long deviceId);

    public List<Temperature> getTemperaturesByDate(String date);
    
    public void recordLight(Light temperature, Long deviceId) throws Exception;

    public Light getCurrentLight(Long deviceId);

    public List<Light> getLightByDate(String date);
    
    public void recordHumidity(Humidity temperature, Long deviceId) throws Exception;

    public Humidity getCurrentHumidity(Long deviceId);

    public List<Humidity> getHumidityByDate(String date);
}
