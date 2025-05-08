package iot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import iot.dto.DeviceDto;
import iot.mqtt.DeviceControlMqtt;
import jakarta.validation.Valid;

@Component
@RestController
@RequestMapping("/devicecontrol")
public class Esp32Controller {

    // @Autowired
    // private LightMqtt lightMqtt;

    @Autowired(required = false)
    private DeviceControlMqtt deviceControlMqtt;

    @PostMapping("/light/activate")
    public ResponseEntity<ApiResponse<Object>> toggleLight(
            @Valid @RequestBody DeviceDto.LightControlRequestDto requestBody) {
        String action = requestBody.getAction();
        if (action.equals("on")) {
            if (deviceControlMqtt.control("khoahuynh/feeds/V4", requestBody.getColor())) {
                return ResponseEntity.ok()
                        .body(ApiResponse.<Object>builder().message("Turn on successfully").build());
            } else {
                return ResponseEntity.status(500).body(ApiResponse.<Object>builder()
                        .message("Failed to send command to the device").build());
            }

        } else {
            if (deviceControlMqtt.control("khoahuynh/feeds/V4", "light off")) {
                return ResponseEntity.ok()
                        .body(ApiResponse.<Object>builder().message("Turn on successfully").build());
            } else {
                return ResponseEntity.status(500).body(ApiResponse.<Object>builder()
                        .message("Failed to send command to the device").build());
            }
        }
    }

    @PostMapping("/fan/activate")
    public ResponseEntity<ApiResponse<Object>> fanController(
            @Valid @RequestBody DeviceDto.FanControlRequestDto requestBody) {
        String action = requestBody.getAction();
        if (action.equals("on")) {
            if (deviceControlMqtt.control("khoahuynh/feeds/V7", requestBody.getLevel())) {
                return ResponseEntity.ok()
                        .body(ApiResponse.<Object>builder().message("Turn on successfully").build());
            } else {
                return ResponseEntity.status(500).body(ApiResponse.<Object>builder()
                        .message("Failed to send command to the device").build());
            }

        } else {
            if (deviceControlMqtt.control("khoahuynh/feeds/V5", "fan off")) {
                return ResponseEntity.ok()
                        .body(ApiResponse.<Object>builder().message("Turn on successfully").build());
            } else {
                return ResponseEntity.status(500).body(ApiResponse.<Object>builder()
                        .message("Failed to send command to the device").build());
            }
        }
    }

    @PostMapping("/door/activate")
    public ResponseEntity<ApiResponse<Object>> doorController(
            @Valid @RequestBody DeviceDto.DoorControlRequestDto requestBody) {
        String action = requestBody.getAction();
        if (action.equals("open")) {
            if (deviceControlMqtt.control("khoahuynh/feeds/V6", "open")) {
                return ResponseEntity.ok()
                        .body(ApiResponse.<Object>builder().message("Turn on successfully").build());
            } else {
                return ResponseEntity.status(500).body(ApiResponse.<Object>builder()
                        .message("Failed to send command to the device").build());
            }

        } else {
            if (deviceControlMqtt.control("khoahuynh/feeds/V6", "close")) {
                return ResponseEntity.ok()
                        .body(ApiResponse.<Object>builder().message("Turn on successfully").build());
            } else {
                return ResponseEntity.status(500).body(ApiResponse.<Object>builder()
                        .message("Failed to send command to the device").build());
            }
        }

    }

}
