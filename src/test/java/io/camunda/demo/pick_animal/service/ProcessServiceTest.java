package io.camunda.demo.pick_animal.service;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extensions.ZeebeProcessTest;

//Creates and starts a new in-memory engine for each test case
@ZeebeProcessTest
public class ProcessServiceTest {

    // @Mock
    private ZeebeClient zeebeClient;

    // @Mock
    // private ProcessInstanceEvent expectedProcessInstanceEvent;

    private ProcessService processService;

    private final String BPMN_PROCESS_FILE = "pick-an-animal.bpmn";
    private final String BPMN_PROCESS_ID = "animalPickingProcessId";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processService = new ProcessService(zeebeClient);

        // The embedded engine is completely reset before each test run.
        // Therefore, we need to deploy the process each time
        final DeploymentEvent deploymentEvent = deploymentProcess();
        BpmnAssert.assertThat(deploymentEvent)
                .containsProcessesByResourceName(BPMN_PROCESS_FILE);
    }

    @SuppressWarnings("deprecation")
    private DeploymentEvent deploymentProcess() {
        return zeebeClient.newDeployCommand()
                .addResourceFromClasspath(BPMN_PROCESS_FILE)
                .send()
                .join();
    }

    @Test
    public void testProcessInstanceStart() {

        // When
        // Expect a process event when the process is started

        ProcessInstanceEvent expectedProcessInstanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(BPMN_PROCESS_ID)
                .latestVersion()
                .send()
                .join();

        assertNotNull(expectedProcessInstanceEvent);

        // Test the start process method in the ProcessService
        ProcessInstanceEvent actualInstanceEvent = processService.startProcess();

        // Verify the interactions and the result
        assertNotNull(actualInstanceEvent);
        assertNotNull(actualInstanceEvent.getProcessInstanceKey());
        assertEquals(expectedProcessInstanceEvent.getBpmnProcessId(), actualInstanceEvent.getBpmnProcessId());

    }

    /*
     * @Test
     * public void testDeploymentProcess() {
     * // When
     * DeploymentEvent deploymentEvent = initDeployment();
     * 
     * // Then
     * BpmnAssert.assertThat(deploymentEvent);
     * }
     * 
     * private DeploymentEvent initDeployment() {
     * return client.newDeployCommand()
     * .addResourceFromClasspath(BPMN_PROCESS_FILE)
     * .send()
     * .join();
     * }
     * 
     * private ProcessInstanceAssert initProcessInstanceStart() {
     * ProcessInstanceEvent event = ;
     * return BpmnAssert.assertThat(event);
     * }
     */
}