package com.alternabank.engine.account;

import java.util.Objects;

public abstract class AbstractDepositAccount extends AbstractAccount implements DepositAccount {

    private final Deposit deposit;

    protected AbstractDepositAccount(String id, String depositID) {
        super(id);
        this.deposit = new Deposit(depositID);
    }

    @Override
    public Deposit getDeposit() {
        return deposit;
    }

    @Override
    public String toString() {
        return String.format("ACCOUNT DETAILS:" + System.lineSeparator()
                        + "\tID: %s" + System.lineSeparator()
                        + "\tDeposit: %s" + System.lineSeparator()
                        + "\tBalance: %.2f" + System.lineSeparator()
                        + "\t%s",
                getID(), deposit.getID(), getBalance(), getLedger().toString().replace(System.lineSeparator(), System.lineSeparator() + "\t"));
    }

    public class Deposit implements DepositAccount.Deposit {

        private final String id;

        protected Deposit(String id) {
            this.id = id;
        }

        @Override
        public String getID() {
            return id;
        }

        @Override
        public DepositAccount getAccount() {
            return AbstractDepositAccount.this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Deposit deposit = (Deposit) o;
            return Objects.equals(id, deposit.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
