package com.task.demo.controller;

import com.task.demo.entity.Account;
import com.task.demo.exception.NotFoundException;
import com.task.demo.exception.TransactionException;
import com.task.demo.payload.request.CreateAccountRequest;
import com.task.demo.payload.request.TransactionRequest;
import com.task.demo.payload.request.TransferRequest;
import com.task.demo.payload.response.ResponseObject;
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
    public ResponseObject<Account> createAccount(@RequestBody CreateAccountRequest request) {
        return handleAccountOperation(() -> accountService.createAccount(request), HttpStatus.CREATED);
    }

    @GetMapping("/{accountNumber}")
    public ResponseObject<Account> getAccount(@PathVariable String accountNumber) {
        return handleAccountOperation(() -> accountService.getAccount(accountNumber), HttpStatus.OK);
    }

    @GetMapping
    public ResponseObject<List<Account>> listAccounts() {
        return ResponseObject.<List<Account>>builder()
                .withBody(accountService.listAccounts())
                .withStatus(HttpStatus.OK)
                .build();
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseObject<Account> deposit(@PathVariable String accountNumber, @RequestBody TransactionRequest request) {
        return handleAccountOperation(() -> accountService.deposit(accountNumber, request), HttpStatus.OK);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseObject<Account> withdraw(@PathVariable String accountNumber, @RequestBody TransactionRequest request) {
        return handleAccountOperation(() -> accountService.withdraw(accountNumber, request), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseObject<String> transfer(@RequestBody TransferRequest request) {
        try {
            accountService.transfer(request);
            return ResponseObject.<String>builder()
                    .withBody("Transfer successfully executed")
                    .withStatus(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    private <T> ResponseObject<T> buildErrorResponse(Exception e) {
        if (e instanceof NotFoundException) {
            return ResponseObject.<T>builder()
                    .withBody((T) e.getMessage())
                    .withStatus(HttpStatus.NOT_FOUND)
                    .build();
        } else if (e instanceof TransactionException) {
            return ResponseObject.<T>builder()
                    .withBody((T) e.getMessage())
                    .withStatus(HttpStatus.CONFLICT)
                    .build();
        } else {
            return ResponseObject.<T>builder()
                    .withBody((T) e.getMessage())
                    .withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private <T> ResponseObject<T> handleAccountOperation(Operation operation, HttpStatus successStatus) {
        try {
            Account result = operation.execute();
            return ResponseObject.<T>builder()
                    .withBody((T) result)
                    .withStatus(successStatus)
                    .build();
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }
}
