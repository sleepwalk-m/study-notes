# 一个点菜小程序
#### 效果图：
![image](https://user-images.githubusercontent.com/74847491/129535201-e89a211e-3735-4a85-9a3e-25da4c875de9.png)
#### pojo
~~~java
package com.mask.day01;

/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/8/16 15:56
 * @Description: 菜品的对象
 */
public class Dish {

    private Integer id;
    private String name;
    private double price;

    public Dish(Integer id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Dish() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return
                id +"\t\t\t" + name + "\t\t\t\t\t" +  price;
    }
}

~~~
#### 程序的开始
~~~java
package com.mask.day01;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/8/16 15:57
 * @Description: 点菜程序的开始
 */
public class DishMain {

    public static List<Dish> dishList = new ArrayList<>();
    public static List<Dish> orderList = new ArrayList<>();
    public static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        initDish();
        while (true) {
            showMenu();
            String next = scanner.next();
            switch (next){
                case "0":
                    System.exit(0);
                    break;
                case "1":
                    showList();// 点菜
                    break;
                case "2":
                    showOrderList();//查看已点菜
                    break;
                case "3":
                    pay();//买单
                    break;
            }
        }


    }

    /**
     * 买单
     */
    private static void pay() {
        double plus = 0;
        for (Dish dish : dishList) {
            plus += dish.getPrice();
        }

        System.out.println("-----正在结账--------");
        System.out.println("共消费了：" + plus + "元");
        System.exit(0);
    }

    /**
     * 展示已点菜单
     */
    private static void showOrderList() {
        for (Dish dish : orderList) {
            System.out.println(dish);
        }

    }

    /**
     * 展示菜单
     */
    private static void showList() {
        System.out.println("------请您点菜--------");
        for (Dish dish : dishList) {
            System.out.println(dish);
        }
        System.out.println("----------序号点菜，按0返回主菜单----------");


        while (true) {
            String next = scanner.next();
            if (next.equals("0")){
                break;
            }

            for (Dish dish : dishList) {
                boolean equals = dish.getId().toString().equals(next);
                if (equals){
                    System.out.println("您已点：" + dish.getName());
                    orderList.add(dish);
                }
            }
        }


    }

    private static void showMenu() {
        System.out.println("----------主菜单----------");
        System.out.println("菜单\t\t\t1");
        System.out.println("已点菜品\t\t2");
        System.out.println("买单\t\t\t3");
        System.out.println("----------按0返回主菜单----------");
    }


    /**
     * 初始化菜品
     */
    public static void initDish(){
        Dish dish1 = new Dish(1, "酸辣土豆丝", 10.0);
        Dish dish2 = new Dish(2, "麻婆豆腐", 12.0);
        Dish dish3 = new Dish(3, "西红柿炒蛋", 15.0);
        Dish dish4 = new Dish(4, "小炒肉", 18.0);
        Dish dish5 = new Dish(5, "佛跳墙", 99.0);

        dishList.add(dish1);
        dishList.add(dish2);
        dishList.add(dish3);
        dishList.add(dish4);
        dishList.add(dish5);
    }
}

~~~
