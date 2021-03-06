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
     * 1. ???????????? bulk
     */
    @Test
    public void testBulk() throws IOException {
        //??????bulkrequest???????????????????????????
        BulkRequest bulkRequest = new BulkRequest();

       /* # 1. ??????1?????????
        # 2. ??????6?????????
        # 3. ??????3????????? ????????? ????????????*/

        //??????????????????
        //1. ??????1?????????
        DeleteRequest deleteRequest = new DeleteRequest("person", "1");
        bulkRequest.add(deleteRequest);

        //2. ??????6?????????
        Map map = new HashMap();
        map.put("name", "??????");
        IndexRequest indexRequest = new IndexRequest("person").id("6").source(map);
        bulkRequest.add(indexRequest);

        Map map2 = new HashMap();
        map2.put("name", "??????");
        //3. ??????3????????? ????????? ????????????
        UpdateRequest updateReqeust = new UpdateRequest("person", "3").doc(map2);
        bulkRequest.add(updateReqeust);

        //??????????????????
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        RestStatus status = response.status();
        System.out.println(status);
    }


    /**
     * ????????????
     */
    @Test
    public void importData() throws IOException {


        /*
        *    "specStr" : """{"????????????":"16G","??????":"??????3G"}""",
        *    var jsonStr = "{username:"zhs",age:19}"
        *    var json = {username:"zhs",age:19}
        *    Ajax SpringMVC  @RequestBody
        * */

        //1.?????????????????????mysql
        List<Goods> goodsList = goodsMapper.findAll();
        for (Goods goods : goodsList) {
            IndexRequest in = new IndexRequest("goods");
            in.id(goods.getId().toString()).source(JSON.toJSONString(goods),XContentType.JSON);
            client.index(in,RequestOptions.DEFAULT);
        }

        /*//2.bulk??????
        BulkRequest bulkRequest = new BulkRequest();

        //2.1 ??????goodsList?????????IndexRequest????????????
        for (Goods goods : goodsList) {
            //2.2 ??????spec???????????? Map?????????   specStr:{}
            //goods.setSpec(JSON.parseObject(goods.getSpecStr(),Map.class));

            String specStr = goods.getSpecStr();
            //???json?????????????????????Map??????
            Map map = JSON.parseObject(specStr, Map.class);
            //??????spec map
            goods.setSpec(map);
            //???goods???????????????json?????????
            String data = JSON.toJSONString(goods);//map --> {}
            IndexRequest indexRequest = new IndexRequest("goods");
            indexRequest.id(goods.getId() + "").source(data, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(response.status());*/
    }

    /**
     * ????????????
     * 1. matchAll
     * 2. ????????????????????????Goods??????????????????List???
     * 3. ?????????????????????10???
     */
    @Test
    public void testMatchAll() throws IOException {

        // ???????????? ??????????????????API ?????????API ??????????????? ????????????
        // ?????????????????? ???????????????????????????SDK (??????????????????jar)   ????????????   IM    Map
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


        /*//2. ??????????????????????????????????????????????????????
        SearchRequest searchRequest = new SearchRequest("goods");
        //4. ???????????????????????????SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //6. ????????????
        QueryBuilder query = QueryBuilders.matchAllQuery();//??????????????????
        //5. ??????????????????
        sourceBuilder.query(query);

        //3. ??????????????????????????? SearchSourceBuilder
        searchRequest.source(sourceBuilder);

        // 8 . ??????????????????
        sourceBuilder.from(0);
        sourceBuilder.size(3);

        //1. ??????,??????????????????
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //7. ?????????????????? SearchHits
        SearchHits searchHits = searchResponse.getHits();
        //7.1 ??????????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);


        List<Goods> goodsList = new ArrayList<>();
        //7.2 ??????Hits??????  ??????
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            //??????json????????????????????????
            String sourceAsString = hit.getSourceAsString();
            //??????java??????
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);

        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }*/

    }


    /**
     * termQuery:????????????
     */
    @Test
    public void testTermQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder ssb = new SearchSourceBuilder();
        QueryBuilder query = QueryBuilders.termQuery("brandName","??????");
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

        QueryBuilder query = QueryBuilders.termQuery("title", "??????");//term????????????
        sourceBulider.query(query);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //???????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //??????java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }*/
    }


    /**
     * matchQuery:??????????????????
     */
    @Test
    public void testMatchQuery() throws IOException {

        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "????????????");
        query.operator(Operator.AND);//?????????
        sourceBulider.query(query);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //???????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //??????java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * ????????????:WildcardQuery
     */
    @Test
    public void testWildcardQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        WildcardQueryBuilder query = QueryBuilders.wildcardQuery("title", "???*");

        sourceBulider.query(query);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //???????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //??????java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * ????????????:regexpQuery
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
        //???????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //??????java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * ????????????:perfixQuery
     */
    @Test
    public void testPrefixQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        PrefixQueryBuilder query = QueryBuilders.prefixQuery("brandName", "???");

        sourceBulider.query(query);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //???????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //??????java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * 1. ???????????????rangeQuery
     * 2. ??????
     */
    @Test
    public void testRangeQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();


        //????????????
        RangeQueryBuilder query = QueryBuilders.rangeQuery("price");

        //????????????
        query.gte(2000);
        //????????????
        query.lte(3000);

        sourceBulider.query(query);

        //??????
        sourceBulider.sort("price", SortOrder.DESC);

        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //???????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //??????java
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
        QueryStringQueryBuilder query = QueryBuilders.queryStringQuery("????????????").field("title").field("categoryName").field("brandName").defaultOperator(Operator.AND);


        sourceBulider.query(query);


        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //???????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //??????java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * ???????????????boolQuery
     * 1. ?????????????????????:??????
     * 2. ???????????????????????????
     * 3. ??????????????????2000-3000
     */
    @Test
    public void testBoolQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();


        //1.??????boolQuery
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        //2.????????????????????????
        //2.1 ?????????????????????:??????
        QueryBuilder termQuery = QueryBuilders.termQuery("brandName", "??????");
        query.must(termQuery);

        //2.2. ???????????????????????????
        QueryBuilder matchQuery = QueryBuilders.matchQuery("title", "??????");
        query.filter(matchQuery);

        //2.3 ??????????????????2000-3000
        QueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
        ((RangeQueryBuilder) rangeQuery).gte(2000);
        ((RangeQueryBuilder) rangeQuery).lte(3000);
        query.filter(rangeQuery);

        //3.??????boolQuery??????

        sourceBulider.query(query);
        searchRequest.source(sourceBulider);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //???????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //??????java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }
    }


    /**
     * ???????????????????????????????????????
     * 1. ??????title?????????????????????
     * 2. ??????????????????
     */
    @Test
    public void testAggQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        // 1. ??????title?????????????????????
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "??????");

        sourceBulider.query(query);

        // 2. ??????????????????
        /*
        ?????????
            1. ?????????????????????????????????????????????
            2. ???????????????
         */
        AggregationBuilder agg = AggregationBuilders.terms("goods_brands").field("brandName").size(100);
        sourceBulider.aggregation(agg);
        searchRequest.source(sourceBulider);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = searchResponse.getHits();
        //???????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //??????java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }

        // ??????????????????
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
     * ???????????????
     * 1. ????????????
     * * ????????????
     * * ??????
     * * ??????
     * 2. ????????????????????????????????????????????????
     */
    @Test
    public void testHighLightQuery() throws IOException {


        SearchRequest searchRequest = new SearchRequest("goods");

        SearchSourceBuilder sourceBulider = new SearchSourceBuilder();

        // 1. ??????title?????????????????????
        MatchQueryBuilder query = QueryBuilders.matchQuery("title", "??????");

        sourceBulider.query(query);

        //????????????
        HighlightBuilder highlighter = new HighlightBuilder();
        //???????????????
        highlighter.field("title");
        highlighter.preTags("<font color='red'>");
        highlighter.postTags("</font>");


        sourceBulider.highlighter(highlighter);


        // 2. ??????????????????
        /*
        ?????????
            1. ?????????????????????????????????????????????
            2. ???????????????
         */
        AggregationBuilder agg = AggregationBuilders.terms("goods_brands").field("brandName").size(100);
        sourceBulider.aggregation(agg);


        searchRequest.source(sourceBulider);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);


        SearchHits searchHits = searchResponse.getHits();
        //???????????????
        long value = searchHits.getTotalHits().value;
        System.out.println("???????????????" + value);

        List<Goods> goodsList = new ArrayList<>();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();

            //??????java
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);

            // ???????????????????????????goods??????title
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField HighlightField = highlightFields.get("title");
            Text[] fragments = HighlightField.fragments();
            //??????
            goods.setTitle(fragments[0].toString());

            goodsList.add(goods);
        }

        for (Goods goods : goodsList) {
            System.out.println(goods);
        }

        // ??????????????????
        // ???????????? ?????????????????????????????????
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
