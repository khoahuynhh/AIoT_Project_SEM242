package iot.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import iot.controllers.ApiResponse;
import iot.service.AITaskService;
import iot.service.FaceHandlingService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Service
public class AITaskServiceImpl implements AITaskService {
    @Value("${fastapi-url}")
    private String fastapiUrl;

    private final Logger logger = LogManager.getLogger(AITaskServiceImpl.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private FaceHandlingService faceHandlingService;

    @Override
    public boolean sendEmbeddingForIdentification(String username, List<Double> embedding) {
        try {
            // Find embedding from faceHandlingService

            String url = fastapiUrl + "/face/identify";
            ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new RequestEntity<FaceIndentifyDataRequest>(
                            FaceIndentifyDataRequest.builder().embedding(embedding).username(username).build(), null,
                            null),
                    new ParameterizedTypeReference<ApiResponse<Object>>() {
                    });
            logger.info("Send embedding to FastAPI for identification with username: " + username);
            return true; // Return true if the request was successful
        } catch (Exception e) {
            logger.error("Error occurred while sending embedding for identification: " + e.getMessage(), e);
            return false; // Return false if there was an error
        }
    }

    @Override
    public boolean sendUsernameForRegistration(String username) {
        try {
            String url = fastapiUrl + "/face/register";
            ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new RequestEntity<FaceRegisterDataRequest>(
                            FaceRegisterDataRequest.builder().username(username).build(),
                            null, null),
                    new ParameterizedTypeReference<ApiResponse<Object>>() {
                    });

            logger.info("Send username to FastAPI for registration with username: " + username);
            return true; // Return true if the request was successful
        } catch (Exception e) {
            logger.error("Error occurred while sending username for registration: " + e.getMessage(), e);
            return false; // Return false if there was an error
        }
    }
}

@Getter
@Setter
@Builder
class FaceIndentifyDataRequest {
    private String username;
    private List<Double> embedding;
}

@Builder
@Getter
@Setter
class FaceRegisterDataRequest {
    private String username;
    // Getters and setters
}
