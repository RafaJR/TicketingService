package com.cloudbees.trainapi.ticketing.web.controller;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trains")
@Validated
public class ReceiptController {

    @PostMapping("/pursache")
    public ResponseEntity<String> createReceipt(@Valid @RequestBody ReceiptInputDTO receiptInputDTO) {

        return new ResponseEntity<>("Receipt created successfully", HttpStatus.CREATED);
    }
}