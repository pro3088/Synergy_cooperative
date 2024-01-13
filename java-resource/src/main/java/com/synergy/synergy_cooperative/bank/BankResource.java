package com.synergy.synergy_cooperative.bank;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
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
@RequestMapping(value = "/api/banks", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankResource {

    private final BankService bankService;

    public BankResource(final BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public ResponseEntity<List<BankDTO>> getAllBanks() {
        return ResponseEntity.ok(bankService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankDTO> getBank(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(bankService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createBank(@RequestBody @Valid final BankDTO bankDTO) {
        final String createdId = bankService.create(bankDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateBank(@PathVariable(name = "id") final String id,
                                           @RequestBody @Valid final BankDTO bankDTO) {
        bankService.update(id, bankDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteBank(@PathVariable(name = "id") final String id) {
        bankService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
