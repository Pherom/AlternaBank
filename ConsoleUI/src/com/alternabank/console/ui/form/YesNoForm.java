package com.alternabank.console.ui.form;

import java.util.*;

public class YesNoForm extends AbstractOptionsForm<YesNoForm.Option> {

    public YesNoForm(String userInstructions) {
        super(userInstructions, new HashSet<>(Arrays.asList(Option.values())));
    }

    @Override
    protected boolean validateUserResponse() {
        return Option.parse(getUserResponse()).isPresent();
    }

    @Override
    public Option getResults() {
        return Option.parse(getUserResponse()).get();
    }

    public enum Option {
        YES("Y", "YES"),
        NO("N", "NO");

        private final Set<String> stringRepresentations;

        Option(String... stringRepresentations) {
            this.stringRepresentations = new HashSet<>(Arrays.asList(stringRepresentations));
        }

        public Set<String> getStringRepresentations() {
            return stringRepresentations;
        }

        public static Optional<Option> parse(String string) {
            return Arrays.stream(values()).filter(value -> value.stringRepresentations.contains(string.toUpperCase())).findFirst();
        }
    }
}
