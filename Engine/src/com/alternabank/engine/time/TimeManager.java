package com.alternabank.engine.time;

import com.alternabank.engine.time.event.TimeAdvancementEvent;
import com.alternabank.engine.time.event.listener.TimeAdvancementListener;
import com.alternabank.engine.user.Admin;

import java.util.HashSet;
import java.util.Set;

public class TimeManager {

    private final Admin admin;
    private int currentTime = 0;
    private final Set<TimeAdvancementListener> timeAdvancementListeners = new HashSet<>();

    public TimeManager(Admin admin) {
        this.admin = admin;
    }

    public Admin getAdmin() {
        return admin;
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
