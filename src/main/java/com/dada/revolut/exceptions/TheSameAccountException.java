package com.dada.revolut.exceptions;

public class TheSameAccountException extends Exception {
    @Override
    public String getMessage() {
        return "Can't transfer the money to the same account.";
    }
}
