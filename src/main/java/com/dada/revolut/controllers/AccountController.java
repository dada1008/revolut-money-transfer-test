package com.dada.revolut.controllers;

import com.dada.revolut.model.Account;
import com.dada.revolut.model.Money;
import com.dada.revolut.services.AccountService;
import com.dada.revolut.utils.JsonToObjectConvertor;
import com.google.inject.Inject;

import static spark.Spark.*;

/**
 * The <code>AccountController</code> contains REST API for Account.
 */
public class AccountController {

    @Inject
    public AccountController(AccountService accountService) {
        get("/account/:id", (req, res) -> {
            Long id = Long.valueOf(req.params("id"));
            return accountService.getAccount(id);
        });
        
        post("/account/:id/deposit", "application/json", (req, res) -> {
            Long id = Long.valueOf(req.params("id"));
            Money money = JsonToObjectConvertor.jsonToObject(req.body(), Money.class);
            return accountService.deposit(id, money.getAmount());
        });
        
        post("/account/:id/withdraw", "application/json", (req, res) -> {
            Long id = Long.valueOf(req.params("id"));
            Money money = JsonToObjectConvertor.jsonToObject(req.body(), Money.class);
            return accountService.withdraw(id, money.getAmount());
        });
        
        post("/account", "application/json", (req, res) -> {
            Account account = JsonToObjectConvertor.jsonToObject(req.body(), Account.class);
            return accountService.createAccount(account.getBalance());
        });
    }


}
