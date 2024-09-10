package io.camunda.demo.pick_animal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;

@Service
// Deploy the process model
@Deployment(resources = "classpath:pick-an-animal.bpmn")
public class ProcessService {

    @Autowired
    private ZeebeClient zeebeClient;

    private static final Logger LOG = LoggerFactory.getLogger(ProcessService.class);

    // Start a process instance
    public void startProcess() {
        var bpmnProcessId = "animalPickingProcessId";
        var event = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(bpmnProcessId)
                .latestVersion()
                .send()
                .join();
        LOG.info("started a process instance: {}", event.getProcessInstanceKey());

    }
}
