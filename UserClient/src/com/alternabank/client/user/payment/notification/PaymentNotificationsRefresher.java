package com.alternabank.client.user.payment.notification;

import com.alternabank.client.util.json.JsonUtil;
import com.alternabank.client.util.time.ServerTimeUtil;
import com.alternabank.dto.customer.PaymentNotificationsAndVersion;
import com.alternabank.client.util.http.HttpClientUtil;
import com.google.gson.Gson;
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

public class PaymentNotificationsRefresher extends TimerTask {

    private Consumer<PaymentNotificationsAndVersion> paymentNotificationsAndVersionConsumer;

    private IntegerProperty paymentNotificationsVersion = new SimpleIntegerProperty(0);

    public PaymentNotificationsRefresher(Consumer<PaymentNotificationsAndVersion> paymentNotificationsAndVersionConsumer) {
        this.paymentNotificationsAndVersionConsumer = paymentNotificationsAndVersionConsumer;
    }

    @Override
    public void run() {
        String finalUrl;
        if (!ServerTimeUtil.isRewindMode())
            finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/payment-notifications")
                    .newBuilder().addQueryParameter("version", String.valueOf(paymentNotificationsVersion.get())).build().toString();
        else finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/payment-notifications").newBuilder().build().toString();
        HttpClientUtil.runGetAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    PaymentNotificationsAndVersion paymentNotificationsAndVersion = JsonUtil.GSON_INSTANCE.fromJson(response.body().string(), PaymentNotificationsAndVersion.class);
                    if (paymentNotificationsAndVersion.getVersion() != paymentNotificationsVersion.get()) {
                        paymentNotificationsVersion.set(paymentNotificationsAndVersion.getVersion());
                        paymentNotificationsAndVersionConsumer.accept(paymentNotificationsAndVersion);
                    }
                }
            }
        });
    }
}
