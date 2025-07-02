package com.openecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Open E-Commerce System Main Application
 * 
 * @author OpenECommerce Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@EnableFeignClients
public class OpenECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenECommerceApplication.class, args);
    }
}