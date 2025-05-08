package iot.service;

import java.util.List;

public interface FaceHandlingService {
    public boolean addEmbeddingByUsername(String username, List<Double> embedding);

    public List<Double> getEmbeddingByUsername(String username);
}
