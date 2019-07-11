package com.dada.revolut.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction {
    Long fromId;
    Long toId;
    BigDecimal amount;
}
