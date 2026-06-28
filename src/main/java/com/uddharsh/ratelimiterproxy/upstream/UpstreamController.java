package com.uddharsh.ratelimiterproxy.upstream;

import com.uddharsh.ratelimiterproxy.upstream.models.Order;
import com.uddharsh.ratelimiterproxy.upstream.models.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController //Handles HTTP requests
@RequestMapping("/api")
public class UpstreamController {

    private final UpstreamDataService upstreamDataService;

    //Constructor-Inject: UpstreamDataService
    UpstreamController(UpstreamDataService dataService){
        this.upstreamDataService = dataService;
    }

    @GetMapping("/users")
    List<User> usersHandler(){
        return upstreamDataService.getUsers();
    }
    @GetMapping("/orders")
    List<Order> ordersHandler(){
        return upstreamDataService.getOrders();
    }


}
