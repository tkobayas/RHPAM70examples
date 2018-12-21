package com.sample;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;

@Dependent
public class MySelectorDataProvider implements SelectorDataProvider {

    @Override
    public String getProviderName() {
        return this.getClass().getName();
    }

    @Override
    public SelectorData<String> getSelectorData(FormRenderingContext context) {
        Map forms = context.getAvailableForms();
        System.out.println("forms = " + forms);
        
        // This is an example of retrieving data from input data configured in UserTask's DataAssignments
//        MapModelRenderingContext mapModelRenderingContext = (MapModelRenderingContext)context;
//        Map<String, Object> modelMap = mapModelRenderingContext.getModel();
//        System.out.println("modelMap = " + modelMap);
//        
//        Map inputPerson = (Map)modelMap.get("inputPerson");
//        System.out.println("inputPerson = " + inputPerson);
//        
//        String name = (String)inputPerson.get("name");
//        Integer age = (Integer)inputPerson.get("age");
//        System.out.println("name = " + name + ", age = " + age);
        
        // Dummy data
        HashMap<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("realValue1", "label1");
        dataMap.put("realValue2", "label2");
        dataMap.put("realValue3", "label3");

        SelectorData<String> data = new SelectorData<String>(dataMap, "realValue1");
        return data;
    }
}
