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
