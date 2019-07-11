package com.dada.revolut.services.impl;

import java.math.BigDecimal;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.dada.revolut.exceptions.AccountNotFoundException;
import com.dada.revolut.exceptions.BalanceIsNotEnoughException;
import com.dada.revolut.model.Account;
import com.dada.revolut.services.LockManager;
import com.dada.revolut.services.AccountService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of <code>AccountService</code> where the datastore is
 * represented as H2 data base.
 */
@Singleton
public class AccountServiceImpl implements AccountService {
	private Sql2o sql2o;
	private LockManager lockManager;

	@Inject
	public AccountServiceImpl(Sql2o sql2o, LockManager lockManager) {
		this.sql2o = sql2o;
		this.lockManager = lockManager;
	}

	@Override
	public Account createAccount(BigDecimal balance) {
		try (Connection conn = sql2o.beginTransaction()) {
			Long id = conn.createQuery("insert into account (BALANCE) values (:balance)", true)
					.addParameter("balance", balance).executeUpdate().getKey(Long.class);
			conn.commit();
			return getAccount(id);
		} catch (AccountNotFoundException e) {
			return null;
		}
	}

	@Override
	public Account getAccount(Long id) throws AccountNotFoundException {
		try (Connection conn = sql2o.beginTransaction()) {
			Account account = getAccount(conn, id);
			conn.commit();
			if (account == null) {
				throw new AccountNotFoundException(id);
			}
			return account;
		}
	}

	@Override
	public BigDecimal deposit(Long id, BigDecimal amount) throws Exception {
		BigDecimal updatedAmount = lockManager.doInLock(id, () -> {
			Account account;
			try (Connection conn = sql2o.beginTransaction()) {
				account = lockAndFetchAccount(conn, id);
				updateBalance(conn, id, amount);
				conn.commit();
				account = getAccount(conn, id);
			}
			if (account != null) {
				return account.getBalance();
			}
			return null;
		});
		return updatedAmount;
	}

	@Override
	public BigDecimal withdraw(Long id, BigDecimal amount) throws Exception {
		BigDecimal updatedAmount = lockManager.doInLock(id, () -> {
			Account account;
			try (Connection conn = sql2o.beginTransaction()) {
				account = lockAndFetchAccount(conn, id);
				if (account.getBalance().compareTo(amount) < 0) {
					throw new BalanceIsNotEnoughException(id, amount);
				}
				updateBalance(conn, id, amount.negate());
				conn.commit();
				account = getAccount(conn, id);
			}
			if (account != null) {
				return account.getBalance();
			}
			return null;
		});
		return updatedAmount;
	}

	/**
	 * The <code>lockAndFetchAccount</code> is responsible for locking and fetching
	 * the Account which we will update.
	 *
	 * @param conn - connection to DB
	 * @param id   - the id of account that need to be locked.
	 * @return the <code>Account</code> object that contains info about locked
	 *         Account
	 */
	@Override
	public Account lockAndFetchAccount(Connection conn, Long id) throws AccountNotFoundException {
		Account account = conn.createQuery("select * from account where id = :id FOR UPDATE").addParameter("id", id)
				.executeAndFetchFirst(Account.class);
		if (account == null) {
			throw new AccountNotFoundException(id);
		}
		return account;
	}

	@Override
	public Account getAccount(Connection conn, Long id) {
		Account account = conn.createQuery("select * from account where id = :id").addParameter("id", id)
				.executeAndFetchFirst(Account.class);
		return account;
	}

	@Override
	public void updateBalance(Connection conn, Long id, BigDecimal amount) {
		conn.createQuery("UPDATE ACCOUNT set BALANCE = BALANCE + :amount where id = :id").addParameter("id", id)
				.addParameter("amount", amount).executeUpdate();
	}
}
