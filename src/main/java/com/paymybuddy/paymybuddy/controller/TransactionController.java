package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;


    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable int id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Iterable<Transaction>> getAllTransactionByAnUserID(@PathVariable int userId) {
        Iterable<Transaction> tx = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(tx);
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        return transactionService.addTransaction(transaction)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
    //
}
