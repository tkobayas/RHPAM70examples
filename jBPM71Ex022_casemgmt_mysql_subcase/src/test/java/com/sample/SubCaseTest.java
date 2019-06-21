package com.sample;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.impl.model.instance.CaseInstanceImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.LoggerFactory;

/**
 * This is a sample file to launch a process.
 */
public class SubCaseTest extends AbstractCaseServicesBaseTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SubCaseTest.class);

    protected static final String BASIC_SUB_CASE_P_ID = "CaseWithSubCase";
    protected static final String SUB_CASE_ID = "SUB-0000000001";
    protected static final String PROCESS_TO_CASE_P_ID = "process2case";
    
    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("cases/CaseWithSubCase.bpmn2");
        processes.add("cases/UserTaskCase.bpmn2");
//        processes.add("cases/EmptyCase.bpmn2");
        // add processes that can be used by cases but are not cases themselves
//        processes.add("processes/DataVerificationProcess.bpmn2");
//        processes.add("processes/Process2Case.bpmn2");
        return processes;
    }
    
    @Test
    public void testStartCaseWithSubCaseDestroySubCase() {
        
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("manager", new UserImpl("mary"));
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), BASIC_SUB_CASE_P_ID, data, roleAssignments);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), BASIC_SUB_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(SUB_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            caseService.triggerAdHocFragment(caseId, "Sub Case", null);
            
            Collection<CaseInstance> caseInstances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(2, caseInstances.size());
            
            Map<String, CaseInstance> byCaseId = caseInstances.stream().collect(toMap(CaseInstance::getCaseId, c -> c));
            assertTrue(byCaseId.containsKey(SUB_CASE_ID));
            assertTrue(byCaseId.containsKey(HR_CASE_ID));
            
//            CaseInstanceImpl parentCaseInstance = (CaseInstanceImpl)caseService.getCaseInstance(SUB_CASE_ID);
//            CaseFileInstance parentCaseInstanceFile = caseService.getCaseFileInstance(SUB_CASE_ID);
//            
//            Long processInstanceId = parentCaseInstance.getProcessInstanceId();
//            List<WorkItem> workItems = processService.getWorkItemByProcessInstance(processInstanceId);
//            List<String> childCaseIds = workItems.stream().filter(w -> w.getParameter("CaseId") != null ).map(w -> (String)w.getParameter("CaseId")).collect(toList());
//            System.out.println("childCaseIds = " + childCaseIds);
//
//            CaseInstance childCaseInstance = caseService.getCaseInstance(HR_CASE_ID);
//            CaseFileInstance childCaseInstanceFile = caseService.getCaseFileInstance(HR_CASE_ID);
            
            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(HR_CASE_ID, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertEquals("Hello1", tasks.get(0).getName());
            
            CaseFileInstance mainCaseFile = caseService.getCaseFileInstance(caseId);
            assertNotNull(mainCaseFile);
            assertNull(mainCaseFile.getData("subCaseId"));
            
            caseService.destroyCase(HR_CASE_ID);
            
            mainCaseFile = caseService.getCaseFileInstance(caseId);
            assertNotNull(mainCaseFile);
            assertEquals(HR_CASE_ID, mainCaseFile.getData("subCaseId"));
            assertEquals("John Doe", mainCaseFile.getData("outcome"));
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }

}