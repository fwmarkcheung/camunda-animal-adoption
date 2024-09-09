package io.camunda.demo.pick_animal.controller;

import io.camunda.demo.pick_animal.db.UserchoiceNotFoundException;
import io.camunda.demo.pick_animal.db.UserchoiceRepository;
import io.camunda.demo.pick_animal.service.utils.MediaTypeExtractor;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageControllerTest {

  @Mock
  private UserchoiceRepository userchoiceRepository;

  @InjectMocks
  private ImageController imageController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetImage_Success() {
    // Arrange
    String recordId = "123";
    String imageType = "jpg";
    String base64Image = Base64.getEncoder().encodeToString("dummy_image".getBytes());
    Pair<String, String> imageData = new Pair<>(imageType, base64Image);
    when(userchoiceRepository.getBase64ImageStrFromHarperDB(recordId)).thenReturn(imageData);

    // Act
    ResponseEntity<byte[]> response = imageController.getImage(recordId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertArrayEquals("dummy_image".getBytes(), response.getBody());

    HttpHeaders headers = response.getHeaders();
    assertNotNull(headers.getFirst(HttpHeaders.CONTENT_TYPE));

  }

  @Test
  void testGetImage_NotFound() {
    // Arrange
    String recordId = "123";
    when(userchoiceRepository.getBase64ImageStrFromHarperDB(recordId)).thenReturn(new Pair<>(null, null));

    // Act
    ResponseEntity<byte[]> response = imageController.getImage(recordId);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  void testHandleResourceNotFoundException() {
    // Arrange
    UserchoiceNotFoundException ex = new UserchoiceNotFoundException("Userchoice not found");

    // Act
    ResponseEntity<String> response = imageController.handleResourceNotFoundException(ex);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
