package org.pshenko.springcore.account;

public record CloseResult(
    int targetAccountId,
    int transferredAmount
) {
}
