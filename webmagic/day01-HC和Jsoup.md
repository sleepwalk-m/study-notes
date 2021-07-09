# 1. HttpClient使用
</br>maven依赖
~~~xml
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.13</version>
        </dependency>
~~~


## 1.1 HttpClient发起GET请求
~~~java
/**
     * get请求
     *
     * @throws IOException
     */
    @Test
    public void testHCget() throws IOException {
        // 1. 创建hc对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 2. 创建一个get对象 发get请求
        HttpGet httpGet = new HttpGet("http://www.heihe.gov.cn/seach.jsp?wbtreeid=1001&searchScope=0&currentnum=1&newskeycode2=6KGl5YWF5Yy755aX5L%2Bd6Zmp403");
        // 3. 发送请求
        CloseableHttpResponse response = httpClient.execute(httpGet);
        // 4. 拿到响应的数据
        StatusLine statusLine = response.getStatusLine();
        System.out.println("statusLine = " + statusLine);
        int statusCode = statusLine.getStatusCode();
        System.out.println("statusCode = " + statusCode);

        // 响应的html源码
        HttpEntity entity = response.getEntity();
        String s = EntityUtils.toString(entity, "utf-8");
        System.out.println("s = " + s);

        // 5. 关流
        response.close();
        httpClient.close();

    }
~~~
## 1.2 HttpClient发起POST请求
~~~java
/**
     * POST 请求带参数
     *
     * @throws Exception
     */
    @Test
    public void testPost() throws Exception{
        // 1. 创建HC对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 2. 创建post
        HttpPost httpPost = new HttpPost("http://search.gd.gov.cn/api/search/site");
        // 3. 添加参数 httpentity
        // 两种添加参数的方式选其一（可能还有更多方式，暂未研究）
        // 3.1 使用HttpString的方式 httpentity的实现类
        String param = "{\"page\":\"1\",\"keywords\":\"补充医疗保险\",\"sort\":\"smart\",\"site_id\":\"750001\",\"range\":\"site\",\"position\":\"title\",\"recommand\":1,\"gdbsDivision\":\"440700\",\"service_area\":750}";
        HttpEntity httpEntity = new StringEntity(param);
        

        // 3.2 使用表单的方式
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("page","1"));
        list.add(new BasicNameValuePair("keywords","补充医疗保险"));
        list.add(new BasicNameValuePair("sort","smart"));
        list.add(new BasicNameValuePair("site_id","750001"));
        list.add(new BasicNameValuePair("range","site"));
        list.add(new BasicNameValuePair("position","title"));
        list.add(new BasicNameValuePair("recommand","1"));
        list.add(new BasicNameValuePair("gdbsDivision","440700"));
        list.add(new BasicNameValuePair("service_area","750"));
        HttpEntity httpEntity = new UrlEncodedFormEntity(list,"utf-8");
        // 塞入hc
        httpPost.setEntity(httpEntity);
        // 4. 发起请求
        CloseableHttpResponse response = httpClient.execute(httpPost);
        // 5. 拿到响应数据
        String s = EntityUtils.toString(response.getEntity(), "utf-8");
        JSONObject jsonObject = JSONObject.parseObject(s);
        System.out.println("jsonObject = " + jsonObject);


        // 5. 关流
        response.close();
        httpClient.close();
        }
~~~
## 1.3 使用HttpClient连接池控制
~~~java
/**
     * HC连接池 控制
     *
     */
    @Test
    public void testPoolingHttpClientConnectionManager() throws IOException {
        // 1. 创建连接池对象
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // 2. 创建HC对象,这里要用custom()
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
        // 3. 创建GET请求
        HttpGet httpGet = new HttpGet("http://www.heihe.gov.cn/seach.jsp?wbtreeid=1001&searchScope=0&currentnum=1&newskeycode2=6KGl5YWF5Yy755aX5L%2Bd6Zmp403");
        // 4. 执行
        CloseableHttpResponse response = httpClient.execute(httpGet);
        // 5. 获取响应体
        String html = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println("html = " + html);

        // 6. 关流
        response.close();
        // 注意： httpClient 不需要关流，因为会由连接池回收，当然关流了这个HC也就被关了，连接池就没有其他的HC对象

    }
~~~
