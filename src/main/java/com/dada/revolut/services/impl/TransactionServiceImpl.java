package com.dada.revolut.services.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.dada.revolut.exceptions.AccountNotFoundException;
import com.dada.revolut.exceptions.BalanceIsNotEnoughException;
import com.dada.revolut.exceptions.TheSameAccountException;
import com.dada.revolut.model.Account;
import com.dada.revolut.model.Transaction;
import com.dada.revolut.services.AccountService;
import com.dada.revolut.services.LockManager;
import com.dada.revolut.services.TransactionService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of <code>TransactionService</code> where the datastore is represented as H2 data base.
 */
@Singleton
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

    private final Sql2o sql2o;
    private LockManager lockManager;
    private AccountService accountService;

    @Inject
    public TransactionServiceImpl(Sql2o sql2o, LockManager lockManager, AccountService accountService) {
        this.sql2o = sql2o;
        this.lockManager = lockManager;
        this.accountService = accountService;
    }

    @Override
    public void transfer(Transaction transaction) throws Exception {
        if (transaction.getFromId().equals(transaction.getToId())) {
            LOGGER.error("Can't transfer to the same account.");
            throw new TheSameAccountException();
        }

        lockManager.doInLock(transaction.getFromId(), transaction.getToId(), () -> {
        try (Connection conn = sql2o.beginTransaction()) {
            Account fromAccount = accountService.lockAndFetchAccount(conn, transaction.getFromId());
            Account toAccount = accountService.lockAndFetchAccount(conn, transaction.getToId());
            if (fromAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new BalanceIsNotEnoughException(fromAccount.getId(), transaction.getAmount());
            }
            accountService.updateBalance(conn, transaction.getFromId(), transaction.getAmount().negate());
            accountService.updateBalance(conn, transaction.getToId(), transaction.getAmount());

            conn.commit();
            System.out.println("Completed "+transaction);
		}
        return null;
        });
    }
}
