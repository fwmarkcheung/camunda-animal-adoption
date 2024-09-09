package io.camunda.demo.pick_animal.controller;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import io.camunda.demo.pick_animal.model.Userchoice;
import io.camunda.demo.pick_animal.service.PickAnimalService;
import org.apache.commons.io.IOUtils;

@RestController
public class PickAnimalControllerTest {

  private final static Logger LOG = LoggerFactory.getLogger(PickAnimalController.class);

  @Autowired
  private PickAnimalService work;

  @GetMapping("/userchoice/{id}")
  Optional<Userchoice> findUserchoiceById(@PathVariable final String id) {

    return work.findUserchoiceById(id);
  }

  @GetMapping(value = "/get-image-with-media-type", produces = IMAGE_JPEG_VALUE)
  public @ResponseBody byte[] getImageWithMediaType() throws IOException {
    InputStream in = getClass()
        .getResourceAsStream("/com/baeldung/produceimage/image.jpg");
    return IOUtils.toByteArray(in);
  }

}