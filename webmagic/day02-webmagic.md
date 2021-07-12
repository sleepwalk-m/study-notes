# WebMagic
![image](https://user-images.githubusercontent.com/74847491/125260832-4c6d4980-e333-11eb-8203-b74e7fcfe92e.png)
### 中文文档：http://webmagic.io/docs/zh/posts/ch1-overview/thinking.html
注：由于特殊原因限制，无法将code推送到仓库。
## 1. 组件组成
  - downloader: 下载器组件
  - PageProcessor: 页面解析组件（必须自定义）
  - scheculer: 访问队列组件
  - pipeline: 数据持久化组件（默认是输出到控制台）
## 2. 入门案例
普通maven工程,添加依赖
~~~xml
        <dependency>
            <groupId>us.codecraft</groupId>
            <artifactId>webmagic-core</artifactId>
            <version>0.7.3</version>
        </dependency>
        <dependency>
            <groupId>us.codecraft</groupId>
            <artifactId>webmagic-extension</artifactId>
            <version>0.7.3</version>
        </dependency>
~~~
代码：
~~~java
/**
 * @author wb-jf930343
 * @version 1.0
 * @date 2021/7/12 17:26
 * @Description: 实现对网页页面的解析
 */
public class MyPageProcessor implements PageProcessor {


    /**
     * 下载结果封装为page对象
     * 可以从该对象获取下载结果
     *
     *
     * @param page
     */
    @Override
    public void process(Page page) {
        // 拿到html对象
        Html html = page.getHtml();
        String htmlStr = html.toString();
        // 传递到pipeline 默认是控制台输出 ConsolePipeline
        // 使用的ResultItems对象。本质上是一个map集合
        ResultItems resultItems = page.getResultItems();
        resultItems.put("html",htmlStr);

        // 方式2
        //page.putField("html",htmlStr);

    }

    /**
     * 返回一个site对象
     *  site是站点的配置。例如抓取的频率，时间间隔，抓取失败的重试次数等配置
     *  也可以使用默认配置Site.me()
     *
     * @return
     */
    @Override
    public Site getSite() {
        return Site.me();
    }


    public static void main(String[] args) {
        // 初始化并启动爬虫
        Spider.create(new MyPageProcessor())
                .addUrl("https://www.taobao.com/")
                // run() 同步方法：在一个线程启动，下面的代码阻塞
                // start() 新线程启动
                .run();
    }
}
~~~
