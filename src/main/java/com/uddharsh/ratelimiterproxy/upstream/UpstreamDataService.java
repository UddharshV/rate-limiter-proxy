package com.uddharsh.ratelimiterproxy.upstream;

import com.uddharsh.ratelimiterproxy.upstream.models.Order;
import com.uddharsh.ratelimiterproxy.upstream.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpstreamDataService {
    private final List<User> users;
    private final List<Order> orders;


    public UpstreamDataService() {
        this.users = loadUsers();
        this.orders = loadOrders();
    }

    List<User> loadUsers(){
        List<User> userList = new ArrayList<>();

        User user1 = new User();
        user1.setId("1");
        user1.setName("Alice");
        userList.add(user1);

        User user2 = new User();
        user2.setId("2");
        user2.setName("Bob");
        userList.add(user2);

        return userList;
    }
    List<Order> loadOrders() {
        List<Order> orderList = new ArrayList<>();

        Order order1 = new Order();
        order1.setId("1");
        order1.setDescription("First Test Order");
        order1.setStatus("In Stock");
        orderList.add(order1);

        Order order2 = new Order();
        order2.setId("2");
        order2.setDescription("Second Test Order");
        order2.setStatus("Sold Out");
        orderList.add(order2);

        return orderList;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
