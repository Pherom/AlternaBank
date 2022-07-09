package com.alternabank.dto.time;

import java.util.OptionalInt;

public class ServerTime {

    private final String timeUnitName;

    private final int time;

    private final boolean rewindMode;

    public ServerTime(String timeUnitName, int time, boolean rewindMode) {
        this.timeUnitName = timeUnitName;
        this.time = time;
        this.rewindMode = rewindMode;
    }

    public String getTimeUnitName() {
        return timeUnitName;
    }

    public int getTime() {
        return time;
    }

    public boolean isRewindMode() {
        return rewindMode;
    }
}
