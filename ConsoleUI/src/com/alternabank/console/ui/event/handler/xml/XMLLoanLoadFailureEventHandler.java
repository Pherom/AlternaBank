package com.alternabank.console.ui.event.handler.xml;

import com.alternabank.console.ui.form.YesNoForm;
import com.alternabank.engine.xml.event.XMLLoanLoadFailureEvent;
import com.alternabank.engine.xml.event.listener.XMLLoanLoadFailureListener;
import com.alternabank.console.ui.form.error.ErrorDialogue;
import com.alternabank.console.ui.form.error.xml.XMLLoanLoadErrorDialogue;

public class XMLLoanLoadFailureEventHandler implements XMLLoanLoadFailureListener {

    @Override
    public void loanLoadFailed(XMLLoanLoadFailureEvent event) {
        ErrorDialogue errorDialogue = new XMLLoanLoadErrorDialogue(event);
        errorDialogue.display();
        if(errorDialogue.getResults() == YesNoForm.Option.NO) {
            event.getSource().stopLoading();
        }
    }

}
