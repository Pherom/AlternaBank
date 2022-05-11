package com.alternabank.console.ui.form;

import java.util.Set;

public abstract class AbstractOptionsForm<O extends Enum<O>> extends AbstractForm<O> implements OptionsForm<O> {

    private final Set<O> options;

    protected AbstractOptionsForm(String userInstructions, Set<O> options) {
        super(userInstructions);
        this.options = options;
    }

    @Override
    public Set<O> getOptions() {
        return options;
    }
}
