package iot.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class FaceDto {

    @Builder
    @Getter
    @Setter
    public static class GetEmbeddingResponseDto {
        private String username;
        private List<Double> embedding;
        // Getters and setters
    }

    @Builder
    @Getter
    @Setter
    public static class ConfirmIdentificationResponseDto {
        private int statusCode;
        private String message;
    }
}
