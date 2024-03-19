package com.jerry.springcloudalibaba.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jerry
 * @Date 2024/3/19 09:23
 */
@RestController
public class HelloWrodController {
    @Value("${spring.cloud.sayhello}")
    private String sayhello;


    @GetMapping
    public String getSayhello() {
        return sayhello;
    }
}
