package com.alternabank.ui.form.xml;

import com.alternabank.ui.form.AbstractForm;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XMLLoadForm extends AbstractForm<Path> {

    public XMLLoadForm() {
        super("Please enter path of xml file to load:");
    }

    @Override
    protected boolean validateUserResponse() {
        try {
            Paths.get(getUserResponse());
        } catch (InvalidPathException e) {
            return false;
        }
        return true;
    }

    @Override
    public Path getResults() {
        return Paths.get(getUserResponse());
    }
}
