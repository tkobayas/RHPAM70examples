package org.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class HelloWorld {

    // curl http://localhost:8080/helloworld-rs-pojo-1.0.0/json
    @GET
    @Path("/json")
    @Produces({ "application/json" })
    public Person getPersonJSON() {
        Person p = new Person("John", 20);
        return p;
    }

    // curl http://localhost:8080/helloworld-rs-pojo-1.0.0/xml
    @GET
    @Path("/xml")
    @Produces({ "application/xml" })
    public Person getPersonXML() {
        Person p = new Person("John", 20);
        return p;
    }
}
