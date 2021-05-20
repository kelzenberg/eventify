package com.eventify.api.mail;

//
//@Configuration
//@ConfigurationProperties(prefix = "spring.mail")
//@Getter
//@Setter
//public class MailConfig {
//
//    @Getter
//    @Setter
//    private static class SmtpAttributes {
//        int connectiontimeout;
//        int timeout;
//        int writetimeout;
//        boolean auth = false;
//        boolean starttls = false;
//    }
//
//    @Getter
//    @Setter
//    private static class MailAttributes {
//        SmtpAttributes smtp;
//    }
//
//    @Getter
//    @Setter
//    private static class MailProperties {
//        MailAttributes mail;
//    }
//
//    private String protocol;
//    private String host;
//    private int port;
//    private String username;
//    private String password;
//    private MailProperties properties;
//
//    @Bean
//    public JavaMailSender javaMailSender() throws MessagingException {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setProtocol(protocol);
//        mailSender.setHost(host);
//        mailSender.setPort(port);
//        mailSender.setUsername(username.length() > 0 ? username : null);
//        mailSender.setPassword(password.length() > 0 ? password : null);
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", protocol);
//        props.put("mail.smtp.auth", properties.mail.smtp.auth);
//        props.put("mail.smtp.starttls.enable", properties.mail.smtp.starttls);
//        props.put("mail.smtp.connectiontimeout", properties.mail.smtp.connectiontimeout);
//        props.put("mail.smtp.timeout", properties.mail.smtp.timeout);
//        props.put("mail.smtp.writetimeout", properties.mail.smtp.writetimeout);
//
//        props.put("mail.debug", "true");
//
//        mailSender.setJavaMailProperties(props);
//
//        System.out.println("[DEBUG] Mail properties:\n" + mailSender.getProtocol() + "\n" + mailSender.getHost() + "\n" + mailSender.getPort() + "\n" + mailSender.getJavaMailProperties());
//
//        mailSender.testConnection();
//        return mailSender;
//    }
//}
