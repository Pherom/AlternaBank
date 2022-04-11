package com.alternabank.engine.xml.event.listener;

import com.alternabank.engine.xml.event.XMLCustomerLoadFailureEvent;

public interface XMLCustomerLoadFailureListener {

    void customerLoadFailed(XMLCustomerLoadFailureEvent event);

}
