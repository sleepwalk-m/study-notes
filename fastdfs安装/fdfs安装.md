# 阿里云轻量应用服务器环境搭建-Docker安装FastDFS镜像

> 参考文章：https://www.cnblogs.com/provence666/p/10987156.html

> 通过FastDFS镜像安装，可以省去很多很多的配置。下面开始。

- 搜索镜像

```bash
docker search fastdfs

```

![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703183803358.png)

- 拉取镜像

```bash
docker pull delron/fastdfs

```

![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703183816918.png)

- 查看镜像

```bash
docker images

```

![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703183838316.png)

- 使用docker镜像构建tracker容器（跟踪服务器，起到调度的作用）：

```bash
docker run -dti --network=host --name tracker -v /var/fdfs/tracker:/var/fdfs -v /etc/localtime:/etc/localtime delron/fastdfs tracker
1
```

![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703183900410.png)

- 使用docker镜像构建storage容器（存储服务器，提供容量和备份服务）：

> 注意：下面命令中的111.222.333.444要换成自己云服务器的ip地址（==公网ip==），端口号不要改，**记得之后要在阿里云控制台添加防火墙规则，打开22122端口**

```bash
docker run -dti --network=host --name storage -e TRACKER_SERVER=111.222.333.444:22122 -v /var/fdfs/storage:/var/fdfs  -v /etc/localtime:/etc/localtime  delron/fastdfs storage
1
```

> **注意：TRACKER_SERVER=本机的ip地址:22122 本机ip地址不要使用127.0.0.1**
> ![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703184221564.png)

进入storage容器，到storage的配置文件中配置http访问的端口，配置文件在/etc/fdfs目录下的storage.conf。默认端口是8888，也可以不进行更改。

==由于我的服务器80端口已经占用，这里就使用8888端口，如果要更改记得要更改storage.conf+nginx.conf里面的端口==

- 进入storage容器

```bash
docker exec -it edd7ca5725ab /bin/bash

```

![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703184254569.png)

- 找到`storage.conf`

```bash
cd /etc/fdfs
cat storage.conf
12
```

![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703184427854.png)
![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703184443814.png)
![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703184456804.png)
![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703184519820.png)

- 配置nginx

进入storage,配置nginx，在/usr/local/nginx/conf目录下，找到nginx.conf文件,默认配置不修改

- 找到`nginx.conf`

```bash
cd /usr/local/nginx/conf
cat nginx.conf
12
```

![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703184539421.png)
![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703184553647.png)

### 本地上传的文件存储在FastDFS中的那个位置呢？来找一下

- 首先进入storage容器

```bash
docker exec -it storage /bin/bash
1
```

- 然后回到根目录，再进入到`var/fdfs/data`，data目录下的00、01、等等是FastDfs中做的分组（group），上传的文件会存在这些分组中
  ![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703184725436.png)
  ![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200703184738718.png)

### 常见问题

- FastDFS的storage容器无法启动
  ![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200915173830618.png)

> 解决：
>
> - 删除`fdfs_storaged.pid`
>
> ```bash
> cd /var/fdfs/storage/data
> rm fdfs_storaged.pid
> 12
> ```
>
> ![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200915173856302.png)
>
> - 再次启动
>
> ```bash
> docker start storage
> docker ps -a
> 12
> ```
>
> ![在这里插入图片描述](fdfs%E5%AE%89%E8%A3%85.assets/20200915173954125.png)

> 创作不易，喜欢的话加个关注点个赞，谢谢谢谢谢谢！

## 访问地址是：

返回的 主机公网ip+端口+文件组名；例如：

http://1.2.3.4:8888/group1/M00/00/00/rBoXrmFuxueAGEZdAAAMuL64Eik018.png