package io.camunda.demo.pick_animal.service;

import org.apache.commons.codec.binary.Base64;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    private ImageService imageService;

    private final String TEST_IMAGE_URL = "https://placebear.com/cache/145-300.jpg";
    private final String TEST_IMAGE_FORMAT = TEST_IMAGE_URL.substring(TEST_IMAGE_URL.lastIndexOf('.') + 1);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageService();
    }

    @Test
    void testDownloadImageAndConvertToBase64_Success() throws IOException {
        // Arrange
        BufferedImage bufferedImage = ImageIO.read(new URL(TEST_IMAGE_URL));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, TEST_IMAGE_FORMAT, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.encodeBase64String(imageBytes);

        // Mock static methods
        URL url = mock(URL.class);
        when(url.openStream()).thenReturn(new ByteArrayInputStream(imageBytes));
        ImageIO.setUseCache(false);

        // Act
        Pair<String, String> result = imageService.downloadImageAndConvertToBase64(TEST_IMAGE_URL);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_IMAGE_FORMAT, result.getValue0());
        assertEquals(base64Image, result.getValue1());
    }

    // @Test
    void testDownloadImageAndConvertToBase64_Failure() throws IOException {
        // Arrange
        String errorMessage = "Error while downloading from: " + TEST_IMAGE_URL;

        BufferedImage bufferedImage = ImageIO.read(new URL(TEST_IMAGE_URL));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, TEST_IMAGE_FORMAT, baos);

        // Mock static methods
        URL url = mock(URL.class);
        when(url.openStream()).thenThrow(new ImageDownloadException("Network error"));
        ImageIO.setUseCache(false);

        ImageDownloadException thrown = assertThrows(ImageDownloadException.class, () -> {
            imageService.downloadImageAndConvertToBase64(TEST_IMAGE_URL);
        });
        assertEquals(errorMessage, thrown.getMessage());
    }
}
