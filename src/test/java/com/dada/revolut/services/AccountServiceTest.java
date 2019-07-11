package com.dada.revolut.services;


import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dada.revolut.BaseTest;
import com.dada.revolut.exceptions.AccountNotFoundException;
import com.dada.revolut.model.Account;
import com.dada.revolut.services.impl.LockManagerImpl;
import com.dada.revolut.services.impl.AccountServiceImpl;

public class AccountServiceTest extends BaseTest {

    private static AccountService accountService = new AccountServiceImpl(sql2o, new LockManagerImpl());

    @BeforeClass
    public static void before() {
        accountService.createAccount(BigDecimal.valueOf(200));
    }

    @Test
    public void testCreateAccount() {
    	Account account = accountService.createAccount(BigDecimal.valueOf(150));
        assertEquals(Long.valueOf(2), account.getId());
        assertEquals(BigDecimal.valueOf(150.0), account.getBalance());
    }

    @Test
    public void testGetAccount() throws Exception {
    	Account account = accountService.getAccount(1L);
        assertEquals(Long.valueOf(1), account.getId());
        assertEquals(BigDecimal.valueOf(200.0), account.getBalance());
    }

    @Test(expected = AccountNotFoundException.class)
    public void testGetNotExistingAccountSql() throws Exception {
        accountService.getAccount(5L);
    }
    
    @Test
    public void testDepositeAmount() throws Exception {
    	Account account = accountService.getAccount(1L);
        assertEquals(Long.valueOf(1), account.getId());
        BigDecimal updatedBalance = accountService.deposit(account.getId(), BigDecimal.valueOf(500.0));
        assertEquals(BigDecimal.valueOf(700.0), updatedBalance);
    }
    
    @Test
    public void testWithdrawAmount() throws Exception {
    	Account account = accountService.getAccount(1L);
        assertEquals(Long.valueOf(1), account.getId());
        BigDecimal updatedBalance = accountService.withdraw(account.getId(), BigDecimal.valueOf(500.0));
        assertEquals(BigDecimal.valueOf(200.0), updatedBalance);
    }
}