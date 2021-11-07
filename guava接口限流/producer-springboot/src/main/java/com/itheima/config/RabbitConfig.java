package com.itheima.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    //配置交换机  队列    绑定关系
    public static final String order_Q = "testQ"; //1

    //4.Queue 队列
    @Bean("orderQueue")
    public Queue orderQueue(){
        return QueueBuilder.durable(order_Q).build();
    }

}
