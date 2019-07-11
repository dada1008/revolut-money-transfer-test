package com.dada.revolut.services.impl;

import com.dada.revolut.services.LockManager;
import com.google.inject.Singleton;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public class LockManagerImpl implements LockManager {
    Map<Long, Lock> map = new ConcurrentHashMap<>();
    private final DelayQueue<DelayedEntry> cleanupQueue = new DelayQueue<>();
    private Duration DEFAULT_CACHE_DURATION = Duration.ofSeconds(5);
    
    private Thread evictionThread = new Thread(new EvictionTask(), "EvictionThread");
    
    public LockManagerImpl() {
    	super();
        evictionThread.start();
	}
    
    @Override
    public void createLock(Long accountId) {
        map.putIfAbsent(accountId, new ReentrantLock());
        DelayedEntry delayedEntry = new DelayedEntry(accountId, map.get(accountId), System.currentTimeMillis() + DEFAULT_CACHE_DURATION.toMillis());
        cleanupQueue.remove(delayedEntry);
        cleanupQueue.add(delayedEntry);
        //System.out.println("Lock created for account:"+accountId);
    }

    @Override
    public void removeLock(Long accountId) {
        map.remove(accountId);
        //System.out.println("Lock removed for account:"+accountId);
    }


    @Override
    public <T> T doInLock(Long accountId, Callable<T> action) throws Exception {
        createLock(accountId);
        Lock lock = map.get(accountId);
        lock.lock();
        try {
            return action.call();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T doInLock(Long accountId1, Long accountId2, Callable<T> action) throws Exception {
        createLock(accountId1);
        createLock(accountId2);
        Lock lock1 = map.get(accountId1);
        Lock lock2 = map.get(accountId2);
        boolean gotTwoLocks = false;
        do {
            if (lock1.tryLock()) {
                if (lock2.tryLock()) {
                    gotTwoLocks = true;
                } else {
                    lock1.unlock();
                }
            }
        } while (!gotTwoLocks);
        try {
            return action.call();
        } finally {
            lock2.unlock();
            lock1.unlock();
        }
    }
    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public int size() {
        return map.size();
    }


    @Override
    public void stop() {
        this.evictionThread.interrupt();
    }

	private static class DelayedEntry implements Delayed {

        private final Long accoundId;
        private final Lock lock;
        private final long expiryTime;

		public DelayedEntry(Long accoundId, Lock lock, long expiryTime) {
            this.accoundId = accoundId;
            this.lock = lock;
            this.expiryTime = expiryTime;
        }

        public Long getAccoundId() {
            return accoundId;
        }
        public Lock getLock() {
            return lock;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expiryTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(expiryTime, ((DelayedEntry) o).expiryTime);
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((accoundId == null) ? 0 : accoundId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DelayedEntry other = (DelayedEntry) obj;
			if (accoundId == null) {
				if (other.accoundId != null)
					return false;
			} else if (!accoundId.equals(other.accoundId))
				return false;
			return true;
		}
    }

    class EvictionTask implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DelayedEntry delayedCacheEntry = cleanupQueue.take();
                    removeLock(delayedCacheEntry.getAccoundId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
