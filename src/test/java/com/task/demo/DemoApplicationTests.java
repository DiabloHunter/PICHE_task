package com.task.demo;

import com.task.demo.entity.Account;
import com.task.demo.payload.request.CreateAccountRequest;
import com.task.demo.payload.request.TransactionRequest;
import com.task.demo.payload.request.TransferRequest;
import com.task.demo.payload.response.ResponseObject;
import com.task.demo.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private AccountRepository accountRepository;

	@BeforeEach
	void setUp() {
		accountRepository.deleteAll();
	}

	@Test
	void createAccount() {
		CreateAccountRequest request = new CreateAccountRequest();
		request.setAccountNumber("99999");
		request.setInitialBalance(BigDecimal.valueOf(5000));

		ResponseEntity<ResponseObject<Account>> response = restTemplate.exchange(
				"/api/accounts",
				HttpMethod.POST,
				new HttpEntity<>(request),
				new ParameterizedTypeReference<>() {
				}
		);

		assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();

		Account account = response.getBody().getBody();
		assertThat(account.getAccountNumber()).isEqualTo("99999");
		assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(5000));
	}

	@Test
	void getAccount() {
		Account account = new Account("12345", BigDecimal.valueOf(1000));
		accountRepository.save(account);

		ResponseEntity<ResponseObject<Account>> response = restTemplate.exchange(
				"/api/accounts/12345",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<>() {
				}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		ResponseObject<Account> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getBody().getAccountNumber()).isEqualTo("12345");
		assertThat(body.getBody().getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
	}

	@Test
	void listAccounts() {
		Account account1 = new Account("12345", BigDecimal.valueOf(1000));
		Account account2 = new Account("67890", BigDecimal.valueOf(2000));
		accountRepository.saveAll(List.of(account1, account2));

		ResponseEntity<ResponseObject<List<Account>>> response = restTemplate.exchange(
				"/api/accounts",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<>() {
				}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		ResponseObject<List<Account>> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getBody()).hasSize(2);
	}

	@Test
	void depositFunds() {
		Account account = new Account("12345", BigDecimal.valueOf(1000));
		accountRepository.save(account);

		TransactionRequest request = new TransactionRequest();
		request.setAmount(BigDecimal.valueOf(200));

		ResponseEntity<ResponseObject<Account>> response = restTemplate.exchange(
				"/api/accounts/12345/deposit",
				HttpMethod.POST,
				new HttpEntity<>(request),
				new ParameterizedTypeReference<>() {
				}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		ResponseObject<Account> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getBody().getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1200));
	}

	@Test
	void withdrawFunds() {
		Account account = new Account("12345", BigDecimal.valueOf(1000));
		accountRepository.save(account);

		TransactionRequest request = new TransactionRequest();
		request.setAmount(BigDecimal.valueOf(100));

		ResponseEntity<ResponseObject<Account>> response = restTemplate.exchange(
				"/api/accounts/12345/withdraw",
				HttpMethod.POST,
				new HttpEntity<>(request),
				new ParameterizedTypeReference<>() {
				}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		ResponseObject<Account> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getBody().getBalance()).isEqualByComparingTo(BigDecimal.valueOf(900));
	}

	@Test
	void transferFunds() {
		Account sourceAccount = new Account("12345", BigDecimal.valueOf(1000));
		Account targetAccount = new Account("67890", BigDecimal.valueOf(2000));
		accountRepository.saveAll(List.of(sourceAccount, targetAccount));

		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setSourceAccountNumber("12345");
		transferRequest.setTargetAccountNumber("67890");
		transferRequest.setAmount(BigDecimal.valueOf(500));

		ResponseEntity<ResponseObject<String>> response = restTemplate.exchange(
				"/api/accounts/transfer",
				HttpMethod.POST,
				new HttpEntity<>(transferRequest),
				new ParameterizedTypeReference<>() {
				}
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		ResponseObject<String> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getBody()).isEqualTo("Transfer successfully executed");

		Account updatedSource = accountRepository.findByAccountNumber("12345");
		Account updatedTarget = accountRepository.findByAccountNumber("67890");

		assertThat(updatedSource.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(500));
		assertThat(updatedTarget.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(2500));
	}

	@Test
	void depositFundsNotFound() {
		TransactionRequest request = new TransactionRequest();
		request.setAmount(BigDecimal.valueOf(200));

		ResponseEntity<ResponseObject<String>> response = restTemplate.exchange(
				"/api/accounts/99999/deposit",
				HttpMethod.POST,
				new HttpEntity<>(request),
				new ParameterizedTypeReference<>() {
				}
		);

		assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
		ResponseObject<String> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getBody()).contains("Account with number 99999 does not exist");
	}

	@Test
	void withdrawFundsInsufficientBalance() {
		Account account = new Account("12345", BigDecimal.valueOf(100));
		accountRepository.save(account);

		TransactionRequest request = new TransactionRequest();
		request.setAmount(BigDecimal.valueOf(200));

		ResponseEntity<ResponseObject<String>> response = restTemplate.exchange(
				"/api/accounts/12345/withdraw",
				HttpMethod.POST,
				new HttpEntity<>(request),
				new ParameterizedTypeReference<>() {
				}
		);

		assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.CONFLICT);
		ResponseObject<String> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getBody()).contains("Insufficient balance");
	}

	@Test
	void transferFundsAccountNotFound() {
		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setSourceAccountNumber("12345");
		transferRequest.setTargetAccountNumber("67890");
		transferRequest.setAmount(BigDecimal.valueOf(500));

		ResponseEntity<ResponseObject<String>> response = restTemplate.exchange(
				"/api/accounts/transfer",
				HttpMethod.POST,
				new HttpEntity<>(transferRequest),
				new ParameterizedTypeReference<>() {
				}
		);

		assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
		ResponseObject<String> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getBody()).contains("Account with number 12345 does not exist");
	}
}
