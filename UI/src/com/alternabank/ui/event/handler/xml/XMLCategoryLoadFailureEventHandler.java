package com.alternabank.ui.event.handler.xml;

import com.alternabank.engine.xml.event.XMLCategoryLoadFailureEvent;
import com.alternabank.engine.xml.event.listener.XMLCategoryLoadFailureListener;
import com.alternabank.ui.form.YesNoForm;
import com.alternabank.ui.form.error.ErrorDialogue;
import com.alternabank.ui.form.error.xml.XMLCategoryLoadErrorDialogue;

public class XMLCategoryLoadFailureEventHandler implements XMLCategoryLoadFailureListener {

    @Override
    public void categoryLoadFailed(XMLCategoryLoadFailureEvent event) {
        ErrorDialogue errorDialogue = new XMLCategoryLoadErrorDialogue(event);
        errorDialogue.display();
        if(errorDialogue.getResults() == YesNoForm.Option.NO) {
            event.getSource().stopLoading();
        }
    }

}
