## 注解的作用

目的是为了简化配置。

Spring的全注解开发，注解配置非常繁杂：

- 使用的是`SSM`开发的话，建议XML+注解进配置

  XML负责配置第三方的类，注解配置自己定义的类。

- 使用的是`SpringBoot`开发，建议使用全注解



## 2. 常用注解

### 2.1 注解使用步骤相关

1. 开启组件扫描

   ```xml
   <!--启动组件扫描，指定对应扫描的包路径，该包及其子包下所有的类都会被扫描，加载包含指定注解的类-->
   <context:component-scan base-package="com.itheima"/>
   ```

2. 在要装配进Spring容器的类上添加注解

   ```java
   @Component("userService")  // 如果没有指定id，默认使用当前类型首字母小写作为id
   public class UserServiceImpl implements UserService {}
   ```

3. 通过Spring容器对象获取bean对象并测试

   ```java
   // 略
   ```

4. 注解使用注意事项
   在进行组件所扫描时，会对配置的包及其子包中所有文件进行扫描
   扫描过程是以文件夹递归迭代的形式进行的
   扫描过程仅读取合法的java文件
   扫描时仅读取spring可识别的注解
   扫描结束后会将可识别的有效注解转化为spring对应的资源加入Spring容器

5. 组件扫描之排除

   ```xml
   <!--启动组件扫描，指定对应扫描的包路径，该包及其子包下所有的类都会被扫描，加载包含指定注解的类-->
   <context:component-scan base-package="com.itheima">
       <!-- 不扫描指定的内容 -->
       <!-- 排除标注有指定注解的类 -->
       <context:exclude-filter type="annotation"
                               expression="org.springframework.stereotype.Controller"/>
   
      
       <context:exclude-filter type="custom" expression="com.itheima.web"/>
   </context:component-scan>
   ```

   

### 2.2 常用注解及功能

| 注解                                 |        | 等同于                         | 作用                           | 备注                               |
| ------------------------------------ | ------ | ------------------------------ | ------------------------------ | ---------------------------------- |
| <font color="red">@Component</font>  | 类上   | bean标签                       | 把当前类装配进Spring容器       | 默认以类名作为id<br>类名首字母小写 |
| <font color="red">@Controller</font> | 类上   | bean标签                       | 同@Component                   | 语义化的@Component                 |
| <font color="red">@Service</font>    | 类上   | bean标签                       | 同@Component                   | 语义化的@Component                 |
| <font color="red">@Repository</font> | 类上   | bean标签                       | 同@Component                   | 语义化的@Component                 |
| <font color="red">@AutoWired</font>  | 属性上 | `property[ref]`                | 从Spring容器中寻找对象注入     | 注入不依赖setter                   |
| <font color="red">@Value</font>      | 属性上 | `property[value]``             | 为普通类型属性注入内容         | 注入不依赖setter                   |
| @Qualifier                           | 属性上 | 配合@AutoWired实现按照名称注入 | 配合@AutoWired实现按照名称注入 |                                    |
| @Resource                            | 属性上 | =@AutoWired + @Qulifier        | 按照名称注入                   | jdk9及以上版本默认不支持           |
| @Scope                               | 类上   | bean标签scope属性              | bean的singleton\|prototype     | 默认Singleton                      |
| @PostConstruct                       | 方法上 | `init-method`属性              | 标注初始化方法                 |                                    |
| @PreDestroy                          | 方法上 | `destroy-method`               | 标注销毁方法                   | 多例销毁不受控制                   |
|                                      |        |                                |                                |                                    |
|                                      |        |                                |                                |                                    |
|                                      |        |                                |                                |                                    |
|                                      |        |                                |                                |                                    |
|                                      |        |                                |                                |                                    |
| @Primary                             | 方法上 | property[index=0]              |                                |                                    |
| @lazy@dependenson                    |        |                                |                                |                                    |



### 2.3 @AutoWired

作用：按照类型注入

**运行原理**

，根据被 标注的属性的类型，从Spring容器中查找是否有符合要求的bean对象。

- 容器中有且只有一个符合类型要求的bean，直接注入
- 容器中有且存在多个符合类型要求的bean，会根据成员变量名和容器中的beanId进行匹配，匹配成功就注入
- 匹配失败，就报错，提示需要的类型有两个 ，名字分别是什么，但是我都用不了。

```java
@Autowired
private UserDao userDao;
```



### 2.4 @Qualifier 

作用：配合@AutoWired共同实现：按照名称注入



```java
@Autowired
@Qualifier("userDao")
private UserDao userDao;
```



### 2.5 @Resource

等同于`@Autowired + @Qualifier`，但是属于javax拓展包，Java9及以上版本默认不加载拓展包的依赖，需要手动添加才能用。



### 2.6 工作中如何注入

工作中，为当前类成员变量注入，使用`@Autowired`





### 2.7 @Value

- 注入普通类型是属性 基本类型 + String
- 可以配合SpELl读取配置文件中内容



## 3. 全注解开发Spring

### 3.1 @Bean

作用：装配第三方类

定义一个方法，让方法的返回值是这个第三方类的对象；

方法定义在类中，要去类能被Spring识别，方法才能生效，所以要在类上加一个`@Component`（临时方案）

```java
@Component  //暂时方式，后期会调整
public class JDBCConfig {
    /**
     * @Bean 标注在方法上，会把当前方法的返回值装配进Spring容器，可以指定ID；
     * 如果没有指定id，以方法名作为id。
     * 通常情况下，我们的方法名会省略get，直接写dataSource
     * @return
     */
    // @Bean  这个时候，没有手动指定id，默认使用当前方法名作为id
    @Bean("dataSource")
    public static DruidDataSource dataSource(){
        
        // 读取properties配置文件
        
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/spring_db");
        ds.setUsername("root");
        ds.setPassword("itheima");
        return ds;
    }
}
```



> 注意：

连接四要素硬编码在Java代码中，可以手动读取properties配置文件，后者使用注解加载properties配置文件。

### 3.2 @PropertySource

作用：注解引入properties文件







### 3.3 @ComponentScan

作用：注解开启组件扫描

**配置类**   替代的 就是原来的   **配置文件**

@ComponentScan("basePackage")



- 不扫描指定内容

  ```java
  @ComponentScan(value = "com.itheima",
                 excludeFilters = {@ComponentScan.Filter({Controller.class})})
  @ComponentScan(value = "com.itheima",
                 excludeFilters = {
                     @ComponentScan.Filter(
                         type= FilterType.CUSTOM,
                         pattern = "com.itheima.web")})
  public class SpringConfig {}
  ```

  

  

  

### 3.5











### 3.x 常用注解及功能



| 注解            | 标注位置 | 等同于                                        | 作用                   | 备注               |
| --------------- | -------- | --------------------------------------------- | ---------------------- | ------------------ |
| @Bean           | 方法     | `<bean>`                                      | 装配第三方的类         | 默认以方法名作为id |
| @PropertySource | 类       | `<context:property-placeholder location=""/>` | 引入properties配置文件 |                    |
| @ComponentScan  | 类       | `<context:component-scan package=""/>`        | 开启组件扫描           |                    |
|                 |          |                                               |                        |                    |
|                 |          |                                               |                        |                    |
|                 |          |                                               |                        |                    |
|                 |          |                                               |                        |                    |
|                 |          |                                               |                        |                    |
|                 |          |                                               |                        |                    |
|                 |          |                                               |                        |                    |
|                 |          |                                               |                        |                    |
|                 |          |                                               |                        |                    |

















## Bean的加载控制

## 使用注解装配第三方类

## Spring整合junit

## `IoC`底层核心原理