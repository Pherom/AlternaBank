package com.alternabank.engine.xml.event;

import com.alternabank.engine.xml.XMLLoader;

public class XMLLoadSuccessEvent {

    private final XMLLoader source;

    public XMLLoadSuccessEvent(XMLLoader source) {
        this.source = source;
    }

    public XMLLoader getSource() {
        return source;
    }

}
