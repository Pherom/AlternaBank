package com.alternabank.engine.xml.event.listener;

import com.alternabank.engine.xml.event.XMLLoanLoadFailureEvent;

import java.util.EventListener;

public interface XMLLoanLoadFailureListener extends EventListener {

    void loanLoadFailed(XMLLoanLoadFailureEvent event);

}
