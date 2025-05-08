// package com.iot.iot;

// import static org.junit.jupiter.api.Assertions.assertTrue;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import iot.service.FaceHandlingService;

// @SpringBootTest
// public class FaceHandlingServiceTest {

// @Autowired
// private FaceHandlingService faceHandlingService;

// @Test
// void testAddEmbeddingByUsername() {
// String testUsername = "user1";
// String testEmbedding = "123456789"; // replace with realistic data

// boolean result = faceHandlingService.addEmbeddingByUsername(testUsername,
// testEmbedding);

// assertTrue(result); // or assertFalse depending on your test data
// }

// void testGetEmbeddingByUsername() {
// String testUsername = "testuser";

// boolean result = faceHandlingService.getEmbeddingByUsername(testUsername);

// assertTrue(result); // or assertFalse depending on your DB state
// }
// }
