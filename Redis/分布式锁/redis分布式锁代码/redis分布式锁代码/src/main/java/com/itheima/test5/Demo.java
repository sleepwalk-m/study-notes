package com.itheima.test5;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

public class Demo {

    public static void main(String[] args) {
        RedissonClient redisson = RedissonManager.getRedisson();
        System.out.println(redisson);

    }
}
