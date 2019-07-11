package com.dada.revolut.controllers;


import com.dada.revolut.BaseTest;
import com.dada.revolut.controllers.ExceptionController;
import com.dada.revolut.controllers.TransactionController;
import com.dada.revolut.model.Account;
import com.dada.revolut.model.Transaction;
import com.dada.revolut.services.LockManager;
import com.dada.revolut.services.AccountService;
import com.dada.revolut.services.impl.LockManagerImpl;
import com.dada.revolut.services.impl.AccountServiceImpl;
import com.dada.revolut.services.impl.TransactionServiceImpl;
import com.dada.revolut.utils.JsonToObjectConvertor;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;

import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TransactionControllerTest extends BaseTest {

	private static LockManager accountManger = new LockManagerImpl();
	private static final AccountService accountService = new AccountServiceImpl(sql2o, accountManger);
    
	public static class TransactionControllerTestApplication implements SparkApplication {
        @Override
        public void init() {
            new TransactionController(new TransactionServiceImpl(sql2o, accountManger, accountService));
            new ExceptionController();
        }
    }

    @ClassRule
    public static SparkServer<TransactionControllerTestApplication> testServer =
            new SparkServer<>(TransactionControllerTestApplication.class, 4567);

    @Test
    public void testTransfer() throws Exception {
        Account fromAccount = accountService.createAccount(BigDecimal.valueOf(500));
        Account toAccount = accountService.createAccount(BigDecimal.valueOf(100));
        Transaction transaction = new Transaction(fromAccount.getId(), toAccount.getId(), BigDecimal.valueOf(150));
        PostMethod post = testServer.post("/transfer", JsonToObjectConvertor.convertToJson(transaction), false);
        HttpResponse httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
    }

    @Test
    public void testTransferSameAccounts() throws Exception {
        Account account = accountService.createAccount(BigDecimal.valueOf(500));
        Transaction transaction = new Transaction(account.getId(), account.getId(), BigDecimal.valueOf(150));
        PostMethod post = testServer.post("/transfer", JsonToObjectConvertor.convertToJson(transaction), false);
        HttpResponse httpResponse = testServer.execute(post);
        assertEquals(404, httpResponse.code());
        assertEquals("Can't transfer the money to the same account.", new String(httpResponse.body()));

    }

    @Test
    public void testTransferNotEnoughMoneyAccounts() throws Exception {
        Account fromAccount = accountService.createAccount(BigDecimal.valueOf(0));
        Account toAccount = accountService.createAccount(BigDecimal.valueOf(100));
        Transaction transaction = new Transaction(fromAccount.getId(), toAccount.getId(), BigDecimal.valueOf(150));
        PostMethod post = testServer.post("/transfer", JsonToObjectConvertor.convertToJson(transaction), false);
        HttpResponse httpResponse = testServer.execute(post);
        assertEquals(404, httpResponse.code());
        assertEquals("Account with id = " + fromAccount.getId() + " doesn't have enough balance to transfer this amount = 150",
                new String(httpResponse.body()));

    }

}