package com.task.demo.service.impl;

import com.task.demo.entity.Account;
import com.task.demo.exception.NotFoundException;
import com.task.demo.exception.TransactionException;
import com.task.demo.repository.AccountRepository;
import com.task.demo.payload.request.CreateAccountRequest;
import com.task.demo.payload.request.TransactionRequest;
import com.task.demo.payload.request.TransferRequest;
import com.task.demo.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        Account account = new Account(request.getAccountNumber(), request.getInitialBalance());
        return accountRepository.save(account);
    }

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> listAccounts() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional
    public Account deposit(String accountNumber, TransactionRequest request) throws NotFoundException {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException(String.format("Account with number %s does not exist", accountNumber));
        }
        boolean lockAcquired = account.getLock().tryLock();
        try {
            if (lockAcquired) {
                account.deposit(request.getAmount());
                return accountRepository.save(account);
            } else {
                throw new TransactionException(String.format("Account with number %s has transaction in progress", accountNumber));
            }
        } finally {
            if (lockAcquired) {
                account.getLock().unlock();
            }
        }
    }

    @Override
    @Transactional
    public Account withdraw(String accountNumber, TransactionRequest request) throws NotFoundException {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new NotFoundException(String.format("Account with number %s does not exist", accountNumber));
        }
        boolean lockAcquired = account.getLock().tryLock();
        try {
            if (lockAcquired) {
                account.withdraw(request.getAmount());
                return accountRepository.save(account);
            } else {
                throw new TransactionException(String.format("Account with number %s has transaction in progress", accountNumber));
            }
        } finally {
            if (lockAcquired) {
                account.getLock().unlock();
            }
        }
    }

    @Transactional
    public void transfer(TransferRequest request) {
        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber());
        Account targetAccount = accountRepository.findByAccountNumber(request.getTargetAccountNumber());

        ReentrantLock firstLock = sourceAccount.getLock();
        ReentrantLock secondLock = targetAccount.getLock();

        if (sourceAccount.getAccountNumber().compareTo(targetAccount.getAccountNumber()) > 0) {
            firstLock = targetAccount.getLock();
            secondLock = sourceAccount.getLock();
        }

        boolean firstLockAcquired = false;
        boolean secondLockAcquired = false;

        try {
            firstLockAcquired = firstLock.tryLock();
            secondLockAcquired = secondLock.tryLock();

            if (!firstLockAcquired || !secondLockAcquired) {
                throw new IllegalStateException("Unable to acquire locks for transfer");
            }

            sourceAccount.withdraw(request.getAmount());
            targetAccount.deposit(request.getAmount());

            accountRepository.save(sourceAccount);
            accountRepository.save(targetAccount);
        } finally {
            if (secondLockAcquired) {
                secondLock.unlock();
            }
            if (firstLockAcquired) {
                firstLock.unlock();
            }
        }
    }
}
