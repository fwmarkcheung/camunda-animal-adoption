package io.camunda.demo.pick_animal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;
import org.javatuples.Pair;

@Service
public class ImageService {

    private final static Logger LOG = LoggerFactory.getLogger(ImageService.class);

    /*
     * Downt the image from the url and convert it to base64 string
     */
    public Pair<String, String> downloadImageAndConvertToBase64(String imageUrl) throws ImageDownloadException {
        LOG.info("Downloading image from URL: {}", imageUrl);

        try {
            // Create a URL object for the given image URL
            URL url = new URL(imageUrl);

            // Read the image from the URL
            BufferedImage image = ImageIO.read(url);

            // Extract the file extension (e.g., jpg, png, etc.)
            String formatName = imageUrl.substring(imageUrl.lastIndexOf('.') + 1);
            // Convert the image to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, baos);
            byte[] imageBytes = baos.toByteArray();

            return new Pair<String, String>(formatName, Base64.encodeBase64String(imageBytes));
        } catch (IOException e) {
            StringBuilder msg = new StringBuilder("Error while downloading from:");
            msg.append(imageUrl);
            LOG.error(msg.toString(), e);
            throw new ImageDownloadException(msg.toString(), e);
        }
    }

}
