package com.task.demo.controller;

import com.task.demo.entity.Account;
import com.task.demo.repository.AccountRepository;
import com.task.demo.request.CreateAccountRequest;
import com.task.demo.request.TransactionRequest;
import com.task.demo.request.TransferRequest;
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

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @PostMapping
    public Account createAccount(@RequestBody CreateAccountRequest request) {
        Account account = new Account(request.getAccountNumber(), request.getInitialBalance());
        return accountRepository.save(account);
    }

    @GetMapping("/{accountNumber}")
    public Account getAccount(@PathVariable String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @GetMapping
    public List<Account> listAccounts() {
        return accountRepository.findAll();
    }

    @PostMapping("/{accountNumber}/deposit")
    public Account deposit(@PathVariable String accountNumber, @RequestBody TransactionRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        account.deposit(request.getAmount());
        return accountRepository.save(account);
    }

    @PostMapping("/{accountNumber}/withdraw")
    public Account withdraw(@PathVariable String accountNumber, @RequestBody TransactionRequest request) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        account.withdraw(request.getAmount());
        return accountRepository.save(account);
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferRequest request) {
        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber());
        Account targetAccount = accountRepository.findByAccountNumber(request.getTargetAccountNumber());

        sourceAccount.withdraw(request.getAmount());
        targetAccount.deposit(request.getAmount());

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
    }
}
