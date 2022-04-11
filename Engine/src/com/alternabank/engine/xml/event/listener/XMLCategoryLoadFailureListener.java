package com.alternabank.engine.xml.event.listener;

import com.alternabank.engine.xml.event.XMLCategoryLoadFailureEvent;

public interface XMLCategoryLoadFailureListener {

    void categoryLoadFailed(XMLCategoryLoadFailureEvent event);

}
