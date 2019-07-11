package com.dada.revolut.model;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Account {
    private volatile Long id;
    private volatile BigDecimal balance;

    public Account(Long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }
}
