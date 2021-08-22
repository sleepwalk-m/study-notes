package com.itheima.elasticsearchdemo2;

import com.alibaba.fastjson.JSON;
import com.itheima.elasticsearchdemo2.domain.Goods;
import com.itheima.elasticsearchdemo2.mapper.GoodsMapper;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ElasticsearchDemo2ApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 1. 批量操作 bulk
     */
    @Test
    public void testBulk() throws IOException {
        //创建bulkrequest对象，整合所有操作
        BulkRequest bulkRequest = new BulkRequest();

       /* # 1. 删除1号记录
        # 2. 添加6号记录
        # 3. 修改3号记录 名称为 “三号”*/

        //添加对应操作
        //1. 删除1号记录
        DeleteRequest deleteRequest = new DeleteRequest("person", "1");
        bulkRequest.add(deleteRequest);

        //2. 添加6号记录
        Map map = new HashMap();
        map.put("name", "六号");
        IndexRequest indexRequest = new IndexRequest("person").id("6").source(map);
        bulkRequest.add(indexRequest);

        Map map2 = new HashMap();
        map2.put("name", "三号");
        //3. 修改3号记录 名称为 “三号”
        UpdateRequest updateReqeust = new UpdateRequest("person", "3").doc(map2);
        bulkRequest.add(updateReqeust);

        //执行批量操作
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        RestStatus status = response.status();
        System.out.println(status);
    }


    /**
     * 批量导入
     */
    @Test
    public void importData() throws IOException {


        /*
        *    "specStr" : """{"机身内存":"16G","网络":"联通3G"}""",
        *    var jsonStr = "{username:"zhs",age:19}"
        *    var json = {username:"zhs",age:19}
        *    Ajax SpringMVC  @RequestBody
        * */

        //1.查询所有数据，mysql
        List<Goods> goodsList = goodsMapper.findAll();
        for (Goods goods : goodsList) {
            IndexRequest in = new IndexRequest("goods");
            in.id(goods.getId().toString()).source(JSON.toJSONString(goods),XContentType.JSON);
            client.index(in,RequestOptions.DEFAULT);
        }

        /*//2.bulk导入
        BulkRequest bulkRequest = new BulkRequest();

        //2.1 循环goodsList，创建IndexRequest添加数据
        for (Goods goods : goodsList) {
            //2.2 设置spec规格信息 Map的数据   specStr:{}
            //goods.setSpec(JSON.parseObject(goods.getSpecStr(),Map.class));

            String specStr = goods.getSpecStr();
            //将json格式字符串转为Map集合
            Map map = JSON.parseObject(specStr, Map.class);
            //设置spec map
            goods.setSpec(map);
            //将goods对象转换为json字符串
            String data = JSON.toJSONString(goods);//map --> {}
            IndexRequest indexRequest = new IndexRequest("goods");
            indexRequest.id(goods.getId() + "").source(data, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(response.status());*/
    }

    /**
     * 查询所有
     * 1. matchAll
     * 2. 将查询结果封装为Goods对象，装载到List中
     * 3. 分页。默认显示10条
     */
    @Test
    public void testMatchAll() throws IOException {

        // 凭经验写 对于一些新的API 陌生的API 都要猜着写 多去尝试
        // 在实际开发中 我们会大量的去集成SDK (第三方写好的jar)   短信发送   IM    Map
        SearchRequest resquest = new SearchRequest("goods");
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        ssb.from(20);
        ssb.size(100);
        resquest.source(ssb);
        SearchResponse search = client.search(resquest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        for (SearchHit hit : hits) {
            System.out.println("hit.getSourceAsString() = " + hit.getSourceAsString());
        }
        System.out.println("search = " + search.getHits().getTotalHits());


        /*//2. 构建查询请求对象，指定查询的索引名称
        SearchRequest searchRequest = new SearchRequest("goods");
        //4. 创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //6. 查询条件
        QueryBuilder query = QueryBuilders.matchAllQuery();//查询所有文档
        //5. 指定查询条件
        sourceBuilder.query(query);

        //3. 添加查询条件构建器 SearchSourceBuilder
        searchRequest.source(sourceBuilder);

        // 8 . 添加分页信息
        sourceBuilder.from(0);
        sourceBuilder.size(3);

        //1. 查询,获取查询结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //7. 获取命中对象 SearchHits
        SearchHits searchHits = searchResponse.getHits();
        //7.1 获取总记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);


        List<Goods> goodsList = new ArrayList<>();
        //7.2 获取Hits数据  数组
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            //获取json字符串格式的数据
            String sourceAsString = hit.getSourceAsString();
            //转为java对象
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);

        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }*/

    }


    /**
     * termQuery:词条查询
     */
    @Test
    public void testTermQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        QueryBuilder query = QueryBuilders.termQuery("brandName","华为");
        ssb.query(query);
        ssb.size(20);

        searchRequest.source(ssb);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }





        /*SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        QueryBuilder query = QueryBuilders.termQuery("title", "华为");//term词条查询
        sourceBulider.query(query);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //转为java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }*/
    }


    /**
     * matchQuery:词条分词查询
     */
    @Test
    public void testMatchQuery() throws IOException {

        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "华为手机");
        query.operator(Operator.AND);//求交集
        sourceBulider.query(query);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //转为java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * 模糊查询:WildcardQuery
     */
    @Test
    public void testWildcardQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        WildcardQueryBuilder query = QueryBuilders.wildcardQuery("title", "华*");

        sourceBulider.query(query);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //转为java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * 模糊查询:regexpQuery
     */
    @Test
    public void testRegexpQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        RegexpQueryBuilder query = QueryBuilders.regexpQuery("title", "\\w+(.)*");

        sourceBulider.query(query);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //转为java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * 模糊查询:perfixQuery
     */
    @Test
    public void testPrefixQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        PrefixQueryBuilder query = QueryBuilders.prefixQuery("brandName", "三");

        sourceBulider.query(query);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //转为java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * 1. 范围查询：rangeQuery
     * 2. 排序
     */
    @Test
    public void testRangeQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();


        //范围查询
        RangeQueryBuilder query = QueryBuilders.rangeQuery("price");

        //指定下限
        query.gte(2000);
        //指定上限
        query.lte(3000);

        sourceBulider.query(query);

        //排序
        sourceBulider.sort("price", SortOrder.DESC);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //转为java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * queryString
     */
    @Test
    public void testQueryStringQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();


        //queryString
        QueryStringQueryBuilder query = QueryBuilders.queryStringQuery("华为手机").field("title").field("categoryName").field("brandName").defaultOperator(Operator.AND);


        sourceBulider.query(query);


        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //转为java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * 布尔查询：boolQuery
     * 1. 查询品牌名称为:华为
     * 2. 查询标题包含：手机
     * 3. 查询价格在：2000-3000
     */
    @Test
    public void testBoolQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();


        //1.构建boolQuery
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        //2.构建各个查询条件
        //2.1 查询品牌名称为:华为
        QueryBuilder termQuery = QueryBuilders.termQuery("brandName", "华为");
        query.must(termQuery);

        //2.2. 查询标题包含：手机
        QueryBuilder matchQuery = QueryBuilders.matchQuery("title", "手机");
        query.filter(matchQuery);

        //2.3 查询价格在：2000-3000
        QueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
        ((RangeQueryBuilder) rangeQuery).gte(2000);
        ((RangeQueryBuilder) rangeQuery).lte(3000);
        query.filter(rangeQuery);

        //3.使用boolQuery连接

        sourceBulider.query(query);
        searchRequest.source(sourceBulider);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //转为java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * 聚合查询：桶聚合，分组查询
     * 1. 查询title包含手机的数据
     * 2. 查询品牌列表
     */
    @Test
    public void testAggQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        // 1. 查询title包含手机的数据
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "手机");

        sourceBulider.query(query);

        // 2. 查询品牌列表
        /*
        参数：
            1. 自定义的名称，将来用于获取数据
            2. 分组的字段
         */
        AggregationBuilder agg = AggregationBuilders.terms("goods_brands").field("brandName").size(100);
        sourceBulider.aggregation(agg);
        searchRequest.source(sourceBulider);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        //获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //转为java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }

        // 获取聚合结果
        Aggregations aggregations = searchResponse.getAggregations();

        Map<String, Aggregation> aggregationMap = aggregations.asMap();

        //System.out.println(aggregationMap);
        Terms goods_brands = (Terms) aggregationMap.get("goods_brands");

        List<? extends Terms.Bucket> buckets = goods_brands.getBuckets();

        List brands = new ArrayList();
        for (Terms.Bucket bucket : buckets) {
            Object key = bucket.getKey();
            brands.add(key);
        }

        for (Object brand : brands) {
            System.out.println(brand);
        }

    }


    /**
     * 高亮查询：
     * 1. 设置高亮
     * * 高亮字段
     * * 前缀
     * * 后缀
     * 2. 将高亮了的字段数据，替换原有数据
     */
    @Test
    public void testHighLightQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        // 1. 查询title包含手机的数据
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "手机");

        sourceBulider.query(query);

        //设置高亮
        HighlightBuilder highlighter = new HighlightBuilder();
        //设置三要素
        highlighter.field("title");
        highlighter.preTags("<font color='red'>");
        highlighter.postTags("</font>");


        sourceBulider.highlighter(highlighter);


        // 2. 查询品牌列表
        /*
        参数：
            1. 自定义的名称，将来用于获取数据
            2. 分组的字段
         */
        AggregationBuilder agg = AggregationBuilders.terms("goods_brands").field("brandName").size(100);
        sourceBulider.aggregation(agg);


        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //获取记录数
        long value = searchHits.getTotalHits().value;
        System.out.println("总记录数：" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //转为java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            // 获取高亮结果，替换goods中的title
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField HighlightField = highlightFields.get("title");
            Text[] fragments = HighlightField.fragments();
            //替换
            goods.setTitle(fragments[0].toString());

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }

        // 获取聚合结果
        // 代码仓库 构建一个自己的代码仓库
        Aggregations aggregations = searchResponse.getAggregations();

        Map<String, Aggregation> aggregationMap = aggregations.asMap();

        //System.out.println(aggregationMap);
        Terms goods_brands = (Terms) aggregationMap.get("goods_brands");

        List<? extends Terms.Bucket> buckets = goods_brands.getBuckets();

        List brands = new ArrayList();
        for (Terms.Bucket bucket : buckets) {
            Object key = bucket.getKey();
            brands.add(key);
        }

        for (Object brand : brands) {
            System.out.println(brand);
        }

    }


}
