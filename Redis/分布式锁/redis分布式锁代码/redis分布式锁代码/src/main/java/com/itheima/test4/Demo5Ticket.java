package com.itheima.test4;


import com.itheima.test5.RedissonManager;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import sun.security.krb5.internal.Ticket;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demo5Ticket {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();
        Thread t1 = new Thread(ticket, "窗口1"); //三个进程
        Thread t2 = new Thread(ticket, "窗口2");//三个进程
        Thread t3 = new Thread(ticket, "窗口3");//三个进程

        t1.start();
        t2.start();
        t3.start();
    }

    /**
     * 对票进行售卖   --- 线程安全问题    --- 超卖的问题
     * 如何解决多线程请求下的线程安全问题呢？
     * 加锁  syn  lock
     * 1.存在第一个问题：如果值进行获得锁，释放锁，如果一旦某一次锁没有释放，那么就出现死锁问题？
     *      解决方案：设置锁的ttl 过期时间
     *
     * 2.存在第二个问题： 如果服务器获取了锁。但是服务B 释放了锁。 因为没有对锁进行判断，所以导致服务
     *      B把服务A的锁给释放
     *      解决方案：为当前锁设置一个uuid的为一只，当释放锁的时候，需要判断当前值是否相等，如果相等，释放，如果不想等，不是同一把锁
     * 3.存在第三个问题：如何设置了ttl 但是业务执行时间大于ttl 怎么办呢？ 也导致超卖
     *      * 实现步骤：1.设置业务的执行时间>3S
     *      *          2.设置锁的ttl 2S
     *      *          3.需要进行锁的续期  每秒查询下当前锁的ttl .如果>0  那么就为当前锁续命1秒
     * 4. 存在第四个问题： 原子性操作问题
     *      String set = jedis.set(lock_key, id, params);//需要执行两个执行 nx ex
     *      解决获取锁的原子性
     *
     * 基于redis 分布式锁。解决redis 击穿（热门数据过期，很多用户同时访问，导致走数据库） ，
     * 穿透 （用户而已访问没有数据）， 雪崩的业务场景
     *
     * 5.考虑   redis 高可用   搭建集群 redis-cluster 集群
     *
     *
     * 6.使用redisson解决分布式锁
     */
    static class Ticket implements Runnable {
        private static RedissonClient redisson = RedissonManager.getRedisson();
        private int ticket = 10;
        public  void run() {
            String name = Thread.currentThread().getName();

            while (true) {
                //在业务代码执行之前，进行加锁。
                RLock lock = redisson.getLock("aa");
                boolean b = lock.tryLock();
                //lock.tryLock();//获得锁的
                if(b){
                    if (ticket > 0) {
                        System.out.println(name + "卖票：" + ticket);
                        try {
                            Thread.sleep(300); //大于锁的时长
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ticket--;
                    }
                    lock.unlock();
                    if (ticket <= 0) {
                        break;
                    }
                    //在业务代码执行之后，需要进行解锁
                }
            }
        }
    }

}