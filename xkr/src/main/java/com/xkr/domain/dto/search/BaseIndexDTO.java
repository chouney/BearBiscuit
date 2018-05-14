package com.xkr.domain.dto.search;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public abstract class BaseIndexDTO {

    //索引名
    private transient String indexName;
    //类型名
    private transient String typeName;

    public BaseIndexDTO(String indexName, String typeName) {
        this.indexName = indexName;
        this.typeName = typeName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getTypeName() {
        return typeName;
    }

    public abstract String getIndexKey() ;
}
