package com.task.demo.controller;

import com.task.demo.entity.Account;
import com.task.demo.exception.NotFoundException;
import com.task.demo.exception.TransactionException;
import com.task.demo.payload.request.CreateAccountRequest;
import com.task.demo.payload.request.TransactionRequest;
import com.task.demo.payload.request.TransferRequest;
import com.task.demo.payload.response.ResponseEntity;
import com.task.demo.service.Operation;
import com.task.demo.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final IAccountService accountService;

    @Autowired
    public AccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        return handleAccountOperation(() -> accountService.createAccount(request), HttpStatus.CREATED);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> getAccount(@PathVariable String accountNumber) {
        return handleAccountOperation(() -> accountService.getAccount(accountNumber), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Account>> listAccounts() {
        return ResponseEntity.<List<Account>>builder()
                .withBody(accountService.listAccounts())
                .withStatus(HttpStatus.OK)
                .build();
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<?> deposit(@PathVariable String accountNumber, @RequestBody TransactionRequest request) {
        return handleAccountOperation(() -> accountService.deposit(accountNumber, request), HttpStatus.OK);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String accountNumber, @RequestBody TransactionRequest request) {
        return handleAccountOperation(() -> accountService.withdraw(accountNumber, request), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        try {
            accountService.transfer(request);
            return ResponseEntity.<String>builder()
                    .withBody("Transfer successfully executed")
                    .withStatus(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    private ResponseEntity<?> buildErrorResponse(Exception e) {
        if (e instanceof NotFoundException) {
            return ResponseEntity.<String>builder()
                    .withBody(e.getMessage())
                    .withStatus(HttpStatus.NOT_FOUND)
                    .build();
        } else if (e instanceof TransactionException) {
            return ResponseEntity.<String>builder()
                    .withBody(e.getMessage())
                    .withStatus(HttpStatus.CONFLICT)
                    .build();
        } else {
            return ResponseEntity.<String>builder()
                    .withBody(e.getMessage())
                    .withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private ResponseEntity<?> handleAccountOperation(Operation operation, HttpStatus successStatus) {
        try {
            Account result = operation.execute();
            return ResponseEntity.<Account>builder()
                    .withBody(result)
                    .withStatus(successStatus)
                    .build();
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }
}
