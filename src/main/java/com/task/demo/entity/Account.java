package com.task.demo.entity;

import com.task.demo.exception.TransactionException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @Transient
    private final ReentrantLock lock = new ReentrantLock();

    public Account() {
    }

    public Account(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (this.balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
        } else {
            throw new TransactionException("Insufficient balance");
        }
    }
}
