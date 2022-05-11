package com.alternabank.engine.xml.event.listener;

import com.alternabank.engine.xml.event.XMLFileLoadFailureEvent;

import java.util.EventListener;

public interface XMLFileLoadFailureListener extends EventListener {

    void fileLoadFailed(XMLFileLoadFailureEvent event);

}
