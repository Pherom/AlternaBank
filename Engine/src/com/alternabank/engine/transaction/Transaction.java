package com.alternabank.engine.transaction;

public interface Transaction {

    int getID();

    double getTotal();

    interface Unilateral extends Transaction {

        Type getType();

        Record.Unilateral execute(Initiator initiator);

        interface Type {

            Operation.Unilateral getOperation();

        }

    }

    interface Bilateral extends Transaction {

        Type getType();

        Record.Bilateral execute(Initiator initiator, Recipient recipient);

        interface Type {

            Operation.Bilateral getOperation();

        }

    }

    interface Record {

        int getID();

        int getExecutionTime();

        String getInitiatorID();

        double getInitiatorBalanceBefore();

        double getInitiatorBalanceAfter();

        double getTotal();

        Status getStatus();

        interface Unilateral extends Record {

            Transaction.Unilateral.Type getType();

        }

        interface Bilateral extends Record{

            Transaction.Bilateral.Type getType();

            String getRecipientID();

            double getPrincipalPart();

            double getInterestPart();

            double getRecipientBalanceBefore();

            double getRecipientBalanceAfter();

        }

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

        FAILED, SUCCESSFUL

    }
}
