package com.alternabank.engine.xml.event;

import com.alternabank.engine.xml.XMLLoader;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class XMLCategoryLoadFailureEvent extends XMLLoadFailureEvent<String> {

    public XMLCategoryLoadFailureEvent(XMLLoader source, List<XMLLoadFailureEvent.Cause<String>> causes, String trigger) {
        super(source, causes, trigger);
    }

    public enum Cause implements XMLLoadFailureEvent.Cause<String> {

        EMPTY_LOAN_CATEGORY((absCategory) -> absCategory.isEmpty(), (absCategory) -> "Found empty category!");

        private final Predicate<String> predicate;
        private final Function<String, String> errorMessageGenerator;

        Cause(Predicate<String> predicate, Function<String, String> errorMessageGenerator) {
            this.predicate = predicate;
            this.errorMessageGenerator = errorMessageGenerator;
        }

        @Override
        public Predicate<String> getPredicate() {
            return predicate;
        }

        @Override
        public String getErrorMessage(String trigger) {
            return errorMessageGenerator.apply(trigger);
        }

    }

}
