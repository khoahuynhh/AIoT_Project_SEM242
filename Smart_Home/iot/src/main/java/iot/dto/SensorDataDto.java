package iot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SensorDataDto {

    @Builder
    @Getter
    @Setter
    static public class CurrentTemperatureResponseDto {

        private Double value;
        private String unit; // e.g. Celsius, Fahrenheit
        private String status;
        private String date;
    }

    @Builder
    @Getter
    @Setter
    static public class CurrentTemperatureRequestDto {
        @NotBlank(message = "Device ID cannot be blank")
        private String deviceId;
    }

    @Builder
    @Getter
    @Setter
    static public class CurrentHumidityResponseDto {

        private Double value;
        private String unit; // e.g. Percentage, Relative Humidity
        private String status;
        private String date;
    }

    @Builder
    @Getter
    @Setter
    static public class CurrentHumidityRequestDto {
        @NotBlank(message = "Device ID cannot be blank")
        private String deviceId;
    }

    @Builder
    @Getter
    @Setter
    static public class CurrentLightResponseDto {

        private Double value;
        private String unit; // e.g. Percentage, Relative Humidity
        private String status;
        private String date;
    }

    @Builder
    @Getter
    @Setter
    static public class CurrentLightRequestDto {
        @NotBlank(message = "Device ID cannot be blank")
        private String deviceId;
    }

}
