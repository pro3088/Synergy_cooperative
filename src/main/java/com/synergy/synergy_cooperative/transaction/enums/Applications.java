package com.synergy.synergy_cooperative.transaction.enums;

import com.synergy.synergy_cooperative.transaction.TransactionDTO;

import java.util.List;

public class Applications {
    private List<TransactionDTO> transactions;
    private int totalPages;

    public Applications(List<TransactionDTO> transactions, int totalpages) {
        this.transactions = transactions;
        this.totalPages = totalpages;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
