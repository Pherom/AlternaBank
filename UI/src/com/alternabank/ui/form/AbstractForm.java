package com.alternabank.ui.form;

import java.util.Scanner;

public abstract class AbstractForm<U> implements Form<U> {

    private final String userInstructions;
    private String userResponse;

    protected AbstractForm(String userInstructions) {
        this.userInstructions = userInstructions;
    }

    @Override
    public String getUserInstructions() {
        return userInstructions;
    }

    protected void readUserResponse() {
        this.userResponse = new Scanner(System.in).nextLine();
    }

    @Override
    public String getUserResponse() {
        return userResponse;
    }

    protected abstract boolean validateUserResponse();

    @Override
    public void display() {
        while(true) {
            System.out.println(userInstructions);
            readUserResponse();
            if(!validateUserResponse())
                System.out.println("Invalid user response!");
            else break;
        }
        System.out.println();
    }
}
