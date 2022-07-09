package com.alternabank.client.util.json;

import com.alternabank.dto.transaction.TransactionRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {

    public static final Gson GSON_INSTANCE = new GsonBuilder().registerTypeAdapter(TransactionRecord.class, new TransactionRecordDeserializer()).create();

}
