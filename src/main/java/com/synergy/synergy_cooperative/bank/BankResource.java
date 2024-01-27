package com.synergy.synergy_cooperative.bank;

import com.synergy.synergy_cooperative.bank.interest.InterestDTO;
import com.synergy.synergy_cooperative.bank.interest.InterestService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;

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
@RequestMapping(value = "/api/banks", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankResource {

    @Autowired
    private BankService bankService;

    @Autowired
    private InterestService interestService;

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
    public ResponseEntity<BankDTO> createBank(@RequestBody @Valid final BankDTO bankDTO) {
        return new ResponseEntity<>(bankService.create(bankDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankDTO> updateBank(@PathVariable(name = "id") final String id,
                                           @RequestBody @Valid final BankDTO bankDTO) {
        return ResponseEntity.ok(bankService.update(id, bankDTO));
    }

    @PutMapping("/interest")
    public ResponseEntity<InterestDTO> updateInterest(@RequestBody @Valid final InterestDTO interestDTO) {
        return ResponseEntity.ok(interestService.setInterest(interestDTO.getInterest()));
    }

    @GetMapping("/interest")
    public ResponseEntity<InterestDTO> getInterest(@RequestBody @Valid final InterestDTO interestDTO) {
        return ResponseEntity.ok(interestService.getInterest());
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteBank(@PathVariable(name = "id") final String id) {
        bankService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
