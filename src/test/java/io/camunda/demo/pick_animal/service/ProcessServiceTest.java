package io.camunda.demo.pick_animal.service;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivateJobsResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.junit.jupiter.api.Test;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.assertions.ProcessInstanceAssert;
import io.camunda.zeebe.process.test.extensions.ZeebeProcessTest;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import io.camunda.zeebe.process.test.testengine.RecordStreamSource;

//Creates and starts a new in-memory engine for each test case
@ZeebeProcessTest
public class ProcessServiceTest {

    // injected by ZeebeProcessTest annotation
    private InMemoryEngine engine;
    // injected by ZeebeProcessTest annotation
    private ZeebeClient client;
    // injected by ZeebeProcessTest annotation
    private RecordStreamSource recordStreamSource;

    // Embedded the form to the process definition for testing purpose
    private final String BPMN_PROCESS_FILE = "test-pick-an-animal.bpmn";
    private final String BPMN_PROCESS_ID = "animalPickingProcessId";
    // private final String BPMN_PROCESS_FORM = "decide-an-animal.form";

    @Test
    public void testDeploymentProcess() {
        // When
        DeploymentEvent deploymentEvent = initDeployment();

        // Then
        BpmnAssert.assertThat(deploymentEvent);
    }

    private DeploymentEvent initDeployment() {
        return client.newDeployCommand()
                .addResourceFromClasspath(BPMN_PROCESS_FILE)
                .send()
                .join();
    }

    private ProcessInstanceAssert initProcessInstanceStart() {
        ProcessInstanceEvent event = client.newCreateInstanceCommand()
                .bpmnProcessId(BPMN_PROCESS_ID)
                .latestVersion()
                .send()
                .join();
        return BpmnAssert.assertThat(event);
    }

}