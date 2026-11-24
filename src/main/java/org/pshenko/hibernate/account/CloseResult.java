package org.pshenko.hibernate.account;

public record CloseResult(
    int targetAccountId,
    int transferredAmount
) {
}
