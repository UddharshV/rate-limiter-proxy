package com.uddharsh.ratelimiterproxy.proxy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //This class handles HTTP requests - specialized component
@RequestMapping("/proxy") //Indicates that all the methods of the class are under the /proxy path
public class ProxyController {

    private final ProxyService proxyService;

    //Constructor for dependency injection
    ProxyController(ProxyService proxyService){
        //Every ProxyController needs a ProxyService, and once it's set, it cannot change.
        //Spring will create a ProxyService bean and pass it into this constructor when it creates a ProxyController
        this.proxyService = proxyService;
    }

    @RequestMapping("/**") // /** -> wildcard: catches /proxy/api/users, /proxy/orders, etc. - any path under /proxy
    //Any request to /proxy (GET, POST, etc.) is routed to ProxyController
    public ResponseEntity<String> methodHandler(HttpServletRequest request){
        return proxyService.forwardRequest(request);
    }

}
