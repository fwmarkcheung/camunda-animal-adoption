package io.camunda.demo.pick_animal;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.camunda.demo.pick_animal.service.PickAnimalService;

@SpringBootTest
class PickAnimalApplicationTests {

	@Autowired
	PickAnimalService pickAnimalWorker;

	// @Test
	public void contextLoads() {
		Assertions.assertThat(pickAnimalWorker).isNot(null);
	}

}
