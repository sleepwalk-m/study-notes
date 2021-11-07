package com.itheima.consumerspringboot;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbimtMQListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    //MQ监听器

    /**
     * ack 确认机制
     * 1.把原有的自动提交改为手动提交  acknowledge-mode: manual      500    250   1~300
     * 2.修改原有的实现接口
     * @param message
     */
    @RabbitListener(queues = "testQ")
    public void ListenerQueue(Message message,Channel channel){//消息的重复消费

        byte[] body = message.getBody();
        String s = new String(body);
        //设置一个标识 具有原子性的
        Long increment = redisTemplate.boundValueOps(s).increment();
        if(increment > 1){//代表当前消息再重复消费
            //直接签收当前消息
            try {
                channel.basicAck(1l,true);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        redisTemplate.boundValueOps("goods").increment();
        //模拟代码吧当前数据存入到数据库   库存+ num

           /* System.out.println("确定签收");
            channel.basicAck(deliveryTag,true);

            channel.basicNack(deliveryTag,true,true);*/


        try {
            int i = 1/0;
            channel.basicNack(1,true,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
