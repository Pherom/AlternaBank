package com.alternabank.engine.time.event.listener;

import com.alternabank.engine.time.event.TimeReversalEvent;

public interface TimeReversalListener {

    void timeReversed(TimeReversalEvent event);

}
