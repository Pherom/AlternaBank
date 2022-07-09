package com.alternabank.engine.xml.result.failue;

import java.util.function.BiPredicate;

public interface XMLLoadFailureCause<T, U> {

    BiPredicate<T, U> getPredicate();

    String getErrorMessage(T trigger);

}
