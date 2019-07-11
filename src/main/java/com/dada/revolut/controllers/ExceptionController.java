package com.dada.revolut.controllers;

import static spark.Spark.exception;

import com.dada.revolut.exceptions.AccountNotFoundException;
import com.dada.revolut.exceptions.BalanceIsNotEnoughException;
import com.dada.revolut.exceptions.TheSameAccountException;

/**
 * The <code>ExceptionController</code> is responsible for exception handling.
 */
public class ExceptionController {

    ExceptionController() {
        exception(AccountNotFoundException.class, (exception, request, response) -> {
            response.status(404);
            response.body(exception.getMessage());
        });

        exception(BalanceIsNotEnoughException.class, (exception, request, response) -> {
            response.status(404);
            response.body(exception.getMessage());
        });

        exception(TheSameAccountException.class, (exception, request, response) -> {
            response.status(404);
            response.body(exception.getMessage());
        });

    }
}
