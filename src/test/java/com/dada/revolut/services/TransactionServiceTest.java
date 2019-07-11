package com.dada.revolut.services;


import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dada.revolut.BaseTest;
import com.dada.revolut.exceptions.AccountNotFoundException;
import com.dada.revolut.exceptions.BalanceIsNotEnoughException;
import com.dada.revolut.exceptions.TheSameAccountException;
import com.dada.revolut.model.Account;
import com.dada.revolut.model.Transaction;
import com.dada.revolut.services.impl.LockManagerImpl;
import com.dada.revolut.services.impl.AccountServiceImpl;
import com.dada.revolut.services.impl.TransactionServiceImpl;

public class TransactionServiceTest extends BaseTest {

    private LockManager accountLockManager = new LockManagerImpl();
    private AccountService accountService = new AccountServiceImpl(sql2o, accountLockManager);
    private TransactionService serviceSql = new TransactionServiceImpl(sql2o, accountLockManager, accountService);
	
    @Before
    public void setUpTestData() {
    	beforeClass();
    }
	
	@After
    public void cleanTestData() {
		/*
		 * accountManger.clear(); accountManger.stop(); accountManger = null; serviceSql
		 * = null;
		 */
    }

    @Test
    public void testSuccessfulTransfer() throws Exception {
        Account fromAccount = accountService.createAccount(BigDecimal.valueOf(300.0));
        Account toAccount = accountService.createAccount(BigDecimal.valueOf(400.0));
        
        Transaction transaction = new Transaction(1L, 2L, BigDecimal.valueOf(100.0));
        serviceSql.transfer(transaction);
        fromAccount = accountService.getAccount(1L);
        toAccount = accountService.getAccount(2L);
        assertEquals(BigDecimal.valueOf(200.0), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(500.0), toAccount.getBalance());

    }

    @Test(expected = TheSameAccountException.class)
    public void testTransferWithToAccountWithTheSameId() throws Exception {
        Account account = accountService.createAccount(BigDecimal.valueOf(300.0));
        Transaction transaction = new Transaction(1L, 1L, BigDecimal.valueOf(100.0));
        serviceSql.transfer(transaction);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferFromNotExistingAccountSql() throws Exception {
        Transaction transaction = new Transaction(1L, 2L, BigDecimal.valueOf(100.0));
        serviceSql.transfer(transaction);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferToNotExistingAccountSql() throws Exception {
        Account fromAccount = accountService.createAccount(BigDecimal.valueOf(300.0));
        Transaction transaction = new Transaction(1L, 2L, BigDecimal.valueOf(100.0));
        serviceSql.transfer(transaction);
    }

    @Test(expected = BalanceIsNotEnoughException.class)
    public void testTransferNotEnoughBalance() throws Exception {
        Account fromAccount = accountService.createAccount(BigDecimal.valueOf(300.0));
        Account toAccount = accountService.createAccount(BigDecimal.valueOf(400.0));
        
        Transaction transaction = new Transaction(1L, 2L, BigDecimal.valueOf(400.0));
        serviceSql.transfer(transaction);
    }

}