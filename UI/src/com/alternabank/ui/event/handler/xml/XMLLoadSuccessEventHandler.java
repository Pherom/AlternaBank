package com.alternabank.ui.event.handler.xml;

import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;
import com.alternabank.engine.xml.event.listener.XMLLoadSuccessListener;
import com.alternabank.ui.message.Message;
import com.alternabank.ui.message.xml.XMLLoadSuccessMessage;

public class XMLLoadSuccessEventHandler implements XMLLoadSuccessListener {

    @Override
    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        Message loadSuccessMessage = new XMLLoadSuccessMessage();
        loadSuccessMessage.display();
    }

}
