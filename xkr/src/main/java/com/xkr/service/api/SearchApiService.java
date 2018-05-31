package com.xkr.service.api;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.xkr.domain.dto.search.BaseIndexDTO;
import com.xkr.domain.dto.search.SearchResultListDTO;
import com.xkr.util.ArgUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


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
                ArgUtil.assertEquals(response.getVersion(), 1L);
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

    public boolean bulkUpdateIndex(String typeName, List<Long> docIds,Map<String,Object> updateMap) {
        if (CollectionUtils.isEmpty(docIds) || CollectionUtils.isEmpty(updateMap)) {
            return false;
        }
        BulkRequest request = new BulkRequest();
        try {
            docIds.forEach(docId -> {
                UpdateRequest updateRequest = new UpdateRequest("xkr", typeName, String.valueOf(docId))
                        .doc(updateMap);
                request.add(updateRequest);
            });

            BulkResponse responses = client.bulk(request);
            return RestStatus.OK.equals(responses.status());
        } catch (IOException e) {
            // TODO: 2018/5/11 log
            e.printStackTrace();
        }
        return false;
    }

    public boolean bulkUpdateIndexStatus(String typeName, List<Long> docIds, Integer status) {
        if (CollectionUtils.isEmpty(docIds) || Objects.isNull(status)) {
            return false;
        }
        BulkRequest request = new BulkRequest();
        try {
            docIds.forEach(docId -> {
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("status", status);
                if ("comment".equals(typeName) || "resource".equals(typeName)) {
                    jsonMap.put("updateTime", new Date());
                }
                UpdateRequest updateRequest = new UpdateRequest("xkr", typeName, String.valueOf(docId))
                        .doc(jsonMap);
                request.add(updateRequest);
            });

            BulkResponse responses = client.bulk(request);
            return RestStatus.OK.equals(responses.status());
        } catch (IOException e) {
            // TODO: 2018/5/11 log
            e.printStackTrace();
        }
        return false;
    }

    public <T extends BaseIndexDTO> void getAndBuildIndexDTOByIndexId(T targetDTO, String docId) {
        if (!BaseIndexDTO.class.isAssignableFrom(targetDTO.getClass()) || StringUtils.isEmpty(docId)) {
            throw new IllegalArgumentException("error argument");
        }
        try {
            GetRequest getRequest = new GetRequest(targetDTO.getIndexName(), targetDTO.getTypeName(), docId);
            GetResponse getResponse = client.get(getRequest);
            if (getResponse.isExists()) {
                BeanUtils.populate(targetDTO, getResponse.getSourceAsMap());
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public <T extends BaseIndexDTO> SearchResultListDTO<T> searchByFilterField(Class<T> resultIndexDTO, Map<String, Object> filterFieldValues,
                                                                                     Pair<Date, Date> rangeDate, String dateKey,
                                                                                     String sortKey, int offset, int size) {
        if (offset < 0 || size < 0) {
            throw new IllegalArgumentException("error argument");
        }
        try {


            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .timeout(TimeValue.timeValueMinutes(2));

            //排序
            if (!StringUtils.isEmpty(sortKey)) {
                builder.sort(sortKey, SortOrder.DESC);
            }
            //分页
            builder.from(offset).size(size);

            //设置查询命令
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            //进行条件过滤
            if (!CollectionUtils.isEmpty(filterFieldValues)) {
                filterFieldValues.forEach((k, v) -> {
                    if (StringUtils.isEmpty(k) || Objects.isNull(v)) {
                        return;
                    }
                    if (Collection.class.isAssignableFrom(v.getClass())) {
                        boolQueryBuilder.filter(QueryBuilders.termsQuery(k, v));
                    } else {
                        boolQueryBuilder.filter(QueryBuilders.termQuery(k, v));
                    }
                });
            }

            //进行时间删选
            if (Objects.nonNull(rangeDate) && !StringUtils.isEmpty(dateKey)) {

                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(dateKey);
                if (Objects.nonNull(rangeDate.getLeft())) {
                    rangeQueryBuilder.gte(rangeDate.getLeft().getTime());
                }
                if (Objects.nonNull(rangeDate.getRight())) {
                    rangeQueryBuilder.lte(rangeDate.getRight().getTime());
                }
                boolQueryBuilder.filter(rangeQueryBuilder);
            }

            builder.query(boolQueryBuilder);

            T targetObject = org.springframework.beans.BeanUtils.instantiate(resultIndexDTO);


            SearchRequest request = new SearchRequest(targetObject.getIndexKey())
                    .types(targetObject.getTypeName())
                    .source(builder);


            SearchResponse response = client.search(request);

            SearchHits hits = response.getHits();

            SearchResultListDTO<T> resultListDTO = new SearchResultListDTO<>();

            buildSearchResultListDTO(resultListDTO, targetObject, hits);

            return resultListDTO;

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    public <T extends BaseIndexDTO> SearchResultListDTO<T> searchByKeyWordInField(Class<T> resultIndexDTO, String keyword, Map<String, Float> fieldWeight, Map<String, Object> filterFieldValues,
                                                                                  Pair<Date, Date> rangeDate, String dateKey,
                                                                                  String sortKey, Set<String> hightField, int offset, int size) {
        if (offset < 0 || size < 0) {
            throw new IllegalArgumentException("error argument");
        }
        try {


            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .timeout(TimeValue.timeValueMinutes(2));

            //高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            if (!CollectionUtils.isEmpty(hightField)) {
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
            if (CollectionUtils.isEmpty(fieldWeight)) {
                fieldWeight = ImmutableMap.of(
                        "*", 1F
                );
            }


            //设置查询命令
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            //进行条件过滤
            if (!CollectionUtils.isEmpty(filterFieldValues)) {
                filterFieldValues.forEach((k, v) -> {
                    if (StringUtils.isEmpty(k) || Objects.isNull(v)) {
                        return;
                    }
                    if (Collection.class.isAssignableFrom(v.getClass())) {
                        boolQueryBuilder.filter(QueryBuilders.termsQuery(k, v));
                    } else {
                        boolQueryBuilder.filter(QueryBuilders.termQuery(k, v));
                    }
                });
            }


            //进行时间删选
            if (Objects.nonNull(rangeDate) && !StringUtils.isEmpty(dateKey)) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(dateKey);
                if (Objects.nonNull(rangeDate.getLeft())) {
                    rangeQueryBuilder.gte(rangeDate.getLeft().getTime());
                }
                if (Objects.nonNull(rangeDate.getRight())) {
                    rangeQueryBuilder.lte(rangeDate.getRight().getTime());
                }
                boolQueryBuilder.filter(rangeQueryBuilder);
            }
            //关键词搜索
            if (!StringUtils.isEmpty(keyword)) {
                boolQueryBuilder.should(QueryBuilders.multiMatchQuery(keyword,
                        fieldWeight.keySet().toArray(new String[fieldWeight.size()])).
                        fields(fieldWeight));
            }

            T targetObject = org.springframework.beans.BeanUtils.instantiate(resultIndexDTO);


            boolQueryBuilder.minimumShouldMatch(1);

            builder.query(boolQueryBuilder);

            SearchRequest request = new SearchRequest(targetObject.getIndexName())
                    .types(targetObject.getTypeName())
                    .source(builder);


            SearchResponse response = client.search(request);

            SearchHits hits = response.getHits();

            SearchResultListDTO<T> resultListDTO = new SearchResultListDTO<>();

            buildSearchResultListDTO(resultListDTO, targetObject, hits);

            return resultListDTO;

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void buildSearchResultListDTO(SearchResultListDTO<T> resultListDTO, T targetObject, SearchHits hits) {
        hits.forEach(hit -> {
            try {
                BeanUtils.populate(targetObject, hit.getSourceAsMap());
                hit.getHighlightFields().forEach((k, v) -> {
                    if (v.getFragments().length > 0) {
                        try {
                            BeanUtils.setProperty(targetObject, k, v.getFragments()[0].string());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
                resultListDTO.getSearchResultDTO().add(targetObject);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                // TODO: 2018/5/11 log

            }
        });
        resultListDTO.setTotalCount(hits.getTotalHits());

    }
}
