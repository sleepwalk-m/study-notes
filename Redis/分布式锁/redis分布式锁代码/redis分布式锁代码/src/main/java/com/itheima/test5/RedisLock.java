package com.itheima.test5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

public class RedisLock { //基于redis 单体架构
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private String lock_key = "redis_lock"; //锁键

    protected long internalLockLeaseTime = 30000;//锁过期时间

    private long timeout = 999999; //获取锁的超时时间

    //SET命令的参数  Jedis3.0 引入的一个对象
    SetParams params = SetParams.setParams().nx().px(internalLockLeaseTime);

    JedisPool jedisPool = new JedisPool();
    /**
     * 有没有用过分布式锁呀？
     * 有用过，之前项目中就使用redis实现过分布式锁，使用redis的setnx 创建锁，使用redis的setex设置锁的ttl.
     * 为什么要设置ttl呀？因为补设置可能会导致死锁，那么这样我再固定时间后可以自动释放锁。
     * 为了保证 setnx  setex 操作的原子性，所以使用 SetParams 来执行进行创建锁已经同时ttl构建
     * 如果没有获得到锁的对象，会是cas （等比交换）进行等待然后重新获得锁
     * 释放锁，
     * 直接删除 setnx 锁对象，但是这样会导致，用户A把用户B的锁给释放了，所以，在创建锁的时候，我们会构建一个全局唯一的id
     * 存储在锁的value
     * 在释放锁的实现，先判断当前值是否相当，如果相等，进行锁的释放，如果不相等，代表用户不是这把锁
     * 但是这样需要保证删除锁和查询锁ID需要保证原子性。
     * 所以引入的lua脚本，因为lua脚本具有原子特性，那么可以是lua在进行当前的锁ID查询并且同时删除锁。保证其原子性。
     * 从而实现锁的释放。
     *
     * 加锁
     * @param id
     * @return
     */
    public boolean lock(String id){
        Jedis jedis = jedisPool.getResource();
        Long start = System.currentTimeMillis();
        try{
            for(;;){//cas
                //SET命令返回OK ，则证明获取锁成功
                String lock = jedis.set(lock_key, id, params);
                if("OK".equals(lock)){
                    return true;
                }
                //否则循环等待，在timeout时间内仍未获取到锁，则获取失败
                long l = System.currentTimeMillis() - start;
                if (l>=timeout) {
                    return false;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            jedis.close();
        }
    }
    /**
     * 解锁
     * @param id
     * @return
     */
    public boolean unlock(String id){
        Jedis jedis = jedisPool.getResource();
        String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                        "   return redis.call('del',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";
        try {
            Object result = jedis.eval(script, Collections.singletonList(lock_key),
                    Collections.singletonList(id));
            if("1".equals(result.toString())){
                return true;
            }
            return false;
        }finally {
            jedis.close();
        }
    }
}