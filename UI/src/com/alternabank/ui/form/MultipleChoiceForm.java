package com.alternabank.ui.form;

import java.util.Set;

public interface MultipleChoiceForm<T> extends Form<Set<T>> {

    void displayUserSelection(T selection);

}