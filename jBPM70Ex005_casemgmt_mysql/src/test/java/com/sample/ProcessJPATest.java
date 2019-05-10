package com.sample;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.CaseStage;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.services.api.TaskNotFoundException;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Test;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.slf4j.LoggerFactory;

/**
 * This is a sample file to launch a process.
 */
public class ProcessJPATest extends AbstractCaseServicesBaseTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProcessJPATest.class);

    @Test
    public void testAddUserTaskToCaseWithStage() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            CaseDefinition caseDef = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID);
            assertNotNull(caseDef);
            assertEquals(1, caseDef.getCaseStages().size());
            assertEquals(deploymentUnit.getIdentifier(), caseDef.getDeploymentId());

            CaseStage stage = caseDef.getCaseStages().iterator().next();

            // add dynamic user task to empty case instance - first by case id
            Map<String, Object> parameters = new HashMap<>();
            caseService.addDynamicTaskToStage(FIRST_CASE_ID, stage.getId(), caseService.newHumanTaskSpec("First task", "test", "john", null, parameters));

            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());

            TaskSummary task = tasks.get(0);
            assertTask(task, "john", "First task", Status.Reserved);
            assertEquals("test", task.getDescription());

            // second task add by process instance id
            Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(1, caseProcessInstances.size());

            ProcessInstanceDesc casePI = caseProcessInstances.iterator().next();
            assertNotNull(casePI);
            assertEquals(FIRST_CASE_ID, casePI.getCorrelationKey());

            caseService.addDynamicTaskToStage(casePI.getId(), stage.getId(), caseService.newHumanTaskSpec("Second task", "another test", "mary", null, parameters));
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            task = tasks.get(0);
            assertTask(task, "mary", "Second task", Status.Reserved);
            assertEquals("another test", task.getDescription());
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }

    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("UserTaskCase.bpmn2");
        processes.add("UserTaskWithStageCase.bpmn2");
        return processes;
    }
}