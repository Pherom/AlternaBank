package com.alternabank.engine.xml.event.listener;

import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;

import java.util.EventListener;

public interface XMLLoadSuccessListener extends EventListener {

    void loadedSuccessfully(XMLLoadSuccessEvent event);

}
