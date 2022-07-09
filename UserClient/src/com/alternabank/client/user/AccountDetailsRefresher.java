package com.alternabank.client.user;

import com.alternabank.client.util.json.JsonUtil;
import com.alternabank.client.util.time.ServerTimeUtil;
import com.alternabank.dto.account.AccountDetails;
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

public class AccountDetailsRefresher extends TimerTask {

    private final Consumer<AccountDetails> accountDetailsConsumer;

    private final IntegerProperty ledgerVersion = new SimpleIntegerProperty(0);

    public AccountDetailsRefresher(Consumer<AccountDetails> accountDetailsConsumer) {
        this.accountDetailsConsumer = accountDetailsConsumer;
    }

    @Override
    public void run() {
        String finalUrl;
        if (!ServerTimeUtil.isRewindMode())
            finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/account")
                        .newBuilder().addQueryParameter("ledger-ver", String.valueOf(ledgerVersion.get())).build().toString();
        else finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/account").newBuilder().build().toString();
        HttpClientUtil.runGetAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    AccountDetails accountDetails = JsonUtil.GSON_INSTANCE.fromJson(response.body().string(), AccountDetails.class);
                    if (ledgerVersion.get() != accountDetails.getLedgerVersion()) {
                        ledgerVersion.set(accountDetails.getLedgerVersion());
                        accountDetailsConsumer.accept(accountDetails);
                    }
                }
            }
        });
    }
}
