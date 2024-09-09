package io.camunda.demo.pick_animal.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.camunda.demo.pick_animal.model.Userchoice;
import io.camunda.demo.pick_animal.service.PickAnimalService;

@RestController
public class PickAnimalController {

  @Autowired
  private PickAnimalService work;

  @GetMapping("/userchoice/{id}")
  Optional<Userchoice> findUserchoiceById(@PathVariable final String id) {

    return work.findUserchoiceById(id);
  }

}