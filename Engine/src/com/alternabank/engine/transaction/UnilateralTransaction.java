package com.alternabank.engine.transaction;

import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.user.UserManager;

public class UnilateralTransaction extends AbstractTransaction implements Transaction.Unilateral {

    private final Unilateral.Type type;

    public UnilateralTransaction(Unilateral.Type type, double total) {
        super(total);
        this.type = type;
    }

    @Override
    public Unilateral.Type getType() {
        return type;
    }

    @Override
    public Transaction.Record.Unilateral execute(Initiator initiator) {
        double balanceBefore = initiator.getBalance();
        Status status = type.getOperation().transact(initiator, getTotal());
        return new Record(UserManager.getInstance().getAdmin().getTimeManager().getCurrentTime(), initiator.getID(), balanceBefore, initiator.getBalance(), status);
    }

    public class Record extends AbstractTransaction.Record implements Transaction.Record.Unilateral {

        private Record(int executionTime, String initiatorID, double initiatorBalanceBefore, double initiatorBalanceAfter, Status status) {
            super(executionTime, initiatorID, initiatorBalanceBefore, initiatorBalanceAfter, status);
        }

        @Override
        public Transaction.Unilateral.Type getType() {
            return type;
        }

        @Override
        public String toString() {
            return String.format(
                    "(%s %d) %s TRANSACTION:" + System.lineSeparator()
                    + "\tInitiator: %s (Balance: %.2f -> %.2f)" + System.lineSeparator()
                    + "\tTotal: %.2f" + System.lineSeparator()
                    + "\tStatus: %s",
                    UserManager.getInstance().getAdmin().getTimeManager().getTimeUnitName(),
                    getExecutionTime(),
                    type, getInitiatorID(), getInitiatorBalanceBefore(),
                    getInitiatorBalanceAfter(), getTotal(), getStatus());
        }

    }

    public enum Type implements Unilateral.Type {

        DEPOSIT((initiator, total) -> initiator.addFunds(total) ? Status.SUCCESSFUL : Status.FAILED),
        WITHDRAWAL((initiator, total) -> initiator.deductFunds(total) ? Status.SUCCESSFUL : Status.FAILED);

        private final Operation.Unilateral operation;

        Type(Operation.Unilateral operation) {
            this.operation = operation;
        }

        @Override
        public Operation.Unilateral getOperation() {
            return operation;
        }

    }

}
