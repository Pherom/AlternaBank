package com.alternabank.client.util.http;

import com.alternabank.client.util.http.cookie.SimpleCookieManager;
import okhttp3.*;

import java.io.IOException;

public class HttpClientUtil {
    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public static void runGetAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static Response runPutSync(String finalUrl, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .url(finalUrl)
                .put(requestBody)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        return call.execute();
    }

    public static Response runPostSync(String finalUrl, RequestBody responseBody) throws IOException {
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(responseBody)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        return call.execute();
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
