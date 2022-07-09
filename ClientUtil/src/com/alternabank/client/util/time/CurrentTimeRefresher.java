package com.alternabank.client.util.time;

import com.alternabank.client.util.http.HttpClientUtil;
import com.alternabank.client.util.json.JsonUtil;
import com.alternabank.dto.time.ServerTime;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class CurrentTimeRefresher extends TimerTask {

    private final Consumer<ServerTime> timeConsumer;

    public CurrentTimeRefresher(Consumer<ServerTime> timeConsumer) {
        this.timeConsumer = timeConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse("http://localhost:8080/AlternaBank/current-time")
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runGetAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                timeConsumer.accept(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {

                }
                else {
                    ServerTime currentTime = JsonUtil.GSON_INSTANCE.fromJson(response.body().string(), ServerTime.class);
                    timeConsumer.accept(currentTime);
                }
            }
        });
    }
}
