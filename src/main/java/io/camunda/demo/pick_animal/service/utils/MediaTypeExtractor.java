package io.camunda.demo.pick_animal.service.utils;

import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class MediaTypeExtractor {

    // Map of file extensions to MediaType
    private static final Map<String, MediaType> mediaTypeMap = new HashMap<>();

    static {
        mediaTypeMap.put("jpg", MediaType.IMAGE_JPEG);
        mediaTypeMap.put("jpeg", MediaType.IMAGE_JPEG);
        mediaTypeMap.put("png", MediaType.IMAGE_PNG);
        mediaTypeMap.put("gif", MediaType.IMAGE_GIF);
        mediaTypeMap.put("bmp", MediaType.parseMediaType("image/bmp"));
        mediaTypeMap.put("webp", MediaType.parseMediaType("image/webp"));
    }

    // Method to extract MediaType from URL string
    public static MediaType extractMediaTypeFromUrl(String url) {
        // Extract the file extension from the URL
        String fileExtension = getFileExtension(url);

        // Return the matching MediaType, or default to application/octet-stream
        return mediaTypeMap.getOrDefault(fileExtension.toLowerCase(), MediaType.APPLICATION_OCTET_STREAM);
    }

    // Helper method to get file extension from URL string
    private static String getFileExtension(String url) {
        if (url != null && url.contains(".")) {
            return url.substring(url.lastIndexOf('.') + 1);
        }
        return "";
    }

    public static void main(String[] args) {
        // Test the method
        String imageUrl = "https://example.com/image.jpg";
        MediaType mediaType = extractMediaTypeFromUrl(imageUrl);
        System.out.println("Media Type: " + mediaType);
    }
}
