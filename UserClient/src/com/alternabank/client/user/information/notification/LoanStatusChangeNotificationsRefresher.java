package com.alternabank.client.user.information.notification;

import com.alternabank.client.util.http.HttpClientUtil;
import com.alternabank.client.util.json.JsonUtil;
import com.alternabank.dto.customer.LoanStatusChangeNotificationsAndVersion;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Alert;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class LoanStatusChangeNotificationsRefresher extends TimerTask {

    private IntegerProperty loanStatusChangeNotificationsVersion = new SimpleIntegerProperty(0);

    private final Consumer<LoanStatusChangeNotificationsAndVersion> loanStatusChangeNotificationsAndVersionConsumer;

    public LoanStatusChangeNotificationsRefresher(Consumer<LoanStatusChangeNotificationsAndVersion> loanStatusChangeNotificationsAndVersionConsumer) {
        this.loanStatusChangeNotificationsAndVersionConsumer = loanStatusChangeNotificationsAndVersionConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/loan-status-notifications")
                .newBuilder().addQueryParameter("version", String.valueOf(loanStatusChangeNotificationsVersion.get())).build().toString();
        HttpClientUtil.runGetAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    LoanStatusChangeNotificationsAndVersion loanStatusChangeNotificationsAndVersion = JsonUtil.GSON_INSTANCE.fromJson(response.body().string(), LoanStatusChangeNotificationsAndVersion.class);
                    if (loanStatusChangeNotificationsAndVersion.getVersion() != loanStatusChangeNotificationsVersion.get()) {
                        loanStatusChangeNotificationsVersion.set(loanStatusChangeNotificationsAndVersion.getVersion());
                        loanStatusChangeNotificationsAndVersionConsumer.accept(loanStatusChangeNotificationsAndVersion);
                    }
                }
            }
        });
    }
}
