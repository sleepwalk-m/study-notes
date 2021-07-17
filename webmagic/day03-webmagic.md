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
## 3. 使用selenium + 无头浏览器抓取数据
![image](https://user-images.githubusercontent.com/74847491/125724210-eebabd62-8eea-4240-8864-5b962ef8725c.png)
### 3.1 selenium
- 是一个前端测试框架
- 使用代码控制浏览器
- 应该使用java版本的selenium
### 3.2 无头浏览器
- 没有界面的浏览器
- phantomjs
<b>测试京东-使用方法</b>
<br>导入依赖
~~~xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>us.codecraft</groupId>
            <artifactId>webmagic-core</artifactId>
            <version>0.7.3</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!--selenium-->
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>3.13.0</version>
        </dependency>
        <!--phantomjs-->
        <dependency>
            <groupId>com.codeborne</groupId>
            <artifactId>phantomjsdriver</artifactId>
            <version>1.4.3</version>
        </dependency>

    </dependencies>
~~~
~~~java
/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/15 11:03
 * @Description: phtomjs的测试 测试未成功需要登陆 且phantomjs并不好用 不深究
 */
public class PhantomJSTest {

    public static void main(String[] args) throws InterruptedException {
        /*
        在使用phantomjs：
            1. 安装phantomjs，解压到任意目录
            2. 添加selenimu和phantomjs的依赖
            3. 设置phantomjs的参数信息，配置phantomjs的安装路径

         */
        // 设置必要参数
        DesiredCapabilities dcaps = new DesiredCapabilities();
        // 指定phantomjs安装位置
        // 参数1：该浏览器的名字 参数2：安装路径（启动路径）
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "D:\\develop\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");

        // 4. 创建一个RemoteWebDriver对象，相当于启动浏览器
        // 不同的浏览器就用不同的实现类
//        new ChromeDriver()
//        new FirefoxDriver()
        RemoteWebDriver driver = new PhantomJSDriver(dcaps);
        // 5. 让其去访问一个url
        driver.get("https://list.jd.com/list.html?cat=9987,653,655");
        // 由于京东的页面 是通过ajax请求而来 需要向下滚动触发 这里直接来操作
        driver.executeScript("window.scrollTo(0,document.body.scrollHeight-300)");
        // 睡一段时间 让浏览器渲染成功
        Thread.sleep(5000);
        // 6. 得到结果     
        List<WebElement> list = driver.findElementsByCssSelector("li.gl-item");
        System.out.println(list.size());

        // 7. 关闭浏览器
        driver.close();
    }
}
~~~
##### Chorme的无头模式
![image](https://user-images.githubusercontent.com/74847491/125732099-31830dee-fba8-412d-b67d-a431a5146123.png)
selenium-java包中已经包含了chormedriver的驱动，不必再去导包
~~~java
/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/15 13:23
 * @Description: chorme的无头模式 + selenium
 */
public class ChromeTest {

    public static void main(String[] args) throws InterruptedException {
        // 创建chorme的配置信息
        System.setProperty("webdriver.chrome.driver","C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe");
        // 创建chorme对象
        ChromeOptions chromeOptions = new ChromeOptions();
        // 设置为无头模式
        //chromeOptions.addArguments("--headless");
        // 设置浏览器打开窗口大小
        chromeOptions.addArguments("--window-size=1920,1080");

        // 1. 基于配置信息，创建RemoteWebDriver对象
        RemoteWebDriver driver = new ChromeDriver(chromeOptions);
        // 解析京东案例
        //parseJD(driver);

        // 解析带反爬的政府网站案例
        // 2.  使用driver对象访问一个网站
        driver.get("http://www.yueyang.gov.cn/yyyb/index.htm");
        // 3. 使用driver控制网站的动作
        // 搜索框传参
        driver.findElementByCssSelector("#queryString").sendKeys("补充医疗保险");
        Thread.sleep(3000);
        // 点击搜索
        driver.findElementByCssSelector("input.but").click();
        Thread.sleep(3000);
        // 页面滚动到下方
        driver.executeScript("window.scrollTo(0,document.body.scrollHeight-300)");
        Thread.sleep(3000);

        // 4. 拿到结果
        List<WebElement> elementsByCssSelector = driver.findElementsByCssSelector("div.rb");
        System.out.println(elementsByCssSelector.size());



        // 5. 关闭浏览器
        driver.close();
    }

    private static void parseJD(RemoteWebDriver driver) throws InterruptedException {
        // 2.  使用driver对象访问一个网站
        driver.get("https://www.jd.com");
        // 3. 使用driver控制网站的动作
        // 搜索框传参
        driver.findElementByCssSelector("#key").sendKeys("华为");
        Thread.sleep(3000);
        // 点击搜索
        driver.findElementByCssSelector("button.button").click();
        Thread.sleep(3000);
        // 页面滚动到下方
        driver.executeScript("window.scrollTo(0,document.body.scrollHeight-300)");
        Thread.sleep(3000);

        // 4. 拿到结果
        List<WebElement> elementsByCssSelector = driver.findElementsByCssSelector("li.gl-item");
        System.out.println(elementsByCssSelector.size());
    }

}
~~~
## 4. 使用浏览器渲染 抓取京东商城的数据并保存
![image](https://user-images.githubusercontent.com/74847491/125741052-355922cd-d001-4bca-86c0-76d9b2357214.png)
项目结构：
![image](https://user-images.githubusercontent.com/74847491/125932136-10786e16-1a65-40d1-89aa-5f9af9fb1b10.png)
建表语句：
~~~sql
CREATE TABLE `jd_item` (
	`id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
	`spu` BIGINT NULL DEFAULT NULL COMMENT '商品集合id',
	`sku` BIGINT NULL DEFAULT NULL COMMENT '商品最小品类单元id',
	`title` VARCHAR(1000) NULL DEFAULT NULL COMMENT '商品标题',
	`price` FLOAT(10,0) NULL DEFAULT NULL COMMENT '商品价格',
	`pic` VARCHAR(200) NULL DEFAULT NULL COMMENT '商品图片',
	`url` VARCHAR(1500) NULL DEFAULT NULL COMMENT '商品详情地址',
	`created` DATETIME NULL DEFAULT NULL COMMENT '创建时间',
	`updated` DATETIME NULL DEFAULT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`),
	INDEX `sku` (`sku`) USING BTREE
)
COMMENT='京东商品'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=217;
~~~
POM依赖：
~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.mask.crawler</groupId>
    <artifactId>crawler-jd-headless</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <version>2.1.5.RELEASE</version>
    </parent>

    <properties>
        <maven.compiler.target>1.8</maven.compiler.target>
        <maven.compiler.source>1.8</maven.compiler.source>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>us.codecraft</groupId>
            <artifactId>webmagic-core</artifactId>
            <version>0.7.3</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>us.codecraft</groupId>
            <artifactId>webmagic-extension</artifactId>
            <version>0.7.3</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.1.1</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.6</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
        </dependency>
    </dependencies>

</project>
~~~
JdDownloader：
~~~java
package com.mask.crawler.jd.compoent;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.PlainText;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/15 16:25
 * @Description: 自定义downloader
 */
@Component
public class JdDownloader implements Downloader {

    private RemoteWebDriver driver;

    // 利用构造 初始化chromedriver
    public JdDownloader(){
        // 加载chromedriver 是使用chorme的必要条件
        System.setProperty("webdriver.chrome.driver","C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe");
        // 添加chrome的配置信息
        ChromeOptions chromeOptions = new ChromeOptions();
        // 设置为无头模式
        //chromeOptions.addArguments("--headless");
        // 设置打开的窗口大小 非必要属性
        chromeOptions.addArguments("--window-size=1920,1080");

        // 使用配置信息 创建driver对象
        driver = new ChromeDriver(chromeOptions);
    }




    /**
     * 自定义downloader
     *
     * @param request
     * @param task
     * @return
     */
    @Override
    public Page download(Request request, Task task) {
        /**
         * 2种情况
         *  1. 列表页：
         *         使用chrome 无头模式 来拿到渲染后的html并封装为page对象 给到processor
         *         同时： 还要去做分页
         *  2. 详情页：
         *          不做操作 直接拿到html封装为page并返回
         */

        // 使用上下文做标识  list：列表页  detail：详情页
        String level = request.getExtra("level").toString();
        String url = request.getUrl();
        if ("list".equals(level)){
            try {
                // 如果是列表页，需要做2件事情
                // 解析列表 使用浏览器渲染
                driver.get(url);
                // 搜索框传参
                driver.findElementByCssSelector("#key").sendKeys("华为");
                Thread.sleep(2000);
                // 点击搜索
                driver.findElementByCssSelector("button.button").click();
                Thread.sleep(2000);
                // 页面滚动到下方
                Integer start = 0;
                Integer end = 500;
                //6000为最大值
                while (true){
                    if (end == 6000){
                        break;
                    }
                    String scriptStr = "window.scrollTo("+ start + ","+ end +")";
                    driver.executeScript(scriptStr);
                    Thread.sleep(500);
                    start+=500;
                    end+=500;
                }

                String html = driver.getPageSource();
                Page page = createPage(html, driver.getCurrentUrl(), "list", request.getExtra("pageNum").toString());
                return page;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 分页 todo
        if ("page".equals(level)){
            try {
                // 分页的请求，就来做点击分页
                // 这里还是走首页，再搜索，再去点击分页
                driver.get("http://www.jd.com/");
                // 搜索框传参
                driver.findElementByCssSelector("#key").sendKeys("华为");
                Thread.sleep(2000);
                // 点击搜索
                driver.findElementByCssSelector("button.button").click();
                Thread.sleep(2000);

                // 这里再去点击分页
                String pageNum = request.getExtra("pageNum").toString();
                for (int i = 1; i < Integer.valueOf(pageNum); i++) {
                    // 点击翻页
                    driver.findElementByCssSelector(".fp-next").click();
                    Thread.sleep(2000);
                }

                // 页面滚动到下方
                Integer start = 0;
                Integer end = 500;
                //6000为最大值
                while (true){
                    if (end == 6000){
                        break;
                    }
                    String scriptStr = "window.scrollTo("+ start + ","+ end +")";
                    driver.executeScript(scriptStr);
                    Thread.sleep(500);
                    start+=500;
                    end+=500;
                }

                Page page = createPage(driver.getPageSource(), driver.getCurrentUrl(), "list", pageNum);
                return page;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        // 详情页
        if ("detail".equals(level)){
            // 详情的情况 不必去浏览器渲染 直接拿数据
            driver.get(url);
            // 封装page对象
            Page page = createPage(driver.getPageSource(), request.getExtra("detailUrl").toString(), "detail", request.getExtra("pageNum").toString());
            return page;
        }


        return null;
    }

    /**
     * 公共的封装page对象的方法
     *
     * @param html
     * @param url
     */
    private Page createPage(String html, String url,String flag,String pageNum) {
        Page page = new Page();
        // 设置回去html
        page.setRawText(html);
        // 把url包装成selectable对象
        page.setUrl(new PlainText(url));
        // 下载成功
        page.isDownloadSuccess();

        // 还要设置request对象
        Request request = new Request(url);
        // 这一步是设置标识 在解析的时候根据标识解析不同的页面
        request.putExtra("level",flag);
        request.putExtra("pageNum",pageNum);
        page.setRequest(request);

        return page;
    }

    /**
     * 处理线程的 不必去操作
     * @param threadNum
     */
    @Override
    public void setThread(int threadNum) {

    }
}

~~~
JdPageProcessor：
~~~java
package com.mask.crawler.jd.compoent;

import com.mask.crawler.jd.pojo.Item;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/15 17:27
 * @Description: 处理页面
 */
@Component
public class JdPageProcessor implements PageProcessor {

    @Value("${indexUrl}")
    private String indexUrl;

    @Override
    public void process(Page page) {
        String level = page.getRequest().getExtra("level").toString();
        switch (level) {
            case "list":
                parseList(page);
                break;
            case "detail":
                parseDetail(page);
                break;
        }

    }

    /**
     * 解析列表页
     *
     * @param page
     */
    private void parseList(Page page) {
        Html html = page.getHtml();
        // 这里拿到sku 和 spu 并交给pipeline
        List<Selectable> nodes = html.$("ul.gl-warp.clearfix > li").nodes();
        List<Item> itemList = new ArrayList<>();
        for (Selectable node : nodes) {
            // 拿到sku和spu
            String sku = node.$("li").xpath("///@data-sku").get();
            String spu = node.$("li").xpath("///@data-spu").get();
            String href = "https:" + node.$("div.p-img a").xpath("///@href").get();

            Item item = new Item();
            item.setSku(Long.valueOf(sku));
            item.setSpu(StringUtils.isNotBlank(spu) ? Long.valueOf(spu) : null);
            item.setCreated(new Date());
            itemList.add(item);

            // 同时还需要把链接加到详情页 加到队列
            Request request = new Request(href);
            request.putExtra("level", "detail");
            request.putExtra("pageNum", page.getRequest().getExtra("pageNum"));
            request.putExtra("detailUrl", href);
            page.addTargetRequest(request);
        }


        // 以集合的方式存入
        page.putField("itemList", itemList);

        // 这里目前做的是 当第一页才去做分页，只做一次分页。相当于测试，只拿前2页的数据。
        if (page.getRequest().getExtra("pageNum").equals("1")) {
            // 同时还要去做分页
            String pageNum = page.getRequest().getExtra("pageNum").toString();
            Request request = new Request("https://nextpage.com");
            request.putExtra("level", "page"); // 标识去分页
            request.putExtra("pageNum", (Integer.valueOf(pageNum) + 1) + "");// 页码要+1 接下来要的是第二页
            // 添加到队列
            page.addTargetRequest(request);
        }

    }

    /**
     * 解析详情页
     *
     * @param page
     */
    private void parseDetail(Page page) {
        Html html = page.getHtml();
        String title = html.$("div.master .p-name").xpath("///allText()").get();
        String priceStr = html.$("div.summary-price-wrap .p-price span.price").xpath("///allText()").get();
        String pic = "https:"+html.$("#spec-img").xpath("///@src").get();
        String url = "https:"+html.$("div.master .p-name a").xpath("///@href").get();
        String sku = html.$("a.notice.J-notify-sale").xpath("///@data-sku").get();

        Item item = new Item();
        item.setTitle(title);
        item.setPic(pic);
        item.setPrice(Float.valueOf(priceStr));
        item.setUrl(url);
        item.setUpdated(new Date());
        item.setSku(StringUtils.isNotBlank(sku)?Long.valueOf(sku) : null);

        // 单条数据塞入
        page.putField("item", item);

    }

    @Override
    public Site getSite() {
        return Site.me();
    }
}

~~~
JdPipeline：
~~~java
package com.mask.crawler.jd.compoent;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mask.crawler.jd.mapper.ItemMapper;
import com.mask.crawler.jd.pojo.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/15 17:58
 * @Description: 自定义pipeline
 */
@Component
public class JdPipeline implements Pipeline {


    @Autowired
    private ItemMapper itemMapper;


    /**
     * 自定义pipeline来存到数据库
     *
     * @param resultItems
     * @param task
     */
    @Override
    public void process(ResultItems resultItems, Task task) {
        // 从结果集种拿到数据 有list数据 则代表是列表页的保存
        List<Item> itemList = resultItems.get("itemList");
        if (itemList != null){
            for (Item item : itemList) {
                itemMapper.insert(item);
            }
        }else {
            // 没有list数据，则表示是更新详情页的数据
            Item item = resultItems.get("item");
            itemMapper.update(item, Wrappers.<Item>lambdaUpdate().eq(Item::getSku,item.getSku()));
        }

    }
}

~~~
JdSpider：
~~~java
package com.mask.crawler.jd.compoent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/15 18:07
 * @Description: 爬虫的启动
 */
@Component
public class JdSpider {

    @Value("${indexUrl}")
    String indexUrl;

    @Autowired
    private JdPageProcessor pageProcessor;

    @Autowired
    private JdPipeline jdPipeline;

    @Autowired
    private JdDownloader jdDownloader;



    //@Scheduled(cron = "0 0 8 ? * *")
    @Scheduled(fixedRate = 3600*60*24)
    public void doCrawler(){
        QueueScheduler queueScheduler = new QueueScheduler();
        queueScheduler.setDuplicateRemover(new BloomFilterDuplicateRemover(1000000));

        Request request = new Request(indexUrl);
        request.putExtra("level","list");
        request.putExtra("pageNum","1");

        Spider.create(pageProcessor)
                .setDownloader(jdDownloader)
                .addPipeline(jdPipeline)
                .setScheduler(queueScheduler)
                .thread(1)
                .addRequest(request)
                .start();
    }
}
~~~
POJO:
~~~java
package com.mask.crawler.jd.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/9 17:18
 * @Description: Item 商品实体
 */
@TableName("jd_item")
@Data
public class Item {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField("spu")
    private Long spu;
    @TableField("sku")
    private Long sku;
    @TableField("title")
    private String title;
    @TableField("price")
    private Float price;
    @TableField("pic")
    private String pic;
    @TableField("url")
    private String url;
    @TableField("created")
    private Date created;
    @TableField("updated")
    private Date updated;
}
~~~
application.yml:
~~~yml
spring:
  application:
    name: crawler-jd-headless
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/crawler_jd?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: root

indexUrl: "https://www.jd.com/"
~~~
