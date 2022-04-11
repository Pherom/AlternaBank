package com.alternabank.ui.form.error.xml;

import com.alternabank.engine.xml.event.XMLFileLoadFailureEvent;
import com.alternabank.ui.form.error.AbstractErrorDialogue;

public class XMLFileLoadErrorDialogue extends AbstractErrorDialogue {

    public XMLFileLoadErrorDialogue(XMLFileLoadFailureEvent event) {
        super(event.getErrorMessage(), "Load different file? (Y/N):");
    }

}
