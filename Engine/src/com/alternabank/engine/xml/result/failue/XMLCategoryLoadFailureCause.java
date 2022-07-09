package com.alternabank.engine.xml.result.failue;

import com.alternabank.engine.Engine;

import java.util.function.BiPredicate;
import java.util.function.Function;

public enum XMLCategoryLoadFailureCause implements XMLLoadFailureCause<String, Engine> {

    EMPTY_LOAN_CATEGORY((absCategory, engine) -> absCategory.isEmpty(), (absCategory) -> "Found empty category!");

    private final BiPredicate<String, Engine> predicate;
    private final Function<String, String> errorMessageGenerator;

    XMLCategoryLoadFailureCause(BiPredicate<String, Engine> predicate, Function<String, String> errorMessageGenerator) {
        this.predicate = predicate;
        this.errorMessageGenerator = errorMessageGenerator;
    }

    @Override
    public BiPredicate<String, Engine> getPredicate() {
        return predicate;
    }

    @Override
    public String getErrorMessage(String trigger) {
        return errorMessageGenerator.apply(trigger);
    }

}
