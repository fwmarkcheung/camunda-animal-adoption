package io.camunda.demo.pick_animal.model;

import java.util.Date;

public record Userchoice(String id, String username, Date creationdate, String animal, String imageURL,
                String imageType, String imageBase64String) {
}
