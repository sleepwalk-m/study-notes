package com.itheima.elasticsearchdemo;

import com.alibaba.fastjson.JSON;
import com.itheima.domain.Person;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ElasticsearchDemoApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() throws IOException {
        /*//1.创建ES客户端对象
        RestHighLevelClient client1 = new RestHighLevelClient(RestClient.builder(
           new HttpHost(
                   "192.168.110.81",
                   9200,
                   "http"
           )
        ));*/
        System.out.println(client);


        IndicesClient indices = client.indices();
        CreateIndexRequest aaa = new CreateIndexRequest("xxxx");
        // HTTP 请求头  请求体 ..
        CreateIndexResponse createIndexResponse = indices.create(aaa, RequestOptions.DEFAULT);
        System.out.println("createIndexResponse = " + createIndexResponse);
    }


    /**
     * 添加索引
     */
    @Test
    public void addIndex() throws IOException {
        //1.使用client获取操作索引的对象
        IndicesClient indicesClient = client.indices();
        //2.具体操作，获取返回值
        CreateIndexRequest createRequest = new CreateIndexRequest("itheima");
        CreateIndexResponse response = indicesClient.create(createRequest, RequestOptions.DEFAULT);

        //3.根据返回值判断结果
        System.out.println(response.isAcknowledged());
    }


    /**
     * 添加索引
     */
    @Test
    public void addIndexAndMapping() throws IOException {
        //1.使用client获取操作索引的对象
        IndicesClient indicesClient = client.indices();
        //2.具体操作，获取返回值
        CreateIndexRequest createRequest = new CreateIndexRequest("itcast0001");
        //2.1 设置mappings
        String mapping = "{\n" +
                "      \"properties\" : {\n" +
                "        \"address\" : {\n" +
                "          \"type\" : \"text\",\n" +
                "          \"analyzer\" : \"ik_max_word\"\n" +
                "        },\n" +
                "        \"age\" : {\n" +
                "          \"type\" : \"long\"\n" +
                "        },\n" +
                "        \"name\" : {\n" +
                "          \"type\" : \"keyword\"\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        createRequest.mapping(mapping, XContentType.JSON);
        CreateIndexResponse response = indicesClient.create(createRequest, RequestOptions.DEFAULT);

        //3.根据返回值判断结果
        System.out.println(response.isAcknowledged());
    }


    /**
     * 查询索引
     */
    @Test
    public void queryIndex() throws IOException {
        IndicesClient indices = client.indices();

        GetIndexRequest getReqeust = new GetIndexRequest("person2");
        GetIndexResponse response = indices.get(getReqeust, RequestOptions.DEFAULT);
        System.out.println("13w32342");
        //获取结果
        Map<String, MappingMetaData> mappings = response.getMappings();
        for (String key : mappings.keySet()) {
            System.out.println(key + ":" + mappings.get(key).getSourceAsMap());
        }
    }


    /**
     * 删除索引
     */
    @Test
    public void deleteIndex() throws IOException {
        IndicesClient indices = client.indices();
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest("itheima");
        AcknowledgedResponse response = indices.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(response.isAcknowledged());
    }

    /**
     * 判断索引是否存在
     */
    @Test
    public void existIndex() throws IOException {
        IndicesClient indices = client.indices();
        GetIndexRequest getRequest = new GetIndexRequest("itcast");
        boolean exists = indices.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }


    /**
     * 添加文档,使用map作为数据
     * Map  List<Map>
     * 对象  List<对象>
     * 多条任务的
     */
    @Test
    public void addDoc() throws IOException {
        //数据对象，map
        Map data = new HashMap();
        data.put("address", "北京昌平");
        data.put("name", "大胖");
        data.put("age", 20);


        //1.获取操作文档的对象
        IndexRequest request = new IndexRequest("itcast").id("1").source(data);
        //添加数据，获取结果
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        //打印响应结果
        System.out.println(response.getId());
    }


    /**
     * 添加文档,使用对象作为数据
     */
    @Test
    public void addDoc2() throws IOException {
        //数据对象，javaObject
        Person p = new Person();
        p.setId("2");
        p.setName("小胖2222");
        p.setAge(30);
        p.setAddress("陕西西安");

        // 我们不能直接把一个Bean对象保存到ES中
        // 需要先把Bean转成Map 或者 JSON
        // 将对象转为json  HTTP协议  把参数 发送到ES  Bean  网络传输 必须要序列化
        // FastJSON  Jackson
        String data = JSON.toJSONString(p);

        //1.获取操作文档的对象
        IndexRequest request = new IndexRequest("itcast").id(p.getId()).source(data, XContentType.JSON);
        //添加数据，获取结果
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        //打印响应结果
        System.out.println(response.getId());
    }


    /**
     * 修改文档：添加文档时，如果id存在则修改，id不存在则添加
     */
    @Test
    public void updateDoc() throws IOException {
        // 我们进行修改都是根据Bean进行修改的  其实还是根据id进行修改的
        // 分页  多条件的组合查询 ...
    }


    /**
     * 根据id查询文档
     */
    @Test
    public void findDocById() throws IOException {
        GetRequest getReqeust = new GetRequest("itcast", "1");
        //getReqeust.id("1");
        GetResponse response = client.get(getReqeust, RequestOptions.DEFAULT);
        //获取数据对应的json
        System.out.println(response.getSourceAsString());
    }


    /**
     * 根据id删除文档
     */
    @Test
    public void delDoc() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("itcast", "1");
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response.getId());
    }
}
