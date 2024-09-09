package io.camunda.demo.pick_animal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;

/*
 * Deploy a process using the model:pick-an-animal.bpmn 
 * from the resources directory
 */
@SpringBootApplication
@Deployment(resources = "classpath:pick-an-animal.bpmn")
public class PickAnimalApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(PickAnimalApplication.class);

    @Autowired
    private ZeebeClient zeebeClient;

    public static void main(String[] args) {
        SpringApplication.run(PickAnimalApplication.class, args);
    }

    /*
     * Starts a new process instance for the process model: animalPickingProcessId
     * using the latest version
     */
    @Override
    public void run(final String... args) {
        var bpmnProcessId = "animalPickingProcessId";
        var event = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(bpmnProcessId)
                .latestVersion()
                .send()
                .join();
        LOG.info("started a process instance: {}", event.getProcessInstanceKey());
    }
}