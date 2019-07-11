package com.dada.revolut.services;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dada.revolut.BaseTest;
import com.dada.revolut.exceptions.AccountNotFoundException;
import com.dada.revolut.model.Account;
import com.dada.revolut.model.Transaction;
import com.dada.revolut.services.impl.LockManagerImpl;
import com.dada.revolut.services.impl.AccountServiceImpl;
import com.dada.revolut.services.impl.TransactionServiceImpl;

public class TransactionServiceConcurrencyTest extends BaseTest {

	private LockManager lockManger;
	
	@Before
    public void setUpTestData() {
		beforeClass();
		lockManger = new LockManagerImpl();
    }
	
	@After
    public void cleanTestData() {
    	lockManger.clear();
    	lockManger.stop();
    }
	
    @Test
    public void testTwoAccounts() throws InterruptedException, AccountNotFoundException {
    	AccountService accountService = new AccountServiceImpl(sql2o, lockManger);
        TransactionService transactionService = new TransactionServiceImpl(sql2o, lockManger, accountService);
        BigDecimal originalBalance = BigDecimal.ZERO;
        int nAccounts = 2;
        for (long i = 1; i <= nAccounts; i++) {
            Account account = accountService.createAccount(BigDecimal.valueOf(2500));
            originalBalance = originalBalance.add(account.getBalance());
        }

        ConcurrencyTestUtil stressTester = new ConcurrencyTestUtil(100);
        //Always transfer from first to second account
        test(transactionService, 1, stressTester);

        BigDecimal resultBalance = BigDecimal.ZERO;

        for (long i = 1; i <= nAccounts; i++) {
            Account account = accountService.getAccount(i);
            resultBalance = resultBalance.add(account.getBalance());
        }
        assertEquals(originalBalance, resultBalance);

        //as result the first account should contains 2500 - 1000
        assertEquals(BigDecimal.valueOf(1500.0), accountService.getAccount(1L).getBalance());
        //as result second account should contains 2500 + 1000
        assertEquals(BigDecimal.valueOf(3500.0), accountService.getAccount(2L).getBalance());
    }

    @Test
    public void testConcurrentUsers() throws InterruptedException, AccountNotFoundException {
        AccountService accountService = new AccountServiceImpl(sql2o, lockManger);
        TransactionService transactionService = new TransactionServiceImpl(sql2o, lockManger, accountService);
        BigDecimal originalBalance = BigDecimal.ZERO;
        for (long i = 1; i <= 50; i++) {
            Account account = accountService.createAccount(BigDecimal.valueOf(1000));
            originalBalance = originalBalance.add(account.getBalance());
        }
        ConcurrencyTestUtil stressTester = new ConcurrencyTestUtil(10);
        test(transactionService, 50, stressTester);

        BigDecimal resultBalance = BigDecimal.ZERO;

        for (long i = 1; i <= 50; i++) {
            Account account = accountService.getAccount(i);
            resultBalance = resultBalance.add(account.getBalance());
        }
        assertEquals(originalBalance, resultBalance);
    }

    private void test(TransactionService transactionService, int nAccounts, ConcurrencyTestUtil stressTester)
            throws InterruptedException {

        stressTester.concurrentRun(() -> {
            long randomFromId = ThreadLocalRandom.current().nextLong(1, nAccounts + 1);
            long randomToId = ThreadLocalRandom.current().nextLong(1, nAccounts + 1);
            long toId = randomToId == randomFromId ? randomToId > 1 ? randomToId - 1 : randomToId + 1 : randomToId;

            Transaction transaction = new Transaction(randomFromId, toId, BigDecimal.TEN);
            try {
                transactionService.transfer(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        stressTester.shutdown();
    }
}
