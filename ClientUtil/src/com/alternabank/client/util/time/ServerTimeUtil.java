package com.alternabank.client.util.time;

import com.alternabank.dto.time.ServerTime;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;

import java.util.Timer;
import java.util.TimerTask;

public class ServerTimeUtil {

    private static TimerTask currentTimeRefresher;

    private static Timer timer;

    public static final BooleanProperty rewindMode = new SimpleBooleanProperty();

    public static final IntegerProperty currentTime = new SimpleIntegerProperty();

    public static final StringProperty timeUnitName = new SimpleStringProperty();

    private static void updateCurrentTime(ServerTime currentTime) {
        Platform.runLater(() -> {
            ServerTimeUtil.currentTime.set(currentTime.getTime());
            timeUnitName.set(currentTime.getTimeUnitName());
            rewindMode.set(currentTime.isRewindMode());
        });
    }

    public static void startCurrentTimeRefresher() {
        currentTimeRefresher = new CurrentTimeRefresher(
                ServerTimeUtil::updateCurrentTime);
        timer = new Timer();
        timer.schedule(currentTimeRefresher, 500, 500);
    }

    public static boolean isRewindMode() {
        return rewindMode.get();
    }

}
