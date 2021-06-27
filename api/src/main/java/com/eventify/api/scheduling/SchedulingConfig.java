package com.eventify.api.scheduling;

import com.eventify.api.entities.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Autowired
    UserService userService;

    // "Every day at 10:00 am"
    @Scheduled(cron = "0 0 10 * * *", zone = "Europe/Berlin")
    public void remindUnverifiedUsers() {
        //
    }

    // "Every day at 00:01 am and 12:01 am"
    @Scheduled(cron = "0 1 0,12 * * *", zone = "Europe/Berlin")
    public void deleteUnverifiedUsers() {
        //
    }
}
