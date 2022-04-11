package com.alternabank.ui.form.main;

import com.alternabank.ui.form.AbstractOptionsForm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MainForm extends AbstractOptionsForm<MainForm.Option> {
    public MainForm() {
        super("Enter command to execute:" + System.lineSeparator()
                + "XML - load system from xml file" + System.lineSeparator()
                + "LOANS - display loan data" + System.lineSeparator()
                + "CUSTOMERS - display customer data" + System.lineSeparator()
                + "DEPOSIT - deposit funds to customer account" + System.lineSeparator()
                + "WITHDRAW - withdraw funds from customer account" + System.lineSeparator()
                + "INVEST - perform lender-loan assignment" + System.lineSeparator()
                + "ADVANCE - advance time" + System.lineSeparator()
                + "EXIT - exit program", new HashSet<>(Arrays.asList(Option.values())));
    }

    @Override
    protected boolean validateUserResponse() {
        return MainForm.Option.parse(getUserResponse()).isPresent();
    }

    @Override
    public Option getResults() {
        return Option.parse(getUserResponse()).get();
    }

    public enum Option {
        LOAD_XML_FROM_FILE("XML", "LOAD", "FILE",
                "LOAD XML", "LOAD FILE", "LOAD SYSTEM", "LOAD DATA",
                "LOAD XML FILE", "LOAD DATA FILE", "LOAD FROM FILE", "LOAD FROM XML FILE",
                "LOAD XML FROM FILE", "LOAD SYSTEM FROM FILE", "LOAD DATA FROM FILE"),
        DISPLAY_LOANS("LOANS", "LOAN DATA", "DISPLAY LOANS", "DISPLAY LOAN DATA"),
        DISPLAY_CUSTOMERS("CUSTOMERS", "CUSTOMER DATA",
                "DISPLAY CUSTOMERS", "DISPLAY CUSTOMER DATA",
                "SHOW CUSTOMERS", "SHOW CUSTOMER DATA"),
        DEPOSIT_FUNDS("DEPOSIT", "DEPOSIT FUNDS",
                "DEPOSIT TO CUSTOMER", "DEPOSIT FUNDS TO CUSTOMER",
                "DEPOSIT FUNDS TO CUSTOMER ACCOUNT"),
        WITHDRAW_FUNDS("WITHDRAW", "WITHDRAW FUNDS",
                "WITHDRAW FROM CUSTOMER", "WITHDRAW FUNDS FROM CUSTOMER",
                "WITHDRAW FUNDS FROM CUSTOMER ACCOUNT"),
        LOAN_ASSIGNMENT("ASSIGN", "INVEST", "NEW INVESTMENT", "NEW LOAN ASSIGNMENT",
                "ASSIGN LOANS", "INVEST FUNDS",
                "ASSIGN LENDER TO LOANS", "INVEST FUNDS IN LOANS",
                "ASSIGN LOANS TO LENDER"),
        ADVANCE_TIME("ADVANCE", "TIME", "ADVANCE TIME", "TIME ADVANCEMENT"),
        EXIT("EXIT", "QUIT", "ESCAPE");

        private final Set<String> stringRepresentations;

        Option(String... stringRepresentations) {
            this.stringRepresentations = new HashSet<>(Arrays.asList(stringRepresentations));
        }

        public Set<String> getStringRepresentations() {
            return stringRepresentations;
        }

        public static Optional<MainForm.Option> parse(String string) {
            return Arrays.stream(values()).filter(value -> value.stringRepresentations.contains(string.toUpperCase())).findFirst();
        }
    }

}
