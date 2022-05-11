package com.alternabank.console.ui.form;

public interface Form<T> {

    String getUserInstructions();

    String getUserResponse();

    T getResults();

    void display();

}
