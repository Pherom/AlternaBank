package com.alternabank.engine.transaction;

import com.alternabank.dto.transaction.UnilateralTransactionRecord;
import com.alternabank.dto.transaction.type.UnilateralTransactionTypeData;
import com.alternabank.engine.time.TimeManager;

public class UnilateralTransaction extends AbstractTransaction implements Transaction.Unilateral {

    private final Type type;

    public UnilateralTransaction(Type type, double total) {
        super(total);
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public UnilateralTransactionRecord execute(Initiator initiator, int executionTime) {
        double balanceBefore = initiator.getBalance();
        Status status = type.getOperation().transact(initiator, getTotal());
        return new UnilateralTransactionRecord(TimeManager.TIME_UNIT_NAME, getID(), Type.toDTO(type), executionTime, initiator.getID(), getTotal(), balanceBefore, initiator.getBalance(), Status.toDTO(status));
    }

    public enum Type {

        DEPOSIT((initiator, total) -> initiator.addFunds(total) ? Status.SUCCESSFUL : Status.FAILED),
        WITHDRAWAL((initiator, total) -> initiator.deductFunds(total) ? Status.SUCCESSFUL : Status.FAILED);

        private final Operation.Unilateral operation;

        Type(Operation.Unilateral operation) {
            this.operation = operation;
        }

        public Operation.Unilateral getOperation() {
            return operation;
        }

        public static UnilateralTransactionTypeData toDTO(Type type) {
            UnilateralTransactionTypeData result = null;
            switch(type) {
                case DEPOSIT:
                    result = UnilateralTransactionTypeData.DEPOSIT;
                    break;
                case WITHDRAWAL:
                    result = UnilateralTransactionTypeData.WITHDRAWAL;
                    break;
            }
            return result;
        }

    }

}
