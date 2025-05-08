package iot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class DeviceDto {

    @Builder
    @Getter
    @Setter
    static public class AddDeviceRequestDto {
        private String HomeId;
        private String deviceName;
        private String deviceNumber;
        private String deviceType;
        private String deviceStatus;
    }

    @Builder
    @Getter
    @Setter
    static public class LightControlRequestDto {
        @NotBlank(message = "Device ID is required")
        private String deviceId;
        @NotBlank(message = "Action is required")
        private String action;
        private String color;
    }

    @Builder
    @Getter
    @Setter
    static public class FanControlRequestDto {
        @NotBlank(message = "Device ID is required")
        private String deviceId;
        @NotBlank(message = "Action is required")
        private String action;
        private String level;
    }

    @Builder
    @Getter
    @Setter
    static public class DoorControlRequestDto {
        @NotBlank(message = "Device ID is required")
        private String deviceId;
        @NotBlank(message = "Action is required")
        private String action;
    }
}