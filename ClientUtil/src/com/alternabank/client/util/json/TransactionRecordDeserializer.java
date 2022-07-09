package com.alternabank.client.util.json;

import com.alternabank.dto.transaction.BilateralTransactionRecord;
import com.alternabank.dto.transaction.TransactionRecord;
import com.alternabank.dto.transaction.UnilateralTransactionRecord;
import com.google.gson.*;

import java.lang.reflect.Type;

public class TransactionRecordDeserializer implements JsonDeserializer<TransactionRecord> {
    @Override
    public TransactionRecord deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        TransactionRecord result;
        if (jsonObject.has("recipientID"))
            result = jsonDeserializationContext.deserialize(jsonElement, BilateralTransactionRecord.class);
        else result = jsonDeserializationContext.deserialize(jsonElement, UnilateralTransactionRecord.class);
        return result;
    }
}
