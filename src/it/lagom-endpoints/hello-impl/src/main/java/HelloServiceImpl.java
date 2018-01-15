package com.lightbend.rp.test.lagomendpoints.impl;

public class HelloServiceImpl implements HelloService {
    @Override
    public ServiceCall<NotUsed, String> hello(String id) {
        return request -> {
            return "Hello, " + id;
        }
    }
}
