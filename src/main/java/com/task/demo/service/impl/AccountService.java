package com.task.demo.service.impl;

import com.task.demo.entity.Account;
import com.task.demo.exception.NotFoundException;
import com.task.demo.exception.TransactionException;
import com.task.demo.repository.AccountRepository;
import com.task.demo.payload.request.CreateAccountRequest;
import com.task.demo.payload.request.TransactionRequest;
import com.task.demo.payload.request.TransferRequest;
import com.task.demo.service.IAccountService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private Map<String, ReentrantLock> lockMap = new HashMap<>();
    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        String accountNumber = request.getAccountNumber();
        Account existedAccount = accountRepository.findByAccountNumber(accountNumber);
        if (existedAccount != null) {
            throw new TransactionException(String.format("Account with number %s already exists", accountNumber));
        }
        Account account = new Account(accountNumber, request.getInitialBalance());
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

    @Transactional
    public Account deposit(String accountNumber, TransactionRequest request) throws NotFoundException, BadRequestException {
        if(accountNumber == null) {
            throw new BadRequestException("Account number must not be empty");
        }
        if(request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Deposit amount must be positive number");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            accountNotFoundException(accountNumber);
        }

        ReentrantLock lock = getLock(accountNumber);
        if (!lock.tryLock()) {
            transactionInProgressException(accountNumber);
        }

        try {
            account.deposit(request.getAmount());
            return accountRepository.save(account);
        } finally {
            lock.unlock();
        }
    }

    private ReentrantLock getLock(String accountNumber) {
        return lockMap.computeIfAbsent(accountNumber, key -> new ReentrantLock());
    }

    @Override
    @Transactional
    public Account withdraw(String accountNumber, TransactionRequest request) throws NotFoundException, BadRequestException {
        if(accountNumber == null) {
            throw new BadRequestException("Account number must not be empty");
        }
        if(request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Withdrawal amount must be positive number");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            accountNotFoundException(accountNumber);
        }
        ReentrantLock lock = getLock(accountNumber);
        if (!lock.tryLock()) {
            transactionInProgressException(accountNumber);
        }

        try {
            account.withdraw(request.getAmount());
            return accountRepository.save(account);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void transfer(TransferRequest request) throws NotFoundException, BadRequestException {
        String sourceAccountNumber = request.getSourceAccountNumber();
        String targetAccountNumber = request.getTargetAccountNumber();
        if(sourceAccountNumber == null || targetAccountNumber == null) {
            throw new BadRequestException("Source and target account number must not be empty");
        }
        if(request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transfer amount must be positive number");
        }

        if (sourceAccountNumber.equals(targetAccountNumber)) {
            throw new TransactionException("Invalid request parameters");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
        Account targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);

        if (sourceAccount == null || targetAccount == null) {
            accountNotFoundException(sourceAccount == null ? sourceAccountNumber : targetAccountNumber);
        }

        ReentrantLock sourceLock = getLock(sourceAccountNumber);
        ReentrantLock targetLock = getLock(targetAccountNumber);
        boolean sourceLockAcquired = false;
        boolean targetLockAcquired = false;

        try {
            sourceLockAcquired = sourceLock.tryLock();
            targetLockAcquired = targetLock.tryLock();

            if (!sourceLockAcquired || !targetLockAcquired) {
                transactionInProgressException(sourceLockAcquired ? sourceAccountNumber : targetAccountNumber);
            }

            sourceAccount.withdraw(request.getAmount());
            targetAccount.deposit(request.getAmount());

            accountRepository.save(sourceAccount);
            accountRepository.save(targetAccount);
        } finally {
            if (targetLockAcquired) {
                targetLock.unlock();
            }
            if (sourceLockAcquired) {
                sourceLock.unlock();
            }
        }
    }

    private static void accountNotFoundException(String accountNumber) throws NotFoundException {
        throw new NotFoundException(String.format("Account with number %s does not exist", accountNumber));
    }

    private static void transactionInProgressException(String accountNumber) {
        throw new TransactionException(String.format("Account with number %s has a transaction in progress", accountNumber));
    }
}
