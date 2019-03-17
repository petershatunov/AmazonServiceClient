package com.sellics.assignment.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "amazon-rest-client")
public interface AmazonRestClient {

    @GetMapping(value = "/search/complete?method=completion&q={query}&search-alias=aps&client=amazon-search-ui&mkt=1")
    String getAutocompletion(@PathVariable("query") String query);

}
