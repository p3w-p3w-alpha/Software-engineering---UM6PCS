package com.sams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application entry point for SAMS
 * Student Academic Management System - started this project back in march
 *
 * @author dev team
 * note: dont forget to check the database connection settings in application.properties
 */
@SpringBootApplication
public class SamsApplication {

    // main method - this is where evrything starts
    public static void main(String[] args) {
        // fire up the spring boot app
        // took me a while to figure out how spring boot works lol
        SpringApplication.run(SamsApplication.class, args);
    }
}
