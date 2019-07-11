package com.dada.revolut.exceptions;

public class AccountNotFoundException extends Exception {

    private final Long id;

    public AccountNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Account with id = " + id + " wasn't found";
    }
}
