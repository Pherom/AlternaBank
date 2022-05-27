package com.alternabank.graphical.ui.application.user.util;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class DoubleTextFormatter extends TextFormatter<Double>{

    public DoubleTextFormatter(Double defaultValue) {
        super(new StringConverter<Double>() {
            @Override
            public Double fromString(String s) {
                if (s.isEmpty())
                    return null;
                else if(".".equals(s))
                    return 0.0;
                else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d == null ? "" : d.toString();
            }

        }, defaultValue, change -> {
            String text = change.getControlNewText();
            if (Pattern.compile("(([1-9][0-9]*)|0)?(\\.[0-9]*)?").matcher(text).matches()) {
                return change ;
            } else {
                return null ;
            }
        });

    }

}
