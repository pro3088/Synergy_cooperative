package com.synergy.synergy_cooperative.transaction.shares;

import java.math.BigDecimal;

public class ShareDTO {
    private String id;

    private String user;

    private BigDecimal amount;

    public ShareDTO(String id, String user, BigDecimal amount) {
        this.id = id;
        this.user = user;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
