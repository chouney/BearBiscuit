package com.xkr.service.api;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.xkr.domain.dto.search.BaseIndexDTO;
import com.xkr.domain.dto.search.SearchResultDTO;
import com.xkr.domain.dto.search.SearchResultListDTO;
import com.xkr.util.ArgUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
@Service
public class SearchApiService {

    @Resource(name = "esClient")
    private RestHighLevelClient client;

    public boolean upsertIndex(BaseIndexDTO baseIndexDTO) {
        try {
            final IndexRequest request = new IndexRequest()
                    .index(baseIndexDTO.getIndexName())
                    .type(baseIndexDTO.getTypeName())
                    .id(baseIndexDTO.getIndexKey())
                    .timeout(TimeValue.timeValueMinutes(2))
                    // Map
                    .source(JSON.toJSONString(baseIndexDTO), XContentType.JSON);
            final IndexResponse response = client.index(request);

            if (response.getResult() == DocWriteResponse.Result.CREATED) {
                ArgUtil.assertEquals(response.getVersion(),1L);
            } else if (response.getResult() == DocWriteResponse.Result.UPDATED) {
                ArgUtil.assertNotEquals(response.getVersion(), 1L);
            }
            return true;
        } catch (final IOException e) {
            // TODO: 2018/5/11 log
            e.printStackTrace();
        }
        return false;
    }


    public SearchResultListDTO searchByKeyWordInField(String keyword, Map<String, Float> fieldWeight, Map<String,Object> filterFieldValues,
                                             Pair<Date,Date> rangeDate, String dateKey,
                                             String sortKey, Set<String> hightField, int offset, int size) {
        if (offset < 0 || size < 0) {
            throw new IllegalArgumentException("error argument");
        }
        try {


            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .timeout(TimeValue.timeValueMinutes(2));

            //高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            if(!CollectionUtils.isEmpty(hightField)){
                hightField.forEach(highlightBuilder::field);
                builder.highlighter(highlightBuilder);

            }

            //排序
            if (!StringUtils.isEmpty(sortKey)) {
                builder.sort(sortKey, SortOrder.DESC);
            }
            //分页
            builder.from(offset).size(size);

            //查询详情
//            builder.explain(true);

            //如果字段为空
            if(CollectionUtils.isEmpty(fieldWeight)){
                fieldWeight = ImmutableMap.of(
                        "*",1F
                );
            }


            //设置查询命令
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            //进行条件过滤
            if(!CollectionUtils.isEmpty(filterFieldValues)){
                filterFieldValues.forEach((k,v)-> boolQueryBuilder.filter(QueryBuilders.termQuery(k,v)));
            }
            //进行时间删选
            if(Objects.nonNull(rangeDate) && !StringUtils.isEmpty(dateKey)){

                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(dateKey);
                if(Objects.nonNull(rangeDate.getLeft())){
                    rangeQueryBuilder.gte(rangeDate.getLeft().getTime());
                }
                if(Objects.nonNull(rangeDate.getRight())){
                    rangeQueryBuilder.lte(rangeDate.getRight().getTime());
                }
                boolQueryBuilder.filter(rangeQueryBuilder);
            }

            boolQueryBuilder.should(QueryBuilders.multiMatchQuery(keyword,
                    fieldWeight.keySet().toArray(new String[fieldWeight.size()])).
                    fields(fieldWeight));


            boolQueryBuilder.minimumShouldMatch(1);

            builder.query(boolQueryBuilder);

            SearchRequest request = new SearchRequest()
                    .source(builder);


            SearchResponse response = client.search(request);

            SearchHits hits = response.getHits();

            SearchResultListDTO resultListDTO = new SearchResultListDTO();

            hits.forEach(hit->{
                SearchResultDTO resultDTO = new SearchResultDTO();
                resultDTO.setResultMap(hit.getSourceAsMap());
                Map<String,String> highlightMap = Maps.newHashMap();
                hit.getHighlightFields().forEach((k,v)->{
                    if(v.getFragments().length>0){
                        highlightMap.put(k,v.getFragments()[0].string());
                    }
                });
                resultDTO.setHighLightMap(highlightMap);
                resultListDTO.getSearchResultDTO().add(resultDTO);
            });
            resultListDTO.setTotalCount(hits.getTotalHits());
            return resultListDTO;

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
