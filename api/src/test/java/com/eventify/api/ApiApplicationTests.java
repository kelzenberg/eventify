package com.eventify.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ApplicationSecurityTestConfig.class
)
class ApiApplicationTests {

    @Autowired
    Environment env;

    @BeforeEach
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
