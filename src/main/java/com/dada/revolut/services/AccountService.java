package com.dada.revolut.services;

import java.math.BigDecimal;

import org.sql2o.Connection;

import com.dada.revolut.exceptions.AccountNotFoundException;
import com.dada.revolut.model.Account;

/**
 * The <code>AccountService</code> is responsible for operations with Account.
 */
public interface AccountService {

    /**
     * The <code>createAccount</code> is responsible for creating the account with started balance.
     * @param balance - the started balance
     * @return the <code>Account</code> object that contains new account.
     */
    Account createAccount(BigDecimal balance);

    /**
     * The <code>getAccount</code> is responsible for getting the account.
     * @param id - the id of account
     * @return the <code>Account</code> object that contains the account.
     * @throws AccountNotFoundException in case account was not found
     */
    Account getAccount(Long id) throws AccountNotFoundException;
    
    /**
     * The <code>deposite</code> is responsible for updating balance of the account.
     * @param id - the id of account
     * @param amount - deposited Amount
     * @return the <code>updated Balance</code>.
     * @throws AccountNotFoundException in case account was not found
     */
    BigDecimal deposit(Long id, BigDecimal amount) throws Exception;
    
    /**
     * The <code>withdraw</code> is responsible for updating balance of the account.
     * @param id - the id of account
     * @param amount - deposited Amount
     * @return the <code>updated Balance</code>.
     * @throws AccountNotFoundException in case account was not found
     * @throws Exception 
     */
    BigDecimal withdraw(Long id, BigDecimal amount) throws Exception;

	Account lockAndFetchAccount(Connection conn, Long id) throws AccountNotFoundException;

	void updateBalance(Connection conn, Long id, BigDecimal amount);

	Account getAccount(Connection conn, Long id);

}
