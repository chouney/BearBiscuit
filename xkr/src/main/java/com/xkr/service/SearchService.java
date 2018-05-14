package com.xkr.service;

import com.xkr.service.api.SearchApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/14
 */
@Service
public class SearchService {

    @Autowired
    private SearchApiService searchApiService;


}
