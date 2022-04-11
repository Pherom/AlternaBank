package com.alternabank.engine.time;

import com.alternabank.engine.time.event.TimeAdvancementEvent;
import com.alternabank.engine.time.event.listener.TimeAdvancementListener;

import java.util.HashSet;
import java.util.Set;

public class TimeManager {

    private static TimeManager instance = null;
    private int currentTime = 0;
    private final Set<TimeAdvancementListener> timeAdvancementListeners = new HashSet<>();

    public static TimeManager getInstance() {
        if(instance == null)
            instance = new TimeManager();
        return instance;
    }

    private TimeManager() {
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void resetCurrentTime() {
        currentTime = 0;
    }

    public String getTimeUnitName() {
        return "YAZ";
    }

    public void advanceTime() {
        currentTime++;
        timeAdvancementListeners.forEach(listener -> listener.timeAdvanced(new TimeAdvancementEvent(currentTime)));
    }

    public void addTimeAdvancementListener(TimeAdvancementListener listener) {
        timeAdvancementListeners.add(listener);
    }

}
