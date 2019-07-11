package com.dada.revolut.controllers;

import com.dada.revolut.model.Transaction;
import com.dada.revolut.services.TransactionService;
import com.dada.revolut.utils.JsonToObjectConvertor;
import com.google.inject.Inject;

import static spark.Spark.post;

/**
 * The <code>TransactionController</code> contains REST API for Transactions.
 */
public class TransactionController {
    @Inject
    public TransactionController(final TransactionService transactionService) {

        post("/transfer", (req, res) -> {
            Transaction transaction = JsonToObjectConvertor.jsonToObject(req.body(), Transaction.class);
            transactionService.transfer(transaction);
            return res;
        });

    }
}
