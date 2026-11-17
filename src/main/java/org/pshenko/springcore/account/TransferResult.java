package org.pshenko.springcore.account;

public record TransferResult(
    int commission,
    int amountAfterCommission,
    boolean sameUser
) {
}
