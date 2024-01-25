package com.synergy.synergy_cooperative.transaction;

import io.swagger.models.auth.In;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionResource {

    protected static Logger log = LoggerFactory.getLogger(TransactionResource.class);

    @Autowired
    TransactionService transactionService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/total/investment")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BigDecimal> getTotalInvestment() {
        return ResponseEntity.ok(transactionService.getTotalInvestment());
    }

    @GetMapping("/total/investment/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BigDecimal> getInvestmentByUser(@PathVariable(name = "userId") final String id) {
        return ResponseEntity.ok(transactionService.getInvestmentByUser(id));
    }

    @GetMapping("/total/loan")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BigDecimal> getTotalLoan() {
        return ResponseEntity.ok(transactionService.getTotalLoans());
    }

    @GetMapping("/total/loan/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BigDecimal> getLoanByUser(@PathVariable(name = "userId") final String id) {
        return ResponseEntity.ok(transactionService.getLoanByUser(id));
    }

    @GetMapping("/total/withdrawn")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BigDecimal> getTotalWithdrawn() {
        return ResponseEntity.ok(transactionService.getTotalWithdrawn());
    }

    @GetMapping("/total/investment/{userId}/{type}/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Integer> getTransactionsCountByUser(@PathVariable(name = "userId") final String id, @PathVariable(name = "type") final String type) {
        return ResponseEntity.ok(transactionService.getTransactionsCountByUser(id, type));
    }

    @GetMapping("/total/loan/{userId}/info")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionDTO> getTotalLoanByUser(@PathVariable(name = "userId") final String id) {
        return ResponseEntity.ok(transactionService.getLoanTotal(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(transactionService.get(id));
    }

    @GetMapping("/{id}/applications")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Applications> getApplicationsByUser(@PathVariable(name = "id") final String id,
                                                                @RequestParam(name = "offset") final int offset,
                                                                @RequestParam(name = "limit") final int limit,
                                                        @RequestParam(name = "pageSize") final int pageSize) {
        final Applications applications = transactionService.getApplicationsByUser(offset, limit, id, pageSize);
        return new ResponseEntity<>(applications, HttpStatus.ACCEPTED);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> createTransaction(
            @RequestBody @Valid final TransactionDTO transactionDTO) {
        final String createdId = transactionService.create(transactionDTO);
        log.info("Created transaction with id, {}", createdId);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> updateTransaction(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final TransactionDTO transactionDTO) {
        transactionService.update(id, transactionDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTransaction(@PathVariable(name = "id") final String id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
