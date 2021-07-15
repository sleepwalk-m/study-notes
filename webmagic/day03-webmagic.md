## 1. 定时任务
![image](https://user-images.githubusercontent.com/74847491/125717049-21c0965d-6246-4d3a-9c85-015012bc794a.png)
#### SpringBoot工程启动类添加 @EnableScheduling注解，新建一个类，在方法上添加@scheduled注解
## 2. 设置代理
使用代理服务器来发起请求，防止反爬策略封IP
<br>正常的流程是：
<br>- 爬虫服务器  -->  目标服务器 --> 采集数据
<br>- 而代理的流程是：
     <br> - 爬虫服务器  -->  代理服务器 --> 目标服务器
      <br>- 目标服务器 --> 代理服务器 --> 爬虫服务器 --> 解析数据
