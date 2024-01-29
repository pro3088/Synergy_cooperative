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
    private String accountNumber;

    @NotNull
    @Size(max = 255)
    private String accountName;

    private boolean company;

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

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(final String accountName) {
        this.accountName = accountName;
    }

    public boolean isCompany() {
        return company;
    }

    public void setCompany(boolean company) {
        this.company = company;
    }
}
