package com.synergy.synergy_cooperative.bank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;


public class BankDTO {

    private String id;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private Integer accountNumber;

    @NotNull
    @Size(max = 255)
    private String accountName;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(final String accountName) {
        this.accountName = accountName;
    }

}
