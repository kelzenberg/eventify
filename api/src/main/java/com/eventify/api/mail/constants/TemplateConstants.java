package com.eventify.api.mail.constants;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TemplateConstants {
    @Value("${public.name}")
    private String publicName;
    public static String STATIC_PUBLIC_NAME;

    @Value("${public.name}")
    private void setPublicNameStatic(String name) {
        TemplateConstants.STATIC_PUBLIC_NAME = name;
    }

    @Value("${public.port}")
    private Integer publicPort;
    private static int STATIC_PUBLIC_PORT;

    @Value("${public.port}")
    private void setPublicPortStatic(Integer port) {
        TemplateConstants.STATIC_PUBLIC_PORT = port;
    }

    @Value("${public.url}")
    private String publicURL;
    private static String STATIC_PUBLIC_URL;

    @Value("${public.url}")
    private void setPublicURLStatic(String name) {
        TemplateConstants.STATIC_PUBLIC_URL = name;
    }

    public static final String SENDER_MAIL_ADDRESS = "noreply@eventify.com";
    public static final SimpleDateFormat HUMAN_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy, HH:mm");

    public static String getBaseTemplate() {
        return "<html><body><h1>" +
                STATIC_PUBLIC_NAME +
                "</h1><p>" +
                "%s" +
                "</p></body></html>";
    }

    public static String getFrontendURL() {
        return STATIC_PUBLIC_URL + ":" + STATIC_PUBLIC_PORT;
    }
}
