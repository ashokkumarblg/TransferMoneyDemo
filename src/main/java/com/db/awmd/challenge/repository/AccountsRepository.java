package com.db.awmd.challenge.repository;

import org.springframework.http.ResponseEntity;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferBalanceRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  //void clearAccounts();

  ResponseEntity<Object> transferMoney(TransferBalanceRequest transferBalanceRequest);
}
