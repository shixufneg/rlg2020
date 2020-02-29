package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

public interface CartService {
    ServerResponse list(User user);

    ServerResponse add(Integer productId, Integer count, Integer type, User u);

    ServerResponse update(Integer productId, Integer count,Integer type, User u);

    ServerResponse delect(Integer productId, User u);

    ServerResponse delectAll(User u);

    ServerResponse getCartProductCoun(User u);

    ServerResponse checked(Integer productId, Integer type,User u);
}
