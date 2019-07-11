package com.dada.revolut.services;

import java.util.concurrent.Callable;

public interface LockManager {
    void createLock(Long accountId);

    void removeLock(Long accountId);

    <T> T doInLock(Long accountId, Callable<T> action) throws Exception;

    <T> T doInLock(Long accountId1, Long accountId2, Callable<T> action) throws Exception;

	void clear();

	int size();

	void stop();

}
