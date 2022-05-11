package com.alternabank.console.ui.form.error;

import com.alternabank.console.ui.form.YesNoForm;
import com.alternabank.console.ui.form.OptionsForm;

public interface ErrorDialogue extends OptionsForm<YesNoForm.Option> {

    String getErrorMessage();

}
