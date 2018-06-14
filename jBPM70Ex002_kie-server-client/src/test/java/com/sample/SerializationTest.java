package com.sample;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import org.kie.server.client.ProcessServicesClient;

public class SerializationTest extends TestCase {

    public void testRest() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        ProcessServicesClient processClient = KieServerRestUtils.getProcessServicesClient();
        
        FileOutputStream fileOut = new FileOutputStream("sample.dat");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(processClient);
        out.close();
        fileOut.close();
    }
}