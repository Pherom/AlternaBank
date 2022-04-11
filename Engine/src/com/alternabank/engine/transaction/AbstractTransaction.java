package com.alternabank.engine.transaction;

import java.util.Objects;

public abstract class AbstractTransaction implements Transaction {

    private static int nextID = 1;
    private final int id;
    private final double total;

    protected AbstractTransaction(double total) {
        this.total = total;
        this.id = nextID++;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public double getTotal() {
        return total;
    }

    public abstract class Record implements Transaction.Record {

        private final int executionTime;
        private final String initiatorID;
        private final double initiatorBalanceBefore;
        private final double initiatorBalanceAfter;
        private final Status status;

        protected Record(int executionTime, String initiatorID, double initiatorBalanceBefore, double initiatorBalanceAfter, Status status) {
            this.executionTime = executionTime;
            this.initiatorID = initiatorID;
            this.initiatorBalanceBefore = initiatorBalanceBefore;
            this.initiatorBalanceAfter = initiatorBalanceAfter;
            this.status = status;
        }

        @Override
        public int getID() {
            return id;
        }

        @Override
        public int getExecutionTime() {
            return executionTime;
        }

        @Override
        public String getInitiatorID() {
            return initiatorID;
        }

        @Override
        public double getTotal() {
            return total;
        }

        @Override
        public Status getStatus() {
            return status;
        }

        @Override
        public double getInitiatorBalanceBefore() {
            return initiatorBalanceBefore;
        }

        @Override
        public double getInitiatorBalanceAfter() {
            return initiatorBalanceAfter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Record record = (Record) o;
            return id == record.getID();
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTransaction that = (AbstractTransaction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
