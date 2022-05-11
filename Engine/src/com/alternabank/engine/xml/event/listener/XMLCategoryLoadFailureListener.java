package com.alternabank.engine.xml.event.listener;

import com.alternabank.engine.xml.event.XMLCategoryLoadFailureEvent;

import java.util.EventListener;

public interface XMLCategoryLoadFailureListener extends EventListener {

    void categoryLoadFailed(XMLCategoryLoadFailureEvent event);

}
