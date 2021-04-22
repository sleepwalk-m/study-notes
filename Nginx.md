# Nginx学习笔记
<i>该笔记为简化版，通俗写出自己的理解，详细版参见链接</i>
> 视频:https://www.bilibili.com/video/BV1F5411J7vK?p=3&spm_id_from=pageDriver <br>
> 笔记:https://www.kuangstudy.com/bbs/1377454518035292162
## Nginx简介
<strong>Nginx是一个高性能的HTTP和反向代理web服务器。简而言之，是一个服务器。</strong><br><br>
<strong>其特点是：<br>&nbsp;&nbsp;&nbsp;占有内存少，并发能力强，并发能力在同类型的网页服务器中表现较好。国内各大厂都有使用。其完全由C语言编写，支持50000并发，而tomcat只有5-600并发。</strong>
## Nginx作用?
<b style="fontsize: 100px">主要作用：HTTP代理、负载均衡、动静分离（静态资源服务器）</b><br><br>
<b>一、正向代理客户端，反向代理服务器。<br><br>
  1.正向代理：在本地代理，例如使用GitHub要在本地PC安装VPN软件，通过这个VPN请求外部的资源（例如请求香港的服务器）==> 香港服务器再去请求国外服务器 ==> 返回数据至香港服务器 ==> 再返回到本地，相当于一个跳板<br><br>
  2.反向代理：代理服务器端，例如www.baidu.com，我们在访问时总是访问这一个域名，但是肯定不止一台服务器，那么多台服务器指向同一个域名，中间就是通过反向代理服务器来做到，我们访问的是代理服务器，代理服务器再去找到百度服务器返回数据。<br><br>
  如图<br><br></b>![image](https://user-images.githubusercontent.com/74847491/115529639-a676e980-a2c5-11eb-9539-7d0bdc92d045.png)<br><br>
<b>二、负载均衡
  > Nginx提供的负载均衡策略有2种：内置策略和扩展策略。内置策略为轮询，加权轮询，Ip hash。扩展策略，就天马行空，只有你想不到的没有他做不到的。
1. 轮询：每一台服务器依次处理请求，这样每一个服务器都是一样的负载。
2. 加权轮询：根据服务器权重的不同，服务器处理的请求量也不同。
3. iphash：iphash对客户端请求的ip进行hash操作，然后根据hash结果将同一个客户端ip的请求分发给同一台服务器进行处理，可以解决session不共享的问题。但是这种办法并不好，这样的话仍然是针对一台服务器请求，如果服务器挂了session仍然没了，解决session共享仍然选择Redis（将session写入redis中，用hash结构）。<br><br>
三、动静分离：<br>
  动静分离，在我们的软件开发中，有些请求是需要后台处理的，有些请求是不需要经过后台处理的（如：css、html、jpg、js等等文件），这些不需要经过后台处理的文件称为静态文件。让动态网站里的动态网页根据一定规则把不变的资源和经常变的资源区分开来，动静资源做好了拆分以后，我们就可以根据静态资源的特点将其做缓存操作。提高资源响应的速度<br><br>
四、Nginx常用命令<br>
 cd /usr/local/nginx/sbin/<br>
./nginx  启动<br>
./nginx -s stop  停止<br>
./nginx -s quit  安全退出<br>
./nginx -s reload  重新加载配置文件<br>
ps aux|grep nginx  查看nginx进程 <br>
五、实战<br><br>
  nginx.conf的配置：<br>
  反向代理、负载均衡
  
  ![image](https://user-images.githubusercontent.com/74847491/115664754-d29d7380-a374-11eb-819c-d00ec0c814b1.png)

</b>
