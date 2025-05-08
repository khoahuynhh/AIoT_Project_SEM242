package iot.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import iot.model.FaceEmbedding;
import iot.model.User;

@Repository
public interface FaceEmbeddingDao extends JpaRepository<FaceEmbedding, Long> {

    @Query(value = "SELECT * FROM face_embedding fe WHERE fe.user_id = ?1", nativeQuery = true)
    FaceEmbedding findByUserId(Long userId); // Find by user ID

    // Add a new embedding with user_id
}
