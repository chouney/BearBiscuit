package com.xkr.domain.dto.search;


import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/14
 */
public class SearchResultListDTO {

    private long totalCount;

    private List<SearchResultDTO> searchResultDTO = Lists.newArrayList();

    public SearchResultListDTO() {
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<SearchResultDTO> getSearchResultDTO() {
        return searchResultDTO;
    }

    public void setSearchResultDTO(List<SearchResultDTO> searchResultDTO) {
        this.searchResultDTO = searchResultDTO;
    }
}
