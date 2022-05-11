package com.alternabank.engine.xml.event.listener;

import com.alternabank.engine.xml.event.XMLCustomerLoadFailureEvent;

import java.util.EventListener;

public interface XMLCustomerLoadFailureListener extends EventListener {

    void customerLoadFailed(XMLCustomerLoadFailureEvent event);

}
