package com.uddharsh.ratelimiterproxy.proxy;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service //specialized component
public class ProxyService {

    private static final Logger logger = LoggerFactory.getLogger(ProxyService.class);

    private final RestClient restClient;

    ProxyService(RestClient restClient){ //Spring's dependency injection will look for a bean of type RestClient
        this.restClient = restClient;
    }

    ResponseEntity<String> forwardRequest(HttpServletRequest request){
        /*This is where we:
            *Read method and URI
            * Strip "/proxy" from the URI
            * Use RestClient to call the upstream
            * Return the upstream response
        */
        String upstreamPath = request.getRequestURI().replaceFirst("/proxy", ""); // /api/users

        logger.info("{} -> {}", request.getMethod(), upstreamPath);

        //Wire the request to RestClient
        ResponseEntity<String> upstreamResponseEntity = restClient.method(HttpMethod.valueOf(request.getMethod())) //Pass the HTTP method
                .uri(upstreamPath) //Pass the upstream path
                .retrieve() //Execute the request
                .toEntity(String.class); //converts response to ResponseEntity - which is the wrapper: ResponseEntity<String>
        //ResponseEntity<String> with status and body from the upstream
        return upstreamResponseEntity;
    }

}
