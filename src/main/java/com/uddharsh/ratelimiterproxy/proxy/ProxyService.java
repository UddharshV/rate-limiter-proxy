package com.uddharsh.ratelimiterproxy.proxy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProxyService {

    ResponseEntity<String> forwardRequest(HttpServletRequest request){
        System.out.println(request.getMethod() + request.getRequestURI());
        return ResponseEntity.ok("forwarded");
    }

}
