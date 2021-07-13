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
## 3. PageProcessor对象
![image](https://user-images.githubusercontent.com/74847491/125381243-25138c80-e3c6-11eb-8097-11d06c55a101.png)
![image](https://user-images.githubusercontent.com/74847491/125383026-52156e80-e3c9-11eb-8ba2-b5bd91b3a9ab.png)
    
</br><b>代码</b>：
~~~java
/**
 * @author wb-jf930343
 * @version 1.0
 * @date 2021/7/13 10:36
 * @Description: 练习 html的 3种解析方式
 */
public class MyPageProcessor2 implements PageProcessor {


    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        // 1. 原生jsoup解析
        Document document = html.getDocument();
        String title1 = document.getElementsByTag("title").text();
        page.putField("title1",title1);
        // 2. css选择器
        // html.css()==>html.$()  两种方式等价，参数都是CSS选择器
        // 是重载方法，传参属性名
        String aStr = html.css("#logo > h1 > a", "text").get();
        String aHref = html.css("#logo > h1 > a", "href").get();
        page.putField("aStr",aStr);
        page.putField("aHref",aHref);
        // 3. xpath
        String title2 = html.xpath("//title/allText()").get();
        page.putField("title2",title2);


        // 当选择的选择器对应多个元素的时候，例如多个li列表，默认只会取第一个选择
        String liStr = html.$("div.J_cate > ul > li").get();
        page.putField("liStr",liStr);

        // 要选取所有，有两种方式.返回值有差异，按需选择
        // 1. html.all()
        List<String> all = html.$("div.J_cate > ul > li").all();
        page.putField("all",all);

        // 2. html.nodes()
        List<Selectable> nodes = html.$("div.J_cate > ul > li").nodes();
        page.putField("nodes",nodes);
        


    }

    @Override
    public Site getSite() {
        return Site.me();
    }


    public static void main(String[] args) {
        Spider.create(new MyPageProcessor2())
                .addUrl("https://www.jd.com")
                .start();
    }
}
~~~
## 4. Pipeline
- 数据持久化组件
- 框架提供3个实现：
  - ConsolePipeline: 默认的pipeline，在控制台输出
  - FilePipeline: 持久化到本地磁盘
  - JsonFilePipeline: 以JSON格式保存到本地
### 持久化到本地案例：用自己的方式写了一个json保存到本地
~~~java
/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/13 11:25
 * @Description: 抓取数据 保存到本地磁盘
 */
public class FilePipelineProcessor implements PageProcessor {

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        Map<String, Object> extras = page.getRequest().getExtras();
        String level = extras.get("level").toString();
        if (StringUtils.isBlank(level)){
            level = "first";
        }
        switch (level){
            case "first":
                parseIndex(page,html);
                break;
            case "detail":
                parseDetailPage(page,html);
                break;
        }
    }

    /**
     * 解析详情页
     *
     * @param page
     * @param html
     */
    private void parseDetailPage(Page page, Html html) {
        String title = html.$("h1.xx-tit").xpath("///allText()").get();
        String content = html.$("div.view.TRS_UEDITOR").xpath("///allText()").get();
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("title",title);
        dataMap.put("content",content);
        page.putField("data", JSON.toJSONString(dataMap));
    }

    /**
     * 解析网站首页，拿到所有的详情页标签
     *
     * @param page
     */
    private void parseIndex(Page page,Html html) {
        List<Selectable> nodes = html.$("ul.cm-news-list > li > a").nodes();
        for (Selectable node : nodes) {
            String url = "http://sxwjw.shaanxi.gov.cn/sy/sxdt/"+node.xpath("///@href").get();
            String title = node.xpath("//allText()").get();
            Request request = new Request(url);
            Map<String,Object> map = new HashMap<>();
            map.put("level","detail");
            request.setExtras(map);
            page.addTargetRequest(request);
        }
    }

    @Override
    public Site getSite() {
        return Site.me();
    }

    public static void main(String[] args) {
        JsonFilePipeline jsonFilePipeline = new JsonFilePipeline();
        jsonFilePipeline.setPath("D:\\webmagic-testdata");

        String url = "http://sxwjw.shaanxi.gov.cn/sy/sxdt/index_1.html";
        Request request = new Request(url);
        Map<String,Object> map = new HashMap<>();
        map.put("level","first");
        request.setExtras(map);

        Spider.create(new FilePipelineProcessor())
                .addRequest(request)
                .addPipeline(new ConsolePipeline())
                .addPipeline(jsonFilePipeline)
                .start();
    }
}
~~~
