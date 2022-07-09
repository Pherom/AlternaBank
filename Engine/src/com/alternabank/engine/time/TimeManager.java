package com.alternabank.engine.time;

import com.alternabank.engine.time.event.TimeAdvancementEvent;
import com.alternabank.engine.time.event.TimeReversalEvent;
import com.alternabank.engine.time.event.listener.TimeAdvancementListener;
import com.alternabank.engine.Engine;
import com.alternabank.engine.time.event.listener.TimeReversalListener;

import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;

public class TimeManager {

    public static String TIME_UNIT_NAME = "YAZ";
    private final Engine engine;
    private int currentTime = 0;

    private OptionalInt timeBeforeRewindMode = OptionalInt.empty();
    private final Set<TimeAdvancementListener> timeAdvancementListeners = new HashSet<>();

    private final Set<TimeReversalListener> timeReversalListeners = new HashSet<>();

    public TimeManager(Engine engine) {
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void resetCurrentTime() {
        currentTime = 0;
    }

    public void advanceTime() {
        if (!isRewindMode()) {
            engine.getLoanManager().saveLoanDetails();
            engine.getLoanManager().saveAvailableCategories();
            engine.getCustomerManager().saveCustomerDetails();
            currentTime++;
            timeAdvancementListeners.forEach(listener -> listener.timeAdvanced(new TimeAdvancementEvent(currentTime + 1)));
        }
        else if (currentTime + 1 == timeBeforeRewindMode.getAsInt()) {
            timeBeforeRewindMode = OptionalInt.empty();
            currentTime++;
        }
        else currentTime++;
    }

    public void reverseTime() {
        if (currentTime > 0) {
            if (!isRewindMode()) {
                engine.getLoanManager().saveLoanDetails();
                engine.getLoanManager().saveAvailableCategories();
                engine.getCustomerManager().saveCustomerDetails();
                timeBeforeRewindMode = OptionalInt.of(currentTime);
            }
            currentTime--;
            timeReversalListeners.forEach(listener -> listener.timeReversed(new TimeReversalEvent(currentTime)));
        }
    }

    public void addTimeAdvancementListener(TimeAdvancementListener listener) {
        timeAdvancementListeners.add(listener);
    }

    public void addTimeReversalListener(TimeReversalListener listener) {
        timeReversalListeners.add(listener);
    }

    public boolean isRewindMode() {
        return timeBeforeRewindMode.isPresent();
    }

    public OptionalInt getTimeBeforeRewindMode() {
        return timeBeforeRewindMode;
    }

}
