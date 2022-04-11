package com.alternabank.engine.transaction;

import com.alternabank.engine.time.TimeManager;

public class BilateralTransaction extends AbstractTransaction implements Transaction.Bilateral {

    private final Bilateral.Type type;
    private final double interest;
    private final double principal;

    public BilateralTransaction(Bilateral.Type type, double principal, double interest) {
        super(principal + interest);
        this.type = type;
        this.principal = principal;
        this.interest = interest;
    }


    @Override
    public Bilateral.Type getType() {
        return type;
    }

    @Override
    public Transaction.Record.Bilateral execute(Initiator initiator, Recipient recipient) {
        double initiatorBalanceBefore = initiator.getBalance();
        double recipientBalanceBefore = recipient.getBalance();
        Status status = type.getOperation().transact(initiator, recipient, getTotal());
        return new Record(TimeManager.getInstance().getCurrentTime(), initiator.getID(), initiatorBalanceBefore, initiator.getBalance(), recipient.getID(), recipientBalanceBefore, recipient.getBalance(), status);
    }

    public class Record extends AbstractTransaction.Record implements Transaction.Record.Bilateral {

        private final String recipientID;
        private final double recipientBalanceBefore;
        private final double recipientBalanceAfter;

        protected Record(int executionTime, String initiatorID, double initiatorBalanceBefore, double initiatorBalanceAfter, String recipientID, double recipientBalanceBefore, double recipientBalanceAfter, Status status) {
            super(executionTime, initiatorID, initiatorBalanceBefore, initiatorBalanceAfter, status);
            this.recipientID = recipientID;
            this.recipientBalanceBefore = recipientBalanceBefore;
            this.recipientBalanceAfter = recipientBalanceAfter;
        }

        @Override
        public Transaction.Bilateral.Type getType() {
            return type;
        }

        @Override
        public String toString() {
            return String.format(
                    "(%s %d) %s TRANSACTION:" + System.lineSeparator()
                            + "\tInitiator: %s (Balance: %.2f -> %.2f)" + System.lineSeparator()
                            + "\tRecipient: %s (Balance: %.2f -> %.2f)" + System.lineSeparator()
                            + "\tTotal: %.2f (Principal: %.2f, Interest: %.2f)" + System.lineSeparator()
                            + "\tStatus: %s",
                    TimeManager.getInstance().getTimeUnitName(),
                    getExecutionTime(),
                    type, getInitiatorID(), getInitiatorBalanceBefore(), getInitiatorBalanceAfter(),
                    getRecipientID(), getRecipientBalanceBefore(), getRecipientBalanceAfter(),
                    getTotal(), getPrincipalPart(), getInterestPart(), getStatus());
        }

        @Override
        public String getRecipientID() {
            return recipientID;
        }

        @Override
        public double getPrincipalPart() {
            return principal;
        }

        @Override
        public double getInterestPart() {
            return interest;
        }

        @Override
        public double getRecipientBalanceBefore() {
            return recipientBalanceBefore;
        }

        @Override
        public double getRecipientBalanceAfter() {
            return recipientBalanceAfter;
        }
    }

    public enum Type implements Bilateral.Type {

        TRANSFER((initiator, recipient, total) -> { if(initiator.deductFunds(total)) { recipient.addFunds(total); return Status.SUCCESSFUL; } return Status.FAILED; });

        private final Operation.Bilateral operation;

        Type(Operation.Bilateral operation) {
            this.operation = operation;
        }

        @Override
        public Operation.Bilateral getOperation() {
            return operation;
        }

    }

}
