package org.demchenko.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.demchenko.dto.CreateVisitRequest;
import org.demchenko.dto.PaginatedResponse;
import org.demchenko.service.VisitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/visits")
public class VisitController {
    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<Void> createVisit(@RequestBody @Valid CreateVisitRequest request) {
        visitService.createVisit(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/patients")
    public ResponseEntity<PaginatedResponse> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> search,
            @RequestParam(required = false) List<Long> doctorIds) {

        PaginatedResponse response = visitService.getPatients(page, size, search, doctorIds);
        return ResponseEntity.ok(response);
    }
}

