package com.task.demo;

import com.task.demo.entity.Account;
import com.task.demo.repository.AccountRepository;
import com.task.demo.request.CreateAccountRequest;
import com.task.demo.request.TransactionRequest;
import com.task.demo.request.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void createAccount() {
		CreateAccountRequest request = new CreateAccountRequest();
		request.setAccountNumber("99999");
		request.setInitialBalance(BigDecimal.valueOf(5000));

		ResponseEntity<Account> response = restTemplate.postForEntity("/api/accounts", request, Account.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getAccountNumber()).isEqualTo("99999");
		assertThat(response.getBody().getBalance()).isEqualByComparingTo(BigDecimal.valueOf(5000));
	}

	@Test
	void depositFunds() {
		Account account = accountRepository.findByAccountNumber("12345");
		TransactionRequest request = new TransactionRequest();
		request.setAmount(BigDecimal.valueOf(200));

		ResponseEntity<Account> response = restTemplate.postForEntity("/api/accounts/12345/deposit", request, Account.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getBalance()).isEqualByComparingTo(account.getBalance().add(BigDecimal.valueOf(200)));
	}

	@Test
	void withdrawFunds() {
		Account account = accountRepository.findByAccountNumber("12345");
		TransactionRequest request = new TransactionRequest();
		request.setAmount(BigDecimal.valueOf(100));

		ResponseEntity<Account> response = restTemplate.postForEntity("/api/accounts/12345/withdraw", request, Account.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getBalance()).isEqualByComparingTo(account.getBalance().subtract(BigDecimal.valueOf(100)));
	}

	@Test
	void transferFunds() {
		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setSourceAccountNumber("12345");
		transferRequest.setTargetAccountNumber("67890");
		transferRequest.setAmount(BigDecimal.valueOf(500));

		restTemplate.postForEntity("/api/accounts/transfer", transferRequest, Void.class);

		Account sourceAccount = accountRepository.findByAccountNumber("12345");
		Account targetAccount = accountRepository.findByAccountNumber("67890");

		assertThat(sourceAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000 - 500));
		assertThat(targetAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(2000 + 500));
	}
}