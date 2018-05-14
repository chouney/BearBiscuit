package com.xkr.domain.dto.search;

import java.util.Map;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/14
 */
public class SearchResultDTO {

    private Map<String,String> highLightMap;

    private Map<String,Object> resultMap;

    public SearchResultDTO() {
    }

    public SearchResultDTO(Map<String, String> highLightMap, Map<String,Object> resultMap) {
        this.highLightMap = highLightMap;
        this.resultMap = resultMap;
    }

    public Map<String, String> getHighLightMap() {
        return highLightMap;
    }

    public void setHighLightMap(Map<String, String> highLightMap) {
        this.highLightMap = highLightMap;
    }

    public Map<String, Object> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, Object> resultMap) {
        this.resultMap = resultMap;
    }
}
