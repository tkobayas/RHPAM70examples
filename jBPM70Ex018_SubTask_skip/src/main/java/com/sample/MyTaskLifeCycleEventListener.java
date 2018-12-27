package com.sample;

import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTaskLifeCycleEventListener implements TaskLifeCycleEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MyTaskLifeCycleEventListener.class);

    private RuntimeManager runtimeManager;
    
    public MyTaskLifeCycleEventListener(RuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }
    
    public void afterTaskActivatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskAddedEvent(TaskEvent arg0) {
        Task task = arg0.getTask();
        long taskId = task.getId();
        
        System.out.println("MyTaskLifeCycleEventListener : event = " + arg0);
        System.out.println("MyTaskLifeCycleEventListener : taskId = " + taskId);
        
        if (task.getName().equals("ParentTask")) {
            // Store parentTaskId for later use (give it to SubTasks)
            long processInstanceId = task.getTaskData().getProcessInstanceId();
            RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
            KieSession kieSession = runtimeEngine.getKieSession();
            ProcessInstance processInstance = kieSession.getProcessInstance(processInstanceId);
            System.out.println("   setting parentTaskId = " + taskId);
            ((WorkflowProcessInstance)processInstance).setVariable("parentTaskId", taskId);
            
            // set SubTaskStrategy. You may add logic to change the strategy based on input parameter etc. (e.g. parameter "SubTasksStrategy")
            ((InternalTask)task).setSubTaskStrategy(SubTasksStrategy.SkipAllSubTasksOnParentSkip);
        }
    }

    public void afterTaskClaimedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskCompletedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskDelegatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskExitedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskFailedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskForwardedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskNominatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskReleasedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskResumedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskSkippedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskStartedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskStoppedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void afterTaskSuspendedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskActivatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskAddedEvent(TaskEvent event) {
    }

    public void beforeTaskClaimedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskCompletedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskDelegatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskExitedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskFailedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskForwardedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskNominatedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskReleasedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskResumedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskSkippedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskStartedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskStoppedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTaskSuspendedEvent(TaskEvent arg0) {
        // TODO Auto-generated method stub

    }

}
