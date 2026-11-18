package org.pshenko.springcore.account;

public record TransferResult(
    int fromUserId,
    int toUserId,
    int commission,
    int amountAfterCommission
) {
    public boolean isSameUser() {
        return fromUserId == toUserId;
    }
}
