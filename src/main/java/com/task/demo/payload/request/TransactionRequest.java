package com.task.demo.payload.request;

import java.math.BigDecimal;

public class TransactionRequest {
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
