package com.synergy.synergy_cooperative.transaction;

import com.synergy.synergy_cooperative.dto.TransactionInfo;
import com.synergy.synergy_cooperative.transaction.shares.ShareService;
import com.synergy.synergy_cooperative.transaction.enums.Applications;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Autowired
    ShareService shareService;


    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/total/{userId}/gain")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionInfo> getTotalEarningsById(@PathVariable(name = "userId") final String id){
        return ResponseEntity.ok(shareService.getTotalEarning(id));
    }

    @GetMapping("/total/{type}/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionInfo> getAmountByUserAndType(@PathVariable(name = "userId") final String id, @PathVariable(name = "type") final String type) {
        return ResponseEntity.ok(transactionService.getAmountByUserAndType(id, type));
    }

    @GetMapping("/total/{type}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionInfo> getTotalByType(@PathVariable(name = "type") final String type) {
        return ResponseEntity.ok(transactionService.getTotalByType(type));
    }

    @GetMapping("/total/{type}/{userId}/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<TransactionInfo> getTransactionCountByUser(@PathVariable(name = "userId") final String id, @PathVariable(name = "type") final String type) {
        return ResponseEntity.ok(transactionService.getTransactionCountByUser(id, type));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(transactionService.get(id));
    }

    @GetMapping("/{id}/deposit/status")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionInfo> getDepositStatusByUser(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(transactionService.getDepositStatusByUser(id));
    }

    @GetMapping("/{id}/deposit")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionInfo> getDepositById(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(transactionService.getDepositByUser(id));
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
    public ResponseEntity<TransactionDTO> createTransaction(
            @RequestBody @Valid final TransactionDTO transactionDTO) {
        return new ResponseEntity<>(transactionService.create(transactionDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final TransactionDTO transactionDTO) {
        log.info("Request to update Transaction with id {}", id);
        return new ResponseEntity<>(transactionService.update(id, transactionDTO), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTransaction(@PathVariable(name = "id") final String id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
