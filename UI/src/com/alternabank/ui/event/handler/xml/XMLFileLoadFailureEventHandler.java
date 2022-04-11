package com.alternabank.ui.event.handler.xml;

import com.alternabank.engine.xml.event.XMLFileLoadFailureEvent;
import com.alternabank.engine.xml.event.listener.XMLFileLoadFailureListener;
import com.alternabank.ui.form.YesNoForm;
import com.alternabank.ui.form.Form;
import com.alternabank.ui.form.xml.XMLLoadForm;
import com.alternabank.ui.form.error.ErrorDialogue;
import com.alternabank.ui.form.error.xml.XMLFileLoadErrorDialogue;

import java.nio.file.Path;

public class XMLFileLoadFailureEventHandler implements XMLFileLoadFailureListener {

    @Override
    public void fileLoadFailed(XMLFileLoadFailureEvent event) {
        ErrorDialogue errorDialogue = new XMLFileLoadErrorDialogue(event);
        errorDialogue.display();
        Path lastLoadedFilePath = event.getSource().getLastLoadedFilePath();

        if(lastLoadedFilePath == null && errorDialogue.getResults() == YesNoForm.Option.NO)
            System.out.println("A file must be loaded at least one time to continue!");

        if(lastLoadedFilePath == null || errorDialogue.getResults() == YesNoForm.Option.YES) {
            Form<Path> xmlLoadForm = new XMLLoadForm();
            xmlLoadForm.display();
            event.getSource().loadSystemFromFile(xmlLoadForm.getResults());
        }
    }
}
