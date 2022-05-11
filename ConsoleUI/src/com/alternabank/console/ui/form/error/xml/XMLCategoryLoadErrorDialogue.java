package com.alternabank.console.ui.form.error.xml;

import com.alternabank.engine.xml.event.XMLCategoryLoadFailureEvent;
import com.alternabank.console.ui.form.error.AbstractErrorDialogue;

public class XMLCategoryLoadErrorDialogue extends AbstractErrorDialogue {
    public XMLCategoryLoadErrorDialogue(XMLCategoryLoadFailureEvent event) {
        super(event.getErrorMessage(), "Skip? (Y/N) (Not skipping will result in load cancellation):");
    }
}
