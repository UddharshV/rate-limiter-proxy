package com.uddharsh.ratelimiterproxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration //Indicates to Spring that this class is used as a place to define beans
public class AppConfig {

    @Value("${upstream.base-url}") //Reads the configuration property from application.properties
    private String upstreamBaseUrl; //Puts the value read into this field

    @Bean
    RestClient restClient(){ //Bean of type RestClient; At startup, Spring will call this method once
        return RestClient.builder() //Returns a RestClient.Builder object, not a RestClient - configuration workspace where we set options step by step
                .baseUrl(upstreamBaseUrl) //option that we want to set (server/destination URL)
                .build(); //Builds a RestClient with .baseUrl(upstreamBaseUrl)
        //NOTE: .builder() is mutable - allows changes of internal settings over time.
        //.build() is meant to be a stable, shared client.
    }
}
