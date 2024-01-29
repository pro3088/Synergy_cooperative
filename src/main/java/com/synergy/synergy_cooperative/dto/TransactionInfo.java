package com.synergy.synergy_cooperative.dto;

import com.synergy.synergy_cooperative.transaction.enums.Status;

import java.math.BigDecimal;

public class TransactionInfo {
    BigDecimal amount;

    Integer count;

    Status status;

    public TransactionInfo(BigDecimal amount, Integer count, Status status) {
        this.amount = amount;
        this.count = count;
        this.status = status;
    }

    public TransactionInfo(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionInfo(Integer count) {
        this.count = count;
    }

    public TransactionInfo(Status status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
