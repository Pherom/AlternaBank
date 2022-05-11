package com.alternabank.console.ui.message.time;

import com.alternabank.console.ui.message.AbstractMessage;

public class CurrentTimeMessage extends AbstractMessage {
    public CurrentTimeMessage(int currentTime, String timeUnit) {
        super(String.format("Current time: %s %d", timeUnit, currentTime));
    }
}
