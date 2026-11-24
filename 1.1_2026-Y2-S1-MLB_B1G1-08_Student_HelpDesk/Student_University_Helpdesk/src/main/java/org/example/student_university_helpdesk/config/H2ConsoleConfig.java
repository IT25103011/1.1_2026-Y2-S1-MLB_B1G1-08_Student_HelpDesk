package org.example.student_university_helpdesk.config;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2ConsoleConfig {

    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2ConsoleServletRegistration() {
        ServletRegistrationBean<JakartaWebServlet> registrationNode =
                new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");
        registrationNode.setName("H2ConsoleServlet");
        return registrationNode;
    }
}