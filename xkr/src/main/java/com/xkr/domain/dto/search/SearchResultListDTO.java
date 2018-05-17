package com.xkr.domain.dto.search;


import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/14
 */
public class SearchResultListDTO<T> implements Serializable{

    private static final long serialVersionUID = -509521854290227996L;

    private long totalCount;

    private List<T> searchResultDTO = Lists.newArrayList();

    public SearchResultListDTO() {
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getSearchResultDTO() {
        return searchResultDTO;
    }

    public void setSearchResultDTO(List<T> searchResultDTO) {
        this.searchResultDTO = searchResultDTO;
    }
}
