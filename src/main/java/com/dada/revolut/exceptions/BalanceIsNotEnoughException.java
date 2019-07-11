package com.dada.revolut.exceptions;

import java.math.BigDecimal;

public class BalanceIsNotEnoughException extends Exception {
    private final Long id;
    private final BigDecimal amount;

    public BalanceIsNotEnoughException(Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }


    @Override
    public String getMessage() {
        return "Account with id = " + id + " doesn't have enough balance to transfer this amount = " + amount;
    }
}
