package com.dada.revolut;

import static spark.Spark.port;

import com.dada.revolut.controllers.AccountController;
import com.dada.revolut.controllers.ExceptionController;
import com.dada.revolut.controllers.TransactionController;
import com.dada.revolut.injector.AppSqlInjector;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Application {

    public static void main(String[] args) {

        port(8080); // Spark will run on port 8080
        Injector injector = Guice.createInjector(new AppSqlInjector());
        injector.getInstance(AccountController.class);
        injector.getInstance(TransactionController.class);
        injector.getInstance(ExceptionController.class);
    }

}