package com.alternabank.console.ui.event.handler.xml;

import com.alternabank.console.ui.message.Message;
import com.alternabank.console.ui.message.xml.XMLLoadSuccessMessage;
import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;
import com.alternabank.engine.xml.event.listener.XMLLoadSuccessListener;

public class XMLLoadSuccessEventHandler implements XMLLoadSuccessListener {

    @Override
    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        Message loadSuccessMessage = new XMLLoadSuccessMessage();
        loadSuccessMessage.display();
    }

}
