package com.alternabank.engine.transaction;

import com.alternabank.dto.transaction.BilateralTransactionRecord;
import com.alternabank.dto.transaction.type.BilateralTransactionTypeData;
import com.alternabank.engine.time.TimeManager;

public class BilateralTransaction extends AbstractTransaction implements Transaction.Bilateral {

    private final Type type;
    private final double interest;
    private final double principal;

    public BilateralTransaction(Type type, double principal, double interest) {
        super(principal + interest);
        this.type = type;
        this.principal = principal;
        this.interest = interest;
    }


    @Override
    public Type getType() {
        return type;
    }

    @Override
    public BilateralTransactionRecord execute(Initiator initiator, Recipient recipient, int executionTime) {
        double initiatorBalanceBefore = initiator.getBalance();
        double recipientBalanceBefore = recipient.getBalance();
        Status status = type.getOperation().transact(initiator, recipient, getTotal());
        return new BilateralTransactionRecord(TimeManager.TIME_UNIT_NAME, getID(), Type.toDTO(type), executionTime, initiator.getID(), principal, interest, initiatorBalanceBefore, initiator.getBalance(), recipient.getID(), recipientBalanceBefore, recipient.getBalance(), Status.toDTO(status));
    }

    public enum Type {

        TRANSFER((initiator, recipient, total) -> { if(initiator.deductFunds(total)) { recipient.addFunds(total); return Status.SUCCESSFUL; } return Status.FAILED; });

        private final Operation.Bilateral operation;

        Type(Operation.Bilateral operation) {
            this.operation = operation;
        }

        public Operation.Bilateral getOperation() {
            return operation;
        }

        public static BilateralTransactionTypeData toDTO(Type type) {
            BilateralTransactionTypeData result = null;
            switch(type) {
                case TRANSFER:
                    result = BilateralTransactionTypeData.TRANSFER;
                    break;
            }
            return result;
        }
    }

}
