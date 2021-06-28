package com.eventify.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class ApiApplicationTests {

    @Autowired
    Environment env;
    
    @Test
    void contextLoads(){
        assertNotNull(env);
    }

    @Test
    void activeProfileIsLocal() {
        List<String> environments = Arrays.asList(env.getActiveProfiles());
        assertEquals(environments.size(), 1);
        assertEquals(environments.get(0), "local");
    }

}
