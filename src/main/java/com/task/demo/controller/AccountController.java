package com.task.demo.controller;

import com.task.demo.entity.Account;
import com.task.demo.exception.NotFoundException;
import com.task.demo.exception.TransactionException;
import com.task.demo.payload.request.CreateAccountRequest;
import com.task.demo.payload.request.TransactionRequest;
import com.task.demo.payload.request.TransferRequest;
import com.task.demo.service.Operation;
import com.task.demo.service.IAccountService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        return handleAccountOperation(() -> accountService.createAccount(request), HttpStatus.CREATED);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        return handleAccountOperation(() -> accountService.getAccount(accountNumber), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Account>> listAccounts() {
        return new ResponseEntity<>(accountService.listAccounts(), HttpStatus.OK);
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable String accountNumber, @RequestBody TransactionRequest request) {
        return handleAccountOperation(() -> accountService.deposit(accountNumber, request), HttpStatus.OK);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable String accountNumber, @RequestBody TransactionRequest request) {
        return handleAccountOperation(() -> accountService.withdraw(accountNumber, request), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        try {
            accountService.transfer(request);
            return new ResponseEntity("Transfer successfully executed", HttpStatus.OK);
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    private <T> ResponseEntity<T> handleAccountOperation(Operation operation, HttpStatus successStatus) {
        try {
            Account result = operation.execute();
            return new ResponseEntity(result, successStatus);
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    private <T> ResponseEntity<T> buildErrorResponse(Exception e) {
        if (e instanceof NotFoundException) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } else if (e instanceof TransactionException) {
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
        } else if (e instanceof BadRequestException) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
