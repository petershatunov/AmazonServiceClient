package com.sellics.assignment.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.sellics.assignment.model.StatisticsResult;
import com.sellics.assignment.service.AmazonServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.math.BigDecimal;

@RestController
public class RestEndpoint {

    @Autowired
    private AmazonServiceClient amazonServiceClient;

    @GetMapping("/estimate")
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")},
            fallbackMethod = "hystrixFall")
    public StatisticsResult estimate(@PathParam("keyword") String keyword) {
        BigDecimal statistics = amazonServiceClient.getStatistics(keyword);
        return new StatisticsResult(keyword, statistics);
    }

    private StatisticsResult hystrixFall(String keyword) {
        return new StatisticsResult("Sorry, service is down. Please try again later", BigDecimal.valueOf(-1));
    }

}
