package com.luv2code.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.luv2code.productservice.client")
public class ProductServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

}
