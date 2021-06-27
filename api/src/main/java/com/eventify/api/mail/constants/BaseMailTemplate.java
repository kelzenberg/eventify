package com.eventify.api.mail.constants;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class BaseMailTemplate {
    @Value("${public.name}")
    private String publicName;

    private static String PUBLIC_NAME_STATIC;

    @Value("${public.name}")
    public void setPublicNameStatic(String name){
        BaseMailTemplate.PUBLIC_NAME_STATIC = name;
    }

    @Value("${public.port}")
    private Integer publicPort;

    private static int PUBLIC_PORT_STATIC;

    @Value("${public.port}")
    public void setPublicPortStatic(Integer port){
        BaseMailTemplate.PUBLIC_PORT_STATIC = port;
    }

    @Value("${public.url}")
    private String publicURL;

    private static String PUBLIC_URL_STATIC;

    @Value("${public.url}")
    public void setPublicURLStatic(String name){
        BaseMailTemplate.PUBLIC_URL_STATIC = name;
    }

    static final String SENDER_MAIL_ADDRESS = "noreply@eventify.com";
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy, HH:mm");

    static String getBaseTemplate() {
        return "<html><body><h1>" + PUBLIC_NAME_STATIC + "</h1><p>%s</p></body></html>";
    }

    static String getBaseURL() {
        return PUBLIC_URL_STATIC + ":" + PUBLIC_PORT_STATIC;
    }
}
