package com.dada.revolut.controllers;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.dada.revolut.BaseTest;
import com.dada.revolut.model.Account;
import com.dada.revolut.model.Money;
import com.dada.revolut.services.impl.LockManagerImpl;
import com.dada.revolut.utils.JsonToObjectConvertor;
import com.dada.revolut.services.impl.AccountServiceImpl;
import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;

import spark.servlet.SparkApplication;

public class AccountControllerTest extends BaseTest {

    public static class AccountControllerTestApplication implements SparkApplication {
        @Override
        public void init() {
            new AccountController(new AccountServiceImpl(sql2o, new LockManagerImpl()));
            new ExceptionController();
        }
    }
    
    @Before
    public void beforeTest() {
    	beforeClass();
    }

    @ClassRule
    public static SparkServer<AccountControllerTestApplication> testServer =
            new SparkServer<>(AccountControllerTestApplication.class, 4567);

    @Test
    public void testCreateAndGetAccount() throws Exception {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100.0));
        PostMethod post = testServer.post("/account", JsonToObjectConvertor.convertToJson(account), false);
        HttpResponse httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        account.setId(1L);
        assertEquals(account.toString(), new String(httpResponse.body()));

        GetMethod get = testServer.get("/account/1", false);
        httpResponse = testServer.execute(get);
        assertEquals(200, httpResponse.code());
        assertEquals(account.toString(), new String(httpResponse.body()));

        assertNotNull(testServer.getApplication());
    }

    @Test
    public void testDepositeAmount() throws Exception {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(500.0));
        PostMethod post = testServer.post("/account", JsonToObjectConvertor.convertToJson(account), false);
        HttpResponse httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        account.setId(1L);
        assertEquals(account.toString(), new String(httpResponse.body()));

        Money money = new Money(BigDecimal.valueOf(300.0));
        post = testServer.post("/account/1/deposit", JsonToObjectConvertor.convertToJson(money), false);
        httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        assertEquals(BigDecimal.valueOf(800.0).toString(), new String(httpResponse.body()));

        assertNotNull(testServer.getApplication());
    }
    
    @Test
    public void testWithdrawAmount() throws Exception {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(500.0));
        PostMethod post = testServer.post("/account", JsonToObjectConvertor.convertToJson(account), false);
        HttpResponse httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        account.setId(1L);
        assertEquals(account.toString(), new String(httpResponse.body()));

        Money money = new Money(BigDecimal.valueOf(300.0));
        post = testServer.post("/account/1/withdraw", JsonToObjectConvertor.convertToJson(money), false);
        httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        assertEquals(BigDecimal.valueOf(200.0).toString(), new String(httpResponse.body()));

        assertNotNull(testServer.getApplication());
    }
    
    
    @Test
    public void testGetNotExistingAccount() throws Exception {
        GetMethod get = testServer.get("/account/2", false);
        HttpResponse httpResponse = testServer.execute(get);
        assertEquals(404, httpResponse.code());
        assertEquals("Account with id = 2 wasn't found", new String(httpResponse.body()));
        assertNotNull(testServer.getApplication());
    }
}