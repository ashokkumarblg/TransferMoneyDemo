package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferBalanceRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.NotificationService;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {
	
	@Autowired
    private AccountsRepository accountRepository;
	
	@Autowired
	private NotificationService notificationService;

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

	/*
	 * @Override public void clearAccounts() { accounts.clear(); }
	 */

  @Override
  public ResponseEntity<Object> transferMoney(TransferBalanceRequest transferBalanceRequest) {
	  String fromAccountNumber = transferBalanceRequest.getAccountFromId();
      String toAccountNumber = transferBalanceRequest.getAccountToId();
      BigDecimal amount = transferBalanceRequest.getAmount();
      
      if (fromAccountNumber.equals(toAccountNumber)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account id must be different.");
	
      if ((amount.compareTo(BigDecimal.ZERO) <= 0)) {
    	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Amount must be greater than zero");
      }
      
      
      Account fromAccount = accounts.get(fromAccountNumber);
      Account toAccount = accounts.get(toAccountNumber);   
        
      if (fromAccount == null) {     
    	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Id "+fromAccountNumber+" is not exists! ");
      }
          
      if (toAccount == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account Id "+toAccountNumber+" is not exists! ");
      }
      
      System.out.println("From Id, ["+fromAccountNumber+"] To Id, ["+toAccountNumber+"] Amount, ["+amount+"]");
      
      if ((fromAccount.getBalance().compareTo(BigDecimal.ZERO) < 0) || (toAccount.getBalance().compareTo(BigDecimal.ZERO) < 0)) {
          //throw new RuntimeException("Amount cannot be negative.");
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Amount cannot be negative.");
      }
      
      if ((fromAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0) || (toAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0)) {
    	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Amount must be greater than zero");
      }
      
      System.out.println("From Account, ["+fromAccount+"] To Account, ["+toAccount+"] Amount, ["+amount+"]");
      
      if(fromAccount.getBalance().compareTo(amount) < 0) {
    	  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds.");
      } else {
    	  String msgText = "";
    	  synchronized (this) {   		  	  
	    	  fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
	    	  fromAccount = accounts.putIfAbsent(fromAccount.getAccountId(), fromAccount);	    	  
	      	  
	    	  toAccount.setBalance(toAccount.getBalance().add(amount));
	    	  toAccount = accounts.putIfAbsent(toAccount.getAccountId(), toAccount); 
	    	  
	    	  msgText = "Amount Rs. "+amount+"/- transferred.";
	    	  
	    	  notificationService.notifyAboutTransfer(toAccount, msgText);
    	      
    		  return ResponseEntity.status(HttpStatus.OK).body("Success: Amount "+amount+" transferred from "+fromAccount+" to "+toAccount+" .");    		  
    	  }
    	  
      }
  }  
}
