package com.itheima.consumerspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumerSpringbootApplication {

    //1.开启手动确认
    //2.监听器手动确认

    public static void main(String[] args) {
        SpringApplication.run(ConsumerSpringbootApplication.class, args);
    }

}
