package io.camunda.demo.pick_animal.service;

import java.io.IOException;

public class ImageDownloadException extends IOException {
    public ImageDownloadException(String msg, IOException e) {
        super(msg, e);
    }

    public ImageDownloadException(String msg) {
        super(msg);
    }

}