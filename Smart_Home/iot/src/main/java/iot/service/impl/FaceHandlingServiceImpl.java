package iot.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import iot.dao.FaceEmbeddingDao;
import iot.dao.UserDao;
import iot.model.FaceEmbedding;
import iot.model.User;
import iot.service.FaceHandlingService;
import iot.utils.JsonUtils;

@Service
public class FaceHandlingServiceImpl implements FaceHandlingService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private FaceEmbeddingDao faceEmbeddingDao;

    private final Logger logger = LogManager.getLogger(FaceHandlingServiceImpl.class);

    @Override
    public boolean addEmbeddingByUsername(String username, List<Double> embedding) {
        try {
            User user = userDao.findByUsername(username);
            logger.info("Find user with userid-username:" + username);
            // Convert List<Double> into Lob by using jackson
            String jsonEmbedding = JsonUtils.toJson(embedding);

            FaceEmbedding faceEmbedding;

            if (user != null) {
                Optional<FaceEmbedding> existing = faceEmbeddingDao.findById(user.getUserId());

                if (existing.isPresent()) {
                    faceEmbedding = existing.get(); // JPA now knows it's managed
                    faceEmbedding.setEmbedding(jsonEmbedding);
                } else {
                    faceEmbedding = FaceEmbedding.builder()
                            .user(user)
                            .embedding(jsonEmbedding)
                            .build();
                }

                faceEmbeddingDao.save(faceEmbedding);
                logger.info("Add face embedding successfully with username:" + username);
                return true;
            } else {
                logger.error("Fail to add face embedding with username:" + username);
                return false; // User not found
            }
        } catch (Exception e) {
            logger.error("Error occurred while adding embedding for user: " + username, e);
            return false;
        }
    }

    @Override
    public List<Double> getEmbeddingByUsername(String username) {
        try {
            Long userId = userDao.findByUsername(username).getUserId();
            FaceEmbedding faceEmbedding = faceEmbeddingDao.findByUserId(userId);
            if (faceEmbedding != null) {
                logger.info("Fetch face embedding successfully with username:" + username);
                // Convert string into List<Double>
                String jsonEmbedding = faceEmbedding.getEmbedding();
                List<Double> embedding = JsonUtils.fromJson(jsonEmbedding, ArrayList.class, Double.class);
                logger.info("Convert face embedding successfully with username:" + username + " and embedding:"
                        + embedding.toString());
                return embedding;
            } else {
                logger.error("Fetch face embedding not found with username:" + username);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching embedding for username: " + username, e);
            return null;
        }
    }
}
