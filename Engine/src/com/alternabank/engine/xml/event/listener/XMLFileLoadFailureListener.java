package com.alternabank.engine.xml.event.listener;

import com.alternabank.engine.xml.event.XMLFileLoadFailureEvent;

public interface XMLFileLoadFailureListener {

    void fileLoadFailed(XMLFileLoadFailureEvent event);

}
