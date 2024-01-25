package com.synergy.synergy_cooperative.docs;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;

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
@RequestMapping(value = "/api/docs", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocsResource {

    private final DocsService docsService;

    public DocsResource(final DocsService docsService) {
        this.docsService = docsService;
    }

    @GetMapping
    public ResponseEntity<List<DocsDTO>> getAllDocs() {
        return ResponseEntity.ok(docsService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocsDTO> getDoc(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(docsService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createDoc(@RequestBody @Valid final DocsDTO docsDTO) {
        final String createdId = docsService.create(docsDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateDoc(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final DocsDTO docsDTO) {
        docsService.update(id, docsDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDoc(@PathVariable(name = "id") final String id) {
        docsService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
