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
