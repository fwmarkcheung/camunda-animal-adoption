package io.camunda.demo.pick_animal.controller;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.demo.pick_animal.db.UserchoiceRepository;
import io.camunda.demo.pick_animal.service.utils.MediaTypeExtractor;

import java.util.Base64;

@RestController
public class ImageController {

    @Autowired
    private UserchoiceRepository userchoiceRepository;

    @GetMapping("/pet/{recordId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String recordId) {

        // Retrieve the Base64-encoded image data from HarperDB
        Pair<String, String> imageData = userchoiceRepository.getBase64ImageStrFromHarperDB(recordId);

        if (imageData.getValue1() != null) {
            // Decode the Base64-encoded image back to bytes
            byte[] imageBytes = Base64.getDecoder().decode(imageData.getValue1());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaTypeExtractor.extractMediaTypeFromUrl(imageData.getValue0()));

            // Return the image as a byte array
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            // Return 404 if the image is not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
