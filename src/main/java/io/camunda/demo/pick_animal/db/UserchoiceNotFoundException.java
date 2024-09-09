package io.camunda.demo.pick_animal.db;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserchoiceNotFoundException extends RuntimeException {
    public UserchoiceNotFoundException(String id) {
        super("User Choice with id:" + id + " not found!");
    }
}