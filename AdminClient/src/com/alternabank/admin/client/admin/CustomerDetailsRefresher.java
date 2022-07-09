package com.alternabank.admin.client.admin;

import com.alternabank.client.util.http.HttpClientUtil;
import com.alternabank.client.util.json.JsonUtil;
import com.alternabank.dto.customer.CustomerDetails;
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

public class CustomerDetailsRefresher extends TimerTask {

    private final Consumer<Map<String, CustomerDetails>> customerDetailsConsumer;

    public CustomerDetailsRefresher(Consumer<Map<String, CustomerDetails>> customerDetailsConsumer) {
        this.customerDetailsConsumer = customerDetailsConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/customers")
                .newBuilder().build().toString();
        HttpClientUtil.runGetAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    Map<String, CustomerDetails> customerDetails = JsonUtil.GSON_INSTANCE.fromJson(response.body().string(), new TypeToken<Map<String, CustomerDetails>>() {}.getType());
                    customerDetailsConsumer.accept(customerDetails);
                }
            }
        });
    }
}
