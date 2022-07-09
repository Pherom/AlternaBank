package com.alternabank.engine.transaction;

import com.alternabank.dto.transaction.BilateralTransactionRecord;
import com.alternabank.dto.transaction.status.TransactionStatusData;
import com.alternabank.dto.transaction.UnilateralTransactionRecord;

public interface Transaction {

    int getID();

    double getTotal();

    interface Unilateral extends Transaction {

        UnilateralTransaction.Type getType();

        UnilateralTransactionRecord execute(Initiator initiator, int executionTime);

    }

    interface Bilateral extends Transaction {

        BilateralTransaction.Type getType();

        BilateralTransactionRecord execute(Initiator initiator, Recipient recipient, int executionTime);

    }

    interface Operation {

        @FunctionalInterface
        interface Unilateral {

            Status transact(Initiator initiator, double total);

        }

        @FunctionalInterface
        interface Bilateral {

            Status transact(Initiator initiator, Recipient recipient, double total);

        }

    }

    interface Initiator {

        String getID();

        double getBalance();

        boolean addFunds(double total);

        boolean deductFunds(double total);

    }

    interface Recipient {

        String getID();

        boolean addFunds(double total);

        double getBalance();

    }

    enum Status {

        FAILED, SUCCESSFUL;

        public static TransactionStatusData toDTO(Status status) {
            TransactionStatusData result = null;
            switch (status) {
                case FAILED:
                    result = TransactionStatusData.FAILED;
                    break;
                case SUCCESSFUL:
                    result = TransactionStatusData.SUCCESSFUL;
                    break;
            }
            return result;
        }

    }
}
