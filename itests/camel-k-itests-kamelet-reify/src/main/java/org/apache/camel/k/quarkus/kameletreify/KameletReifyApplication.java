/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.k.quarkus.kameletreify;

import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.arc.Unremovable;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Endpoint;
import org.apache.camel.FluentProducerTemplate;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/test")
@ApplicationScoped
public class KameletReifyApplication {
    @Inject
    CamelContext context;
    @Inject
    FluentProducerTemplate producerTemplate;
    @Inject
    ConsumerTemplate consumerTemplate;

    @GET
    @Path("/inspect")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject inspect() {
        var components = context.getComponentNames();
        var endpoints = context.getEndpoints().stream().map(Endpoint::getEndpointUri).collect(Collectors.toList());

        return Json.createObjectBuilder()
            .add("components", Json.createArrayBuilder(components))
            .add("endpoints", Json.createArrayBuilder(endpoints))
            .build();
    }
    @POST
    @Path("/request")
    @Consumes(MediaType.TEXT_PLAIN)
    public void request(String payload) {
        producerTemplate.to("direct:request").withBody(payload).asyncRequest();
    }

    @GET
    @Path("/receive")
    @Produces(MediaType.TEXT_PLAIN)
    public String receive() {
        return consumerTemplate.receiveBody("direct:response", 5000, String.class);
    }

    @Unremovable
    @Named("amqcf")
    public ConnectionFactory activeMQConnectionFactory(@ConfigProperty(name = "amqBrokerUrl") String brokerUrl) {
        return new ActiveMQConnectionFactory(brokerUrl);
    }
}
