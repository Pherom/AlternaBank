package com.alternabank.console.ui.form;

import java.util.Set;

public interface OptionsForm<O extends Enum<O>> extends Form<O>{

    Set<O> getOptions();

}