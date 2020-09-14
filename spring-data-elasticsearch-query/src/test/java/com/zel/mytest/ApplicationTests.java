package com.zel.mytest;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.spring.springboot.Application;
import org.spring.springboot.domain.City;
import org.spring.springboot.service.CityQueryService;
import org.spring.springboot.service.impl.RestClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
class ApplicationTests {

    @Autowired
    private RestClientServiceImpl restClientServiceImpl;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @MockBean
    private CityQueryService cityQueryService;

    @Test
    void testMatchAllSearch(){
        SearchRequest searchRequest = new SearchRequest("province");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            for(SearchHit hit:hits){
                String sourceAsString = hit.getSourceAsString();

                System.out.println(sourceAsString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    void testMatchSearch(){
        SearchRequest searchRequest = new SearchRequest("province");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        /*boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("description","城市").boost(2));
        boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("name","城市"));*/
        boolQueryBuilder.should(QueryBuilders.multiMatchQuery("的城市","description","name"));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
//        searchSourceBuilder.sort("id",SortOrder.ASC);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            for(SearchHit hit:hits){
                String sourceAsString = hit.getSourceAsString();

                System.out.println(sourceAsString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    void testWildcardSearch(){
        SearchRequest searchRequest = new SearchRequest("province");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        String content = String.format("*%s*", "城市");
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("description.keyword",content).boost(2));
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("name.keyword",content));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
//        searchSourceBuilder.sort("id",SortOrder.ASC);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            for(SearchHit hit:hits){
                String sourceAsString = hit.getSourceAsString();
                System.out.println(sourceAsString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Rollback
    void test(){
        City city = new City();
        city.setId(14L);
        city.setName("testname");
        city.setDescription("testdescription");
        city.setScore(10);
        Mockito.when(cityQueryService.testMock(Mockito.any())).thenReturn(city);
        restClientServiceImpl.saveCity(city);

    }
}
