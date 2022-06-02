package com.alternabank.engine.loan.state;

import com.alternabank.engine.loan.Loan;

import java.util.*;

public class LoanManagerState {

    private final Set<String> availableCategories = new HashSet<>();
    private final Map<String, Loan> loansByID = new HashMap<>();

    public LoanManagerState(Set<String> availableCategories, Map<String, Loan> loansByID) {
        this.availableCategories.addAll(availableCategories);
        this.loansByID.putAll(loansByID);
    }

    public Set<String> getAvailableCategories() {
        return availableCategories;
    }

    public Map<String, Loan> getLoansByID() {
        return loansByID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanManagerState that = (LoanManagerState) o;
        return Objects.equals(availableCategories, that.availableCategories) && Objects.equals(loansByID, that.loansByID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(availableCategories, loansByID);
    }
}
