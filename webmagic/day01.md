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
