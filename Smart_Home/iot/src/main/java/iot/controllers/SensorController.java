package iot.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import iot.dto.SensorDataDto;
import iot.model.Humidity;
import iot.model.Light;
import iot.model.Temperature;
import iot.service.SensorRecordingService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sensor")
public class SensorController {

	@Autowired
	private SensorRecordingService sensorRecordingService;

	// Get current temperature
	@PostMapping("/temperature/stream")
	public ResponseEntity<ApiResponse<SensorDataDto.CurrentTemperatureResponseDto>> getCurrentTemperature(
			@Valid @RequestBody SensorDataDto.CurrentTemperatureRequestDto request) {
		// Simulate a temperature reading
		Temperature temperature = sensorRecordingService.getCurrentTemperature(1L);
		if (temperature == null) {
			return ResponseEntity.status(404)
					.body(ApiResponse.<SensorDataDto.CurrentTemperatureResponseDto>builder()
							.message("Temperature not found for device ID: "
									+ request.getDeviceId())
							.build());
		}
		SensorDataDto.CurrentTemperatureResponseDto temperatureDto = SensorDataDto.CurrentTemperatureResponseDto
				.builder()
				.value(temperature.getValue())
				.unit(temperature.getUnit())
				.status(temperature.getStatus())
				.date(temperature.getCreatedDate())
				.build();
		return ResponseEntity.ok(ApiResponse.<SensorDataDto.CurrentTemperatureResponseDto>builder()
				.message("Current temperature retrieved successfully")
				.data(temperatureDto)
				.build());
	}

	@GetMapping("/temperature/by-date")
	public ResponseEntity<ApiResponse<List<SensorDataDto.CurrentTemperatureResponseDto>>> getAllTemperaturesByDate(
			@Valid @RequestParam(name = "date") String date) {

		List<Temperature> list = sensorRecordingService.getTemperaturesByDate(date);

		List<SensorDataDto.CurrentTemperatureResponseDto> dtoList = list.stream()
				.map(temp -> SensorDataDto.CurrentTemperatureResponseDto.builder()
						.value(temp.getValue())
						.unit(temp.getUnit())
						.status(temp.getStatus())
						.date(temp.getCreatedDate())
						.build())
				.toList();

		return ResponseEntity.ok(ApiResponse.<List<SensorDataDto.CurrentTemperatureResponseDto>>builder()
				.message("List of temperatures for: " + date)
				.data(dtoList)
				.build());
	}

	// Get current humidity
	@PostMapping("/humidity/stream")
	public ResponseEntity<ApiResponse<SensorDataDto.CurrentHumidityResponseDto>> getCurrentHumidity(
			@Valid @RequestBody SensorDataDto.CurrentHumidityRequestDto request) {
		// Simulate a humidity reading
		Humidity humidity = sensorRecordingService.getCurrentHumidity(1L);
		if (humidity == null) {
			return ResponseEntity
					.status(404).body(
							ApiResponse.<SensorDataDto.CurrentHumidityResponseDto>builder()
									.message("Humidity not found for device ID: "
											+ request.getDeviceId())
									.build());
		}
		SensorDataDto.CurrentHumidityResponseDto humidityDto = SensorDataDto.CurrentHumidityResponseDto
				.builder()
				.value(humidity.getValue())
				.unit(humidity.getUnit())
				.status(humidity.getStatus())
				.date(humidity.getCreatedDate())
				.build();
		return ResponseEntity.ok(ApiResponse.<SensorDataDto.CurrentHumidityResponseDto>builder()
				.message("Current humidity retrieved successfully")
				.data(humidityDto)
				.build());

	}

	@GetMapping("/humidity/by-date")
	public ResponseEntity<ApiResponse<List<SensorDataDto.CurrentHumidityResponseDto>>> getAllHumidityByDate(
			@RequestParam(name = "date") String date) {
		List<Humidity> list = sensorRecordingService.getHumidityByDate(date);
		List<SensorDataDto.CurrentHumidityResponseDto> dtoList = list.stream()
				.map(h -> SensorDataDto.CurrentHumidityResponseDto.builder()
						.value(h.getValue())
						.unit(h.getUnit())
						.status(h.getStatus())
						.date(h.getCreatedDate())
						.build())
				.toList();
		return ResponseEntity.ok(ApiResponse.<List<SensorDataDto.CurrentHumidityResponseDto>>builder()
				.message("List of humidity values for: " + date)
				.data(dtoList)
				.build());
	}

	// Get current light
	@PostMapping("/light/stream")
	public ResponseEntity<ApiResponse<SensorDataDto.CurrentLightResponseDto>> getCurrentHumidity(
			@Valid @RequestBody SensorDataDto.CurrentLightRequestDto request) {
		// Simulate a humidity reading
		Light light = sensorRecordingService.getCurrentLight(1L);
		if (light == null) {
			return ResponseEntity
					.status(404).body(
							ApiResponse.<SensorDataDto.CurrentLightResponseDto>builder()
									.message("Light not found for device ID: "
											+ request.getDeviceId())
									.build());
		}
		SensorDataDto.CurrentLightResponseDto lightDto = SensorDataDto.CurrentLightResponseDto
				.builder()
				.value(light.getValue())
				.unit(light.getUnit())
				.status(light.getStatus())
				.date(light.getCreatedDate())
				.build();
		return ResponseEntity.ok(ApiResponse.<SensorDataDto.CurrentLightResponseDto>builder()
				.message("Light humidity retrieved successfully")
				.data(lightDto)
				.build());
	}

	@GetMapping("/light/by-date")
	public ResponseEntity<ApiResponse<List<SensorDataDto.CurrentLightResponseDto>>> getAllLightByDate(
			@RequestParam(name = "date") String date) {
		List<Light> list = sensorRecordingService.getLightByDate(date);
		List<SensorDataDto.CurrentLightResponseDto> dtoList = list.stream()
				.map(l -> SensorDataDto.CurrentLightResponseDto.builder()
						.value(l.getValue())
						.unit(l.getUnit())
						.status(l.getStatus())
						.date(l.getCreatedDate())
						.build())
				.toList();
		return ResponseEntity.ok(ApiResponse.<List<SensorDataDto.CurrentLightResponseDto>>builder()
				.message("List of light values for: " + date)
				.data(dtoList)
				.build());
	}

}
