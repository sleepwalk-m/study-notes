package com.itheima.test4;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

public class RedisLockTest {

    JedisPool jedisPool = new JedisPool();
    private String lock_key = "redis_lock"; //锁键
    protected long internalLockLeaseTime = 2000;//锁过期时间
    private long timeout = 999999; //获取锁的超时时间
    //SET命令的参数  Jedis3.0 引入的一个对象
    //nx() 代表setnx      px   设置生命时长 ttl
    SetParams params = SetParams.setParams().nx().px(internalLockLeaseTime);


    /**
     * 获得锁
     * @param
     * @return
     */
    public  boolean tryLock(String id){
        //1.执行setnx 如果返回值1 代表获得锁成功 如果返回为0
        Jedis jedis = jedisPool.getResource();
        try {
            for(;;){//代表死循环 CAS
                //再jedis 3.0 版本引入setparams 参数 可以同时设置多个指令 再执行nx 通知执行 ex
                //Long aa = jedis.setnx(lock_key, id);//lock_key   aa
                String set = jedis.set(lock_key, id, params);//需要执行两个执行 nx ex
                //如果方法执行成功，返回ok
                // System.out.println(set);
                if("OK".equals(set)){//代表获得锁成功
                    //如果获得锁成功后，需要设置过期时长
                    //jedis.expire(lock_key,2);
                    //需要再成功获得锁后，需要执行锁续期
                    Thread thread = new Thread(new LockTimeOut());
                    thread.start(); //执行锁续期
                    System.out.println("获得锁成功"+id);
                    return true;
                }
                //如果返回值为0 .代表不成功。不成功。循环等会再来取获得锁
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            jedis.close();
        }
    }

    /**
     * 释放锁
     * @param
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
