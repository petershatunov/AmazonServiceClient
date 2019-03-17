package com.sellics.assignment.model;

import java.math.BigDecimal;

public class StatisticsResult {

    private String keyword;
    private BigDecimal score;

    public StatisticsResult(String keyword, BigDecimal score) {
        this.keyword = keyword;
        this.score = score;
    }

    public String getKeyword() {
        return keyword;
    }

    public BigDecimal getScore() {
        return score;
    }
}
