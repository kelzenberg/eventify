package com.eventify.api.scheduling;

import com.eventify.api.entities.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.mail.MessagingException;
import java.util.Date;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Autowired
    UserService userService;

    private static final String TIME_ZONE = "Europe/Berlin";

    // "Every day at 10:00 am"
    @Scheduled(cron = "0 0 10 * * *", zone = TIME_ZONE)
    public void remindUnverifiedUsers() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("SCHEDULER:REMIND_UNVERIFIED", null));

        try {
            System.out.printf("[DEBUG] Task Scheduler (REMIND_UNVERIFIED) executed at %s.%n", new Date());
        } catch (Exception e) {
            System.out.println("[DEBUG] Task Scheduler (REMIND_UNVERIFIED) failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // "Every day at 00:01 am and 12:01 am"
    @Scheduled(cron = "0 1 0,12 * * *", zone = TIME_ZONE)
    public void disableExpiredUsers() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("SCHEDULER:DISABLE_EXPIRED", null));

        try {
            System.out.printf("[DEBUG] Task Scheduler (DISABLE_EXPIRED) executed at %s.%n", new Date());
            userService.disableAllExpired();
        } catch (MessagingException e) {
            System.out.println("[DEBUG] Task Scheduler (DISABLE_EXPIRED) failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

//    // FOR TESTING ONLY: "Every minute"
//    @Scheduled(cron = "0 * * * * *", zone = TIME_ZONE)
//    public void test() {
//        SecurityContextHolder.getContext()
//                .setAuthentication(new UsernamePasswordAuthenticationToken("SCHEDULER:TEST", null));
//
//        try {
//            System.out.printf("[DEBUG] Task Scheduler (TEST) executed at %s.%n", new Date());
//            userService.disableAllExpired();
//        } catch (MessagingException e) {
//            System.out.println("[DEBUG] Task Scheduler (TEST) failed: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
