package com.alternabank.client.util.loan;

import com.alternabank.client.util.http.HttpClientUtil;
import com.alternabank.client.util.json.JsonUtil;
import com.alternabank.dto.loan.LoanDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

public class LoanDetailsRefresher extends TimerTask {

    private Consumer<Map<String, LoanDetails>> loanDetailsConsumer;

    public LoanDetailsRefresher(Consumer<Map<String, LoanDetails>> loanDetailsConsumer) {
        this.loanDetailsConsumer = loanDetailsConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/loans")
                .newBuilder().build().toString();
        HttpClientUtil.runGetAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    Map<String, LoanDetails> loanDetails = JsonUtil.GSON_INSTANCE.fromJson(response.body().string(), new TypeToken<Map<String, LoanDetails>>() {}.getType());
                    loanDetailsConsumer.accept(loanDetails);
                }
            }
        });
    }
}
