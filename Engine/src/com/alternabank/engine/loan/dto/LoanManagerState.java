package com.alternabank.engine.loan.dto;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;

import java.util.*;

public class LoanManagerState {

    private final Set<String> availableCategories = new HashSet<>();
    private final Map<String, Loan> loansByID = new HashMap<>();
    private final List<UnilateralTransactionListener> unilateralTransactionListeners = new LinkedList<>();
    private final List<BilateralTransactionListener> bilateralTransactionListeners = new LinkedList<>();

    public LoanManagerState(Set<String> availableCategories, Map<String, Loan> loansByID,
                            List<UnilateralTransactionListener> unilateralTransactionListeners,
                            List<BilateralTransactionListener> bilateralTransactionListeners) {
        this.availableCategories.addAll(availableCategories);
        this.loansByID.putAll(loansByID);
        this.unilateralTransactionListeners.addAll(unilateralTransactionListeners);
        this.bilateralTransactionListeners.addAll(bilateralTransactionListeners);
    }

    public Set<String> getAvailableCategories() {
        return availableCategories;
    }

    public Map<String, Loan> getLoansByID() {
        return loansByID;
    }

    public List<UnilateralTransactionListener> getUnilateralTransactionListeners() {
        return unilateralTransactionListeners;
    }

    public List<BilateralTransactionListener> getBilateralTransactionListeners() {
        return bilateralTransactionListeners;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanManagerState that = (LoanManagerState) o;
        return Objects.equals(availableCategories, that.availableCategories) && Objects.equals(loansByID, that.loansByID) && Objects.equals(unilateralTransactionListeners, that.unilateralTransactionListeners) && Objects.equals(bilateralTransactionListeners, that.bilateralTransactionListeners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(availableCategories, loansByID, unilateralTransactionListeners, bilateralTransactionListeners);
    }
}
