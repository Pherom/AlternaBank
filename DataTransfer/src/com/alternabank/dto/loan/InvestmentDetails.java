package com.alternabank.dto.loan;

import java.util.Objects;

public class InvestmentDetails {

    private final String lenderName;

    private final String loanID;

    private final double investmentTotal;

    private final boolean forSale;

    public InvestmentDetails(String lenderName, String loanID, double investmentTotal, boolean forSale) {
        this.lenderName = lenderName;
        this.loanID = loanID;
        this.investmentTotal = investmentTotal;
        this.forSale = forSale;
    }

    public String getLenderName() {
        return lenderName;
    }

    public String getLoanID() {
        return loanID;
    }

    public double getInvestmentTotal() {
        return investmentTotal;
    }

    public boolean getForSale() {
        return forSale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvestmentDetails that = (InvestmentDetails) o;
        return Double.compare(that.investmentTotal, investmentTotal) == 0 && forSale == that.forSale && Objects.equals(lenderName, that.lenderName) && Objects.equals(loanID, that.loanID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lenderName, loanID, investmentTotal, forSale);
    }
}
