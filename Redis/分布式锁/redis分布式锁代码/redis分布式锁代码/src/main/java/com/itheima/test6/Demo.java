package com.itheima.test6;

import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;

public class Demo {
    private static CountDownLatch countDownLatch = new CountDownLatch(10);
    public static void main(String[] args) throws InterruptedException {
        TestJC jc = new TestJC();
        for(int i = 0;i<10;i++){
            Thread thread = new Thread(jc,"窗口"+i);
            thread.start();
            countDownLatch.countDown();
        }
        Thread.currentThread().join();
    }
    public static class TestJC implements Runnable{
        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RestTemplate restTemplate =new RestTemplate();
            String s = restTemplate.getForObject("http://localhost:8080/test/1", String.class);
            System.out.println(s);
        }
    }

}


