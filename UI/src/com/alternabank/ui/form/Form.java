package com.alternabank.ui.form;

public interface Form<T> {

    String getUserInstructions();

    String getUserResponse();

    T getResults();

    void display();

}
