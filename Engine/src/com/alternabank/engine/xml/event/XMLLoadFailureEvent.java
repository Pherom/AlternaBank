package com.alternabank.engine.xml.event;

import com.alternabank.engine.xml.XMLLoader;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class XMLLoadFailureEvent<U> {

    private final XMLLoader source;
    private final List<Cause<U>> causes;
    private final U trigger;

    public interface Cause<T> {

        Predicate<T> getPredicate();

        String getErrorMessage(T trigger);

    }

    protected XMLLoadFailureEvent(XMLLoader source, List<Cause<U>> causes, U trigger) {
        this.source = source;
        this.causes = causes;
        this.trigger = trigger;
    }

    public XMLLoader getSource() {
        return source;
    }

    public List<Cause<U>> getCauses() {
        return causes;
    }

    public String getErrorMessage() {
        StringBuilder errorMessage = new StringBuilder();
        causes.forEach(cause -> errorMessage.append(cause.getErrorMessage(trigger)).append(System.lineSeparator()));
        return errorMessage.toString();
    }

    public U getTrigger() {
        return trigger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XMLLoadFailureEvent<?> that = (XMLLoadFailureEvent<?>) o;
        return Objects.equals(source, that.source) && Objects.equals(causes, that.causes) && Objects.equals(trigger, that.trigger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, causes, trigger);
    }
}
