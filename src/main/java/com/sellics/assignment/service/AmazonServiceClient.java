package com.sellics.assignment.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class AmazonServiceClient {

    @Autowired
    private AmazonRestClient amazonRestClient;

    /**
     * Performs request + additional char to an autocompletion service
     * @param query - word / combination of words which is a search query
     * @return percentage of an autocompletion
     */
    public BigDecimal getStatistics(String query) {

        final String allChars = "abcdefghijklmnopqrstuvwxyz0123456789";

        ArrayList<CompletableFuture<BigDecimal>> completableFutures = new ArrayList<>();

        for (int i = 0; i < allChars.length(); i++) {
            String extendedQuery = query + " " + allChars.charAt(i);

            CompletableFuture<BigDecimal> future = CompletableFuture
                    .supplyAsync(() -> getPercentageOfRequest(query, extendedQuery));

            completableFutures.add(future);
        }

        return completableFutures.stream()
                .map(CompletableFuture::join)
                .reduce(BigDecimal::add)
                .map(bigDecimal -> bigDecimal
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(allChars.length()), 0, BigDecimal.ROUND_HALF_UP)
                )
                .orElse(BigDecimal.ZERO);

    }

    /**
     * A common autocompletion usually returns maximum 10 results
     * The method counts how many results were actually returned
     * and divides them to a maximum.
     * Subtracting a 1 shows that this is actually a keyword duplicated in the response.
     * @param query
     * @param extendedQuery
     * @return percentage of one query
     */
    private BigDecimal getPercentageOfRequest(String query, String extendedQuery) {
        final BigDecimal maxResultPerRequest = BigDecimal.valueOf(10);
        String autocompletion = amazonRestClient.getAutocompletion(extendedQuery);
        BigDecimal matches = BigDecimal.valueOf(StringUtils.countMatches(autocompletion, query));

        if (matches.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

        if (matches.compareTo(maxResultPerRequest) >= 1) return BigDecimal.ONE;

        return matches
                .subtract(BigDecimal.ONE)
                .divide(maxResultPerRequest, 2, BigDecimal.ROUND_HALF_UP);
    }
}
