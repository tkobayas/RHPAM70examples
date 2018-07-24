package org.jbpm.examples.util;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.service.ServiceRegistry;

public class ServiceUtils {

    public static ProcessService getProcessService() {
        ProcessService service = (ProcessService) ServiceRegistry.get().service(ServiceRegistry.PROCESS_SERVICE);
        return service;
    }

    public static UserTaskService getUserTaskService() {
        UserTaskService service = (UserTaskService) ServiceRegistry.get().service(ServiceRegistry.USER_TASK_SERVICE);
        return service;
    }

    public static RuntimeDataService getRuntimeDataService() {
        RuntimeDataService service = (RuntimeDataService) ServiceRegistry.get().service(ServiceRegistry.RUNTIME_DATA_SERVICE);
        return service;
    }
}
