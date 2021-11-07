package com.itheima.test5;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonManager {
    private static Config config = new Config();
    //声明redisso对象
    private static RedissonClient redisson = null;
    //实例化redisson
    static{
     /*config.useClusterServers().setScanInterval(2000).addNodeAddress("redis://192.168.200.136:7002","redis://192.168.200.136:7003")
             .addNodeAddress("redis://192.168.200.136:7001");*/
     config.useSingleServer().setAddress("redis://127.0.0.1:6379");
    //得到redisson对象
    redisson = Redisson.create(config);
    }
    //获取redisson对象的方法
    public static RedissonClient getRedisson(){
        return redisson;
    }
}