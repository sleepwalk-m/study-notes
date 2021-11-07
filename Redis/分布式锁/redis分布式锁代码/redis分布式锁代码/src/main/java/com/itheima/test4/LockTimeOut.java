package com.itheima.test4;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 锁续期的代码
 */
public class LockTimeOut implements  Runnable{

    JedisPool jedisPool = new JedisPool();

    private String lock_key = "redis_lock"; //锁键
    @Override
    public void run() {
        Jedis jedis = jedisPool.getResource();
        //1.获得当前锁的ttl
        //2.如果ttl > 0 那么我就为锁续期
        try {
            while (true) { //模拟定时器 。没秒执行一次
                Long ttl = jedis.ttl(lock_key);
                if (ttl != null && ttl > 0) { //需要进行锁的续期1S
                    jedis.expire(lock_key, (int) (ttl + 1));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            jedis.close();
        }
    }
}
