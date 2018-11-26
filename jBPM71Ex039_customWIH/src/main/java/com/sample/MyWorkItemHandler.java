package com.sample;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyWorkItemHandler implements WorkItemHandler {

    private static Logger logger = LoggerFactory.getLogger(MyWorkItemHandler.class);

    public MyWorkItemHandler() {
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        logger.info("enter executeWorkItem()");

        //        throw new RuntimeException("XXX");

        manager.completeWorkItem(workItem.getId(), null);

        return;

    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        manager.abortWorkItem(workItem.getId());
    }

}
