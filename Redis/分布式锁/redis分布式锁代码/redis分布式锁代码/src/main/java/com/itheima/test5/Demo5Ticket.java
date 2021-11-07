package com.itheima.test5;


import com.itheima.test4.RedisLockTest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.UUID;

public class Demo5Ticket {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();
        Thread t1 = new Thread(ticket, "窗口1");
        Thread t2 = new Thread(ticket, "窗口2");
        Thread t3 = new Thread(ticket, "窗口3");

        t1.start();
        t2.start();
        t3.start();
    }

    static class Ticket implements Runnable {


        private int ticket = 10;
        private static RedissonClient redisson = RedissonManager.getRedisson();

        public void run() {
            String name = Thread.currentThread().getName();
            while (true) {
                //获得锁
                RLock lock = redisson.getLock("aa");
                boolean b = lock.tryLock();
                if (b) {
                    try {
                        if (ticket > 0) {
                            System.out.println(name + "卖票：" + ticket);
                            try {
                                Thread.sleep(200); //大于锁的时长
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ticket--;
                        }
                        if (ticket <= 0) {
                            break;
                        }

                    } finally {
                        //释放锁
                        lock.unlock();
                    }
                }
            }
        }
    }
}