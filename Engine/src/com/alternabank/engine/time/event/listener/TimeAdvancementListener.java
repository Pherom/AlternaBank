package com.alternabank.engine.time.event.listener;

import com.alternabank.engine.time.event.TimeAdvancementEvent;

public interface TimeAdvancementListener {

    void timeAdvanced(TimeAdvancementEvent event);

}
