package com.alternabank.ui.message.time;

import com.alternabank.ui.message.AbstractMessage;

public class CurrentTimeMessage extends AbstractMessage {
    public CurrentTimeMessage(int currentTime, String timeUnit) {
        super(String.format("Current time: %s %d", timeUnit, currentTime));
    }
}
