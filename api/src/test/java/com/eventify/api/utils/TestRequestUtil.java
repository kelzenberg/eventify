package com.eventify.api.utils;

import com.eventify.api.utils.TestEntityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class TestRequestUtil {

    @Autowired
    private TestEntityUtil testEntityUtil;

    String token = "";

    @BeforeEach
    void setUp() {
        token = "Bearer " + testEntityUtil.createTestToken();
    }

    @AfterEach
    void tearDown() {
        token = "";
    }

    public MockHttpServletRequestBuilder getRequest(String url) {
        return requestBuilder(HttpMethod.GET, url, Optional.empty());
    }

    public MockHttpServletRequestBuilder postRequest(String url, String content) {
        return requestBuilder(HttpMethod.POST, url, Optional.of(content));
    }

    public MockHttpServletRequestBuilder putRequest(String url, String content) {
        return requestBuilder(HttpMethod.PUT, url, Optional.of(content));
    }

    public MockHttpServletRequestBuilder deleteRequest(String url) {
        return requestBuilder(HttpMethod.DELETE, url, Optional.empty());
    }

    private MockHttpServletRequestBuilder requestBuilder(HttpMethod method, String url, Optional<String> content) {
        switch (method) {
            case GET:
                return get(url).secure(true).header(HttpHeaders.AUTHORIZATION, token);
            case POST:
                return post(url).secure(true).header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content.get());
            case PUT:
                return put(url).secure(true).header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content.get());
            case DELETE:
                return delete(url).secure(true).header(HttpHeaders.AUTHORIZATION, token);
            default:
                return null;
        }
    }
}
