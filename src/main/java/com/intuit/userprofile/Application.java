package com.intuit.userprofile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class Application {
    public static void main( String[] args ) {
        System.out.println("Starting the application...!!!");
        SpringApplication.run(Application.class, args);
    }
}