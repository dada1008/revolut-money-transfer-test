package com.dada.revolut.services;

import com.dada.revolut.model.Transaction;

/**
 * The <code>TransactionService</code> is responsible for operations with Transactions.
 */
public interface TransactionService {
    /**
     * The <code>transfer</code> is responsible for transfer money between two accounts.
     * @param transaction - the <code>Transaction</code> object that contains info about transaction
     * @throws Exception in case issue
     */
    void transfer(Transaction transaction) throws Exception;
}
