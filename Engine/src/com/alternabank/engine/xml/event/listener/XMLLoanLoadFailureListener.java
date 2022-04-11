package com.alternabank.engine.xml.event.listener;

import com.alternabank.engine.xml.event.XMLLoanLoadFailureEvent;

public interface XMLLoanLoadFailureListener {

    void loanLoadFailed(XMLLoanLoadFailureEvent event);

}
