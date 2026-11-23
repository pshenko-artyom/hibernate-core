package org.pshenko.hibernate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Scanner;

@Configuration
@ComponentScan("org.pshenko.hibernate")
@PropertySource("classpath:application.properties")
public class ApplicationConfiguration {
    @Bean
    public Scanner getScanner() {
        return new Scanner(System.in);
    }
}
