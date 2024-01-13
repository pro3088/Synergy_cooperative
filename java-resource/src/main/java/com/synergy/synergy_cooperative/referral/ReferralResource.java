package com.synergy.synergy_cooperative.referral;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value = "/api/referrals", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReferralResource {

    private final ReferralService referralService;

    public ReferralResource(final ReferralService referralService) {
        this.referralService = referralService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ReferralDTO>> getAllReferrals() {
        return ResponseEntity.ok(referralService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ReferralDTO> getReferral(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(referralService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createReferral(@RequestBody @Valid final ReferralDTO referralDTO, @RequestParam(name = "status") final String status) {
        return new ResponseEntity<>(referralService.create(referralDTO, status), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteReferral(@PathVariable(name = "id") final String id) {
        referralService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
