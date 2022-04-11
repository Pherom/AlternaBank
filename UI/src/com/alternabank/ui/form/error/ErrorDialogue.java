package com.alternabank.ui.form.error;

import com.alternabank.ui.form.OptionsForm;
import com.alternabank.ui.form.YesNoForm;

public interface ErrorDialogue extends OptionsForm<YesNoForm.Option> {

    String getErrorMessage();

}
