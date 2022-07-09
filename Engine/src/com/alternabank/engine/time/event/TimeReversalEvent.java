package com.alternabank.engine.time.event;

import java.util.Objects;

public class TimeReversalEvent {

    private final int timeAfter;

    public TimeReversalEvent(int timeAfter) {
        this.timeAfter = timeAfter;
    }

    public int getTimeBefore() {
        return timeAfter + 1;
    }

    public int getTimeAfter() {
        return timeAfter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeReversalEvent that = (TimeReversalEvent) o;
        return timeAfter == that.timeAfter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeAfter);
    }
}
