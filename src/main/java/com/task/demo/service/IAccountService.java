package com.task.demo.service;

import com.task.demo.entity.Account;
import com.task.demo.exception.NotFoundException;
import com.task.demo.payload.request.CreateAccountRequest;
import com.task.demo.payload.request.TransactionRequest;
import com.task.demo.payload.request.TransferRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IAccountService {
    @Transactional
    Account createAccount(CreateAccountRequest request);

    Account getAccount(String accountNumber);

    List<Account> listAccounts();

    @Transactional
    Account deposit(String accountNumber, TransactionRequest request) throws NotFoundException, BadRequestException;

    @Transactional
    Account withdraw(String accountNumber, TransactionRequest request) throws NotFoundException, BadRequestException;

    @Transactional
    void transfer(TransferRequest request) throws NotFoundException, BadRequestException;
}
