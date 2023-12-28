package com.synergy.synergy_cooperative.transaction;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionResource {

    @Autowired
    TransactionService transactionService;

    @GetMapping("/all")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/total/investment")
    public ResponseEntity<BigDecimal> getTotalInvestment() {
        return ResponseEntity.ok(transactionService.getTotalInvestment());
    }

    @GetMapping("/total/investment/{userId}")
    public ResponseEntity<BigDecimal> getInvestmentByUser(@PathVariable(name = "userId") final String id) {
        return ResponseEntity.ok(transactionService.getInvestmentByUser(id));
    }

    @GetMapping("/total/loan")
    public ResponseEntity<BigDecimal> getTotalLoan() {
        return ResponseEntity.ok(transactionService.getTotalLoans());
    }

    @GetMapping("/total/loan/{userId}")
    public ResponseEntity<BigDecimal> getLoanByUser(@PathVariable(name = "userId") final String id) {
        return ResponseEntity.ok(transactionService.getLoanByUser(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(transactionService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createTransaction(
            @RequestBody @Valid final TransactionDTO transactionDTO) {
        final String createdId = transactionService.create(transactionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final TransactionDTO transactionDTO) {
        transactionService.update(id, transactionDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTransaction(@PathVariable(name = "id") final String id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
