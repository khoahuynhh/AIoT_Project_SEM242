package iot.controllers;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class VoiceController {

    private final Logger logger = LogManager.getLogger(VoiceController.class);

    @PostMapping("/voice-command")
    public ResponseEntity<String> processVoiceCommand(@RequestBody Map<String, String> payload) {
        String command = payload.get("command");
        System.out.println("Received Voice Command: " + command);

        // Send command to Python backend
        RestTemplate restTemplate = new RestTemplate();
        String pythonUrl = "http://localhost:8000/api/voice-command"; // Update with Python URL
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(pythonUrl, payload, String.class);
            logger.info("Python response: " + response.getBody());
            return ResponseEntity.ok(response.getBody()); // Return Python's response
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }
}
