package io.camunda.demo.pick_animal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.demo.pick_animal.service.ProcessService;

/*
 * Deploy a process using the model:pick-an-animal.bpmn 
 * from the resources directory
 */
@SpringBootApplication
public class PickAnimalApplication implements CommandLineRunner {

    // private static final Logger LOG =
    // LoggerFactory.getLogger(PickAnimalApplication.class);

    @Autowired
    private ProcessService service;

    public static void main(String[] args) {
        SpringApplication.run(PickAnimalApplication.class, args);
    }

    /*
     * Starts a new process instance for the process model: animalPickingProcessId
     * using the latest version
     */
    @Override
    public void run(final String... args) {
        service.startProcess();
    }
}