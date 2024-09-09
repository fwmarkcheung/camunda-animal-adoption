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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageService();
    }

    @Test
    void testDownloadImageAndConvertToBase64_Success() throws IOException {
        // Arrange
        String imageUrl = "http://example.com/image.jpg";
        String formatName = "jpg";
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, formatName, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.encodeBase64String(imageBytes);

        // Mock static methods
        URL url = mock(URL.class);
        when(url.openStream()).thenReturn(new ByteArrayInputStream(imageBytes));
        ImageIO.setUseCache(false);

        // Act
        Pair<String, String> result = imageService.downloadImageAndConvertToBase64(imageUrl);

        // Assert
        assertNotNull(result);
        assertEquals(formatName, result.getValue0());
        assertEquals(base64Image, result.getValue1());
    }

    @Test
    void testDownloadImageAndConvertToBase64_Failure() throws IOException {
        // Arrange
        String imageUrl = "http://example.com/image.jpg";
        String errorMessage = "Error while downloading from: " + imageUrl;

        // Mock static methods to throw IOException
        URL url = mock(URL.class);
        when(url.openStream()).thenThrow(new IOException("Network error"));

        // Act & Assert
        ImageDownloadException thrown = assertThrows(ImageDownloadException.class, () -> {
            imageService.downloadImageAndConvertToBase64(imageUrl);
        });
        assertEquals(errorMessage, thrown.getMessage());
    }
}
