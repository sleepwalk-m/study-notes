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
## 5. Scheduler
![image](https://user-images.githubusercontent.com/74847491/125390412-6fe8d080-e3d5-11eb-89c3-31e0fcd6bb90.png)
#### URL去重之布隆过滤器
![image](https://user-images.githubusercontent.com/74847491/125390392-652e3b80-e3d5-11eb-9683-72e4ff8621da.png)
#### 布隆过滤器使用方法
坐标：
~~~xml
<!--webmagic对布隆过滤器的支持-->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>27.0.1-jre</version>
        </dependency>
~~~
代码：
~~~java
/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/13 12:30
 * @Description: URL去重的测试
 */
public class UrlDupProcessor implements PageProcessor {


    @Override
    public void process(Page page) {
        String level = page.getRequest().getExtra("level").toString();
        if ("first".equals(level)){
            // 设置5个url 1和5不同 1和其他都相同 测试是否最终数据仅有2个
            String url1 = "http://sxwjw.shaanxi.gov.cn/sy/sxdt/202107/t20210707_2182221.html";
            String url2 = "http://sxwjw.shaanxi.gov.cn/sy/sxdt/202107/t20210707_2182221.html";
            String url3 = "http://sxwjw.shaanxi.gov.cn/sy/sxdt/202107/t20210707_2182221.html";
            String url4 = "http://sxwjw.shaanxi.gov.cn/sy/sxdt/202107/t20210707_2182221.html";
            String url5 = "http://sxwjw.shaanxi.gov.cn/sy/sxdt/202107/t20210706_2182081.html";
            List<String> list = new ArrayList<>();
            list.add(url1);
            list.add(url2);
            list.add(url3);
            list.add(url4);
            list.add(url5);
            for (String s : list) {
                Request request = new Request(s);
                HashMap<String, Object> map = new HashMap<>();
                map.put("level", "detail");
                request.setExtras(map);
                page.addTargetRequest(request);
            }
        }

        // 解析详情页保存数据
        if ("detail".equals(level)){
            Html html = page.getHtml();
            String title = html.$("h1.xx-tit").xpath("///allText()").get();
            String content = html.$("div.view.TRS_UEDITOR").xpath("///allText()").get();
            Map<String,String> dataMap = new HashMap<>();
            dataMap.put("title",title);
            dataMap.put("content",content);
            page.putField("data", JSON.toJSONString(dataMap));
        }

    }

    @Override
    public Site getSite() {
        return Site.me();
    }

    public static void main(String[] args) {
        Request request = new Request();
        request.setUrl("http://www.baidu.com");
        HashMap<String, Object> map = new HashMap<>();
        map.put("level", "first");
        request.setExtras(map);

        FilePipeline jsonFilePipeline = new FilePipeline();
        jsonFilePipeline.setPath("D:\\webmagic-testdata");

        // 1. 首先不指定scheduler 使用默认的 去重成功
        // 2. 设置布隆过滤器，创建一个Scheduler 内存的。 去重成功
        QueueScheduler queueScheduler = new QueueScheduler();
        // 指定队列使用布隆过滤器去重   传参： 初始化一个布隆过滤器 参数是容量 理解为 可以去重这个级别的URL
        queueScheduler.setDuplicateRemover(new BloomFilterDuplicateRemover(10000000));
        Spider.create(new UrlDupProcessor())
                .addRequest(request)
                .addPipeline(new ConsolePipeline())
                .addPipeline(jsonFilePipeline)
                .start();
    }
}
~~~
<font style='color: red'><b>测试：使用默认的scheduler和布隆过滤器均成功去重</b></font>
## 6. 综合案例
![image](https://user-images.githubusercontent.com/74847491/125394951-0ff62800-e3dd-11eb-9543-fb598ff59710.png)
<br><b>项目结构：</b></br>
![image](https://user-images.githubusercontent.com/74847491/125546411-d4d9470c-bb89-49e0-a86e-dd4d90b7f0b2.png)

1. 初始环境搭建
  依赖
~~~xml
<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
		</dependency>

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
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-lang3</artifactId>
		</dependency>
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.75</version>
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
			<version>8.0.25</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-jdbc</artifactId>
			<version>5.3.8</version>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.2.4</version>
		</dependency>

		<dependency>
			<groupId>com.google.guava</groupId>
			<artifactId>guava</artifactId>
			<version>27.0.1-jre</version>
		</dependency>
	</dependencies>
~~~
2. 建表语句
~~~sql
create table `job_info`(
	`id` bigint(20) not null auto_increment comment '主键id',
	`company_name` varchar(100) default null comment '公司名称',
	`company_addr` varchar(200) default null comment '公司联系方式',
	`company_info` text comment '公司信息',
	`job_name` varchar(100) default null comment '职位名称',
	`job_addr` varchar(50) default null comment '工作地点',crawler_jd
	`job_info` text comment '职位信息',
   `salary_min` float(10,2) default null comment '薪资范围，最小',
   `salary_max` float(10,2) default null comment '薪资范围，最大',
	`url` varchar(1500) default null comment '招聘信息详情页',
	`time` varchar(10) default null comment '职位最近发布时间',
	primary key(`id`)
) engine=innodb auto_increment=217 default charset=utf8 comment='招聘信息';
~~~
3. POJO
~~~java
@Data
@TableName("job_info")
public class JobInfo implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField("company_name")
    private String companyName;
    @TableField("company_addr")
    private String companyAddr;
    @TableField("company_info")
    private String companyInfo;
    @TableField("job_name")
    private String jobName;
    @TableField("job_addr")
    private String jobAddr;
    @TableField("job_info")
    private String jobInfo;
    @TableField("salary_min")
    private Float salaryMin;
    @TableField("salary_max")
    private Float salaryMax;
    @TableField("url")
    private String url;
    @TableField("time")
    private String time;
}
~~~
5. JobPageProcessor
~~~java
/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/13 14:20
 * @Description:
 */
@Component
public class JobPageProcessor implements PageProcessor {

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
     * 处理详情页
     *
     * @param page
     */
    @Transactional
    private void parseDetail(Page page) {
        Html html = page.getHtml();
        String companyName = html.$("p.cname > a:first-child").xpath("///allText()").get();
        String companyAddr = html.$("div.bmsg.inbox > p").xpath("///allText()").get();
        String companyInfo = html.$("div.tCompany_main > div:last-child").xpath("///allText()").get();
        String jobName = html.$("div.cn > h1").xpath("///@title").get();
        String tempJobInfo = html.$("p.msg.ltype").xpath("///allText()").get();
        String[] split = null;
        if (StringUtils.isNotBlank(tempJobInfo)) {
            split = tempJobInfo.split("\\|");
        }
        String jobAddr = "";
        if (split != null && split.length > 4) {
            jobAddr = split[0].trim();

        }
        String jobInfoStr = html.$("div.bmsg.job_msg.inbox").xpath("///allText()").get();
        String tempSalaryStr = html.$("div.cn > strong").xpath("///allText()").get();
        String[] salary = tempSalaryStr.substring(0, tempSalaryStr.lastIndexOf("万")).split("-");
        String salaryMin = "";
        String salaryMax = "";
        if (salary.length > 1){
            salaryMin = salary[0];
            salaryMax = salary[1];
        }

        String url = page.getUrl().toString();
        String time = "";
        if (split != null && split.length > 4) {
            time = split[split.length-1].trim();
        }


        // 去保存
        JobInfo jobInfo = new JobInfo();
        jobInfo.setCompanyName(companyName);
        jobInfo.setCompanyAddr(companyAddr);
        jobInfo.setCompanyInfo(companyInfo);
        jobInfo.setJobName(jobName);
        jobInfo.setJobAddr(jobAddr);
        jobInfo.setJobInfo(jobInfoStr);
        jobInfo.setUrl(url);
        jobInfo.setTime(time);
        jobInfo.setSalaryMin(Float.valueOf(salaryMin));
        jobInfo.setSalaryMax(Float.valueOf(salaryMax));

        page.putField("jobinfo",jobInfo);
    }

    /**
     * 处理列表页
     *
     * @param page
     */
    private void parseList(Page page) {
        Html html = page.getHtml();
        String rawPage = html.xpath("//body/allText()").get();
        JSONObject jsonObject = JSON.parseObject(rawPage);
        List<String> list = (List<String>) JSONPath.eval(jsonObject, "engine_search_result[*].job_href");
        for (String s : list) {
            Request request = new Request(s);
            Map<String, Object> map = new HashMap<>();
            map.put("level", "detail");
            request.setExtras(map);
            page.addTargetRequest(request);
        }

        // 分页


    }

    @Override
    public Site getSite() {
        return Site.me();
    }

}
~~~
6. JobScheduler
~~~java
/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/13 17:03
 * @Description: 定义scheduler
 */
@Configuration
public class JobScheduler{

    @Bean("scheduler")
    public Scheduler scheduler(){
        BloomFilterDuplicateRemover bloomFilterDuplicateRemover = new BloomFilterDuplicateRemover(10000000);
        return new QueueScheduler().setDuplicateRemover(bloomFilterDuplicateRemover);
    }
}
~~~
7. MyPipeline
~~~java
/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/13 16:57
 * @Description: 自定义pipeline
 */
@Component
public class MyPipeline implements Pipeline {

    @Autowired
    private JobInfoMapper jobInfoMapper;


    @Override
    public void process(ResultItems resultItems, Task task) {
        // 取出数据
        JobInfo jobinfo = resultItems.get("jobinfo");

        // 保存数据库
        jobInfoMapper.insert(jobinfo);

    }
}
~~~
8. JobSpider（爬虫启动）
~~~java
/**
 * @author Mask.m
 * @version 1.0
 * @date 2021/7/13 17:00
 * @Description: 爬虫启动
 */
@Component
public class JobSpider {

    @Autowired
    private JobPageProcessor jobPageProcessor;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private MyPipeline myPipeline;

    private static final String URL = "https://search.51job.com/list/080200,000000,0000,32,9,99,Java%25E5%25BC%2580%25E5%258F%2591,2,1.html?lang=c&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&ord_field=0&dibiaoid=0&line=&welfare=";

    public void  doCrawler(){
        Request request = new Request(URL);
        request.addHeader("Accept","application/json, text/javascript, */*; q=0.01");
        Map<String, Object> map = new HashMap<>();
        map.put("level", "list");
        request.setExtras(map);
        Spider.create(jobPageProcessor)
                .addPipeline(myPipeline)
                //.setScheduler(scheduler)
                .addRequest(request)
                .start();
    }
}
~~~
## 51job详情页出现反爬，需要伪装header解决，下列代码添加了header信息
~~~java
package com.mask.ssm.job.compoent;

import com.mask.ssm.job.pojo.JobInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Mask.m
 * @create: 2021/07/14 20:15
 * @description:
 */
@Component
public class JobPageProcessor implements PageProcessor {



    public void process(Page page) {
        // 上下文 可以传递一些数据
        Map<String, Object> extras = page.getRequest().getExtras();
        String level = extras.get("level").toString();

  /*      if (StringUtils.isBlank(level)){
            level = "list";
        }*/

        switch (level){
            case "list":
                parseList(page);
                break;
            case "detail":
                parseDetail(page);
                break;
        }

    }

    /**
     * 解析详情页
     *
     * @param page
     */
    private void parseDetail(Page page) {
        Html html = page.getHtml();
        String companyName = html.$("p.cname > a:first-child").xpath("///allText()").get();
        String companyAddr = html.$("div.tCompany_main > div:nth-of-type(2) p").xpath("///allText()").get();
        String companyInfo = html.$("div.tCompany_main > div:last-child").xpath("///allText()").get();
        String jobName = html.$("div.cn > h1").xpath("///allText()").get();
        String tempJobStr = html.$("p.msg.ltype").xpath("///allText()").get();
        String jobAddr = "";
        String time = "";
        if (StringUtils.isNotBlank(tempJobStr)){
            String[] split = tempJobStr.split("|");
            if (split.length > 4){
                jobAddr = split[0];
                time = split[split.length-1];
            }
        }
        String jobInfoStr = html.$("div.bmsg.job_msg.inbox p").xpath("///allText()").get();
        String tempSalaryStr = html.$("div.cn strong").xpath("///allText()").get();
        String salaryMin = "";
        String salaryMax = "";
        if (StringUtils.isNotBlank(tempSalaryStr)){
            String[] salarySplit = tempSalaryStr.split("万")[0].split("-");
            if (salarySplit.length > 1){
                salaryMin = salarySplit[0];
                salaryMax = salarySplit[1];
            }
        }
        String url = page.getUrl().get();

        // 去保存
        JobInfo jobInfo = new JobInfo();
        jobInfo.setCompanyName(companyName);
        jobInfo.setCompanyAddr(companyAddr);
        jobInfo.setCompanyInfo(companyInfo);
        jobInfo.setJobName(jobName);
        jobInfo.setJobAddr(jobAddr);
        jobInfo.setJobInfo(jobInfoStr);
        jobInfo.setUrl(url);
        jobInfo.setTime(time);
        jobInfo.setSalaryMin(Float.valueOf(salaryMin));
        jobInfo.setSalaryMax(Float.valueOf(salaryMax));

        // 交给pipeline来处理
        page.putField("jobInfo",jobInfo);
    }

    /**
     * 解析列表页
     *
     * @param page
     */
    private void parseList(Page page) {
        Json json = page.getJson();
        // 获取所有的目标详情url
        List<String> urlList = json.jsonPath("engine_search_result[*].job_href").all();
        for (String s : urlList) {
            Request request = new Request(s);
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("level","detail"); // 标识位
            request.setExtras(map);
            setGetHeaders(request);
            page.addTargetRequest(request);
        }
    }

    public Site getSite() {
        return Site.me();
    }


    /**
     * 添加请求伪装
     *
     * @param request
     */
    public void setGetHeaders(Request request){
        request.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        request.addHeader("Accept-Encoding","gzip, deflate, br");
        request.addHeader("Accept-Language","zh-CN,zh;q=0.9");
        request.addHeader("Cache-Control","max-age=0");
        request.addHeader("Connection","keep-alive");
        request.addHeader("Host","jobs.51job.com");
        request.addHeader("Referer","https://search.51job.com/");
        request.addHeader("Upgrade-Insecure-Requests","1");
        request.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        request.addHeader("Cookie","_uab_collina=162735205034353766732154; guid=12b7d58b5754c9671e042cfed72e6e23; nsearch=jobarea%3D%26%7C%26ord_field%3D%26%7C%26recentSearch0%3D%26%7C%26recentSearch1%3D%26%7C%26recentSearch2%3D%26%7C%26recentSearch3%3D%26%7C%26recentSearch4%3D%26%7C%26collapse_expansion%3D; search=jobarea%7E%60080200%7C%21ord_field%7E%600%7C%21recentSearch0%7E%60080200%A1%FB%A1%FA000000%A1%FB%A1%FA0000%A1%FB%A1%FA32%A1%FB%A1%FA99%A1%FB%A1%FA%A1%FB%A1%FA99%A1%FB%A1%FA99%A1%FB%A1%FA99%A1%FB%A1%FA99%A1%FB%A1%FA9%A1%FB%A1%FA99%A1%FB%A1%FA%A1%FB%A1%FA0%A1%FB%A1%FAJava%BF%AA%B7%A2%A1%FB%A1%FA2%A1%FB%A1%FA1%7C%21; acw_tc=76b20ffa16273520460128704e64a6cf35d46409e3087dfc0883c673442d46; acw_sc__v2=60ff6beec5252ed092fcb6a4a427bc48ffefc9bf; ssxmod_itna=eqfxBDy70QdYq0Kq0dD=wgDSid+kxY5eD7YEFiRDBL74iNDnD8x7YDvIILQYvCYgG0oeLKUYTYG6trfpbvq1jRENQDU4i8DCM2eYTDen=D5xGoDPxDeDADYE6DAqiOD7qDdfhTXtkDbxi3fxiaDGeDeEKODY5DhxDC2mPDwx0Cfx24mA9hO6BCTKy45t0DfxG1a40HeASINU8LmmyhwFSQ4xGdDpMDImdeeQiYDU4ODl92DCF1uEyFTIkkMO5GVW23tW2e/BD3ONiote+xZix485QoP7P45DrPpWxb77aKDDc4GOpDD=; ssxmod_itna2=eqfxBDy70QdYq0Kq0dD=wgDSid+kxY5eD7YEFiD8T648xGXhPqGafWUsh1fx82FjxVfWkDoOV4aqxQbVScD9+8WcWogbq0i7mSFktzK4IXw9loISLOtLZnxQnqjri9Tu999=ZuIeG+3RDQ6pYw3sFAYYCd22I3iI8nhzYkYuSfolnf2U94XUtf2PkW0bpeXX3L5s3+jWpQ=AfgcsAbuOVCFRQM5DkfyeL2DaD7QmD7=DewexD===");
    }
}

~~~
