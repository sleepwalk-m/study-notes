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
### 2.1 webmagic的代理设置
可用的免费代理：
- https://proxy.mimvp.com/freesecret?proxy=in_hp&sort=&page=1
- http://www.xiladaili.com/gaoni/
~~~java
/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/15 10:31
 * @Description: 测试代理
 */
public class MyPageProcessor implements PageProcessor {

    private static final Site SITE = new Site();

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        String htmlStr = html.get();
        page.putField("htmlStr",htmlStr);
    }

    @Override
    public Site getSite() {
        SITE.setTimeOut(10000);
        return SITE;
    }

    public static void main(String[] args) {
        // 获取下载对象
        HttpClientDownloader downloader = new HttpClientDownloader();
        // 设置代理。代理可以去网上找免费的. 目前找的代理我这边都发不出去
        downloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("119.36.157.236",64909)));

        Spider.create(new MyPageProcessor())
                .setDownloader(downloader)
                .addUrl("http://www.baidu.com")
                .start();
    }
}
~~~
