package com.inventory.server.infra.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class UsableMemoryHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        File path = new File(".");
        long diskUsableInBytes = path.getUsableSpace();
        boolean isHealthy = diskUsableInBytes >= 10 * 1024 * 1024;
        Status status = isHealthy ? Status.UP : Status.DOWN;

        return Health
                .status(status)
                .withDetail("usable memory", diskUsableInBytes)
                .withDetail("threshold", 10 * 1024 * 1024)
                .build();
    }
}
