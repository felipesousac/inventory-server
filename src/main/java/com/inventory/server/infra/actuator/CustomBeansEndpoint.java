package com.inventory.server.infra.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

    /*
        Demo purpose of how a custom actuator endpoint can be made
     */

@Component
@Endpoint(id = "custom-beans")
public class CustomBeansEndpoint {

    private final ApplicationContext applicationContext;

    public CustomBeansEndpoint(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @ReadOperation
    public Map<String, Integer> beanCount() {
        Map<String, Integer> beanCount = new HashMap<>();
        beanCount.put("beanCount", this.applicationContext.getBeanDefinitionCount());

        return beanCount;
    }
}
