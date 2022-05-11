package com.alternabank.console.ui.form.error;

import com.alternabank.console.ui.form.YesNoForm;

public abstract class AbstractErrorDialogue extends YesNoForm implements ErrorDialogue {

    private final String errorMessage;

    protected AbstractErrorDialogue(String errorMessage, String userInstructions) {
        super(userInstructions);
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void display() {
        System.out.println("An error has occurred:");
        System.out.println(errorMessage);
        do {
            System.out.println(getUserInstructions());
            readUserResponse();
        }
        while(!validateUserResponse());
    }
}
