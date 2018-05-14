package com.xkr.core.config;
import com.xkr.core.ESClientFactory;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

//使用javaConfig方式配置
@Configuration
public class ESConfig {

    @Value("${httpHost.host}")
    private String host;

    @Value("${httpHost.port}")
    private int port;

    @Value("${httpHost.schema}")
    private String schema;

    @Value("${esclient.connectNum}")
    private Integer connectNum;

    @Value("${esclient.connectPerRoute}")
    private Integer connectPerRoute;



    @Bean
    public HttpHost httpHost(){
        return new HttpHost(host,port,schema);
    }

    @Bean(initMethod="init")
    public ESClientFactory getFactory(){
        return ESClientFactory.
                build(httpHost(), connectNum, connectPerRoute);
    }

    @Bean(name = "esClient")
    public RestHighLevelClient getRHLClient(){
        return getFactory().getRhlClient();
    }
}