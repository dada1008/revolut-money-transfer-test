package com.dada.revolut.injector;

import com.dada.revolut.services.AccountService;
import com.dada.revolut.services.LockManager;
import com.dada.revolut.services.TransactionService;
import com.dada.revolut.services.impl.AccountServiceImpl;
import com.dada.revolut.services.impl.LockManagerImpl;
import com.dada.revolut.services.impl.TransactionServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class AppSqlInjector extends AbstractModule {

    @Override
    protected void configure() {
        //bind the service to implementation class
        bind(TransactionService.class).to(TransactionServiceImpl.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(LockManager.class).to(LockManagerImpl.class);
    }

    @Provides
    public Sql2o provideSql2o() {
        String dbUrl = "jdbc:h2:~/revolut;";

        Sql2o sql2o = new Sql2o(dbUrl, null, null);
        try (Connection connection = sql2o.beginTransaction()) {
            connection.createQuery("DROP TABLE ACCOUNT IF EXISTS;")
                    .executeUpdate();
            connection.createQuery("CREATE table if not exists account(" +
                    "id bigint auto_increment PRIMARY KEY," +
                    "balance DOUBLE PRECISION" +
                    ");")
                    .executeUpdate();
            connection.commit();
        }
        return sql2o;
    }
}
