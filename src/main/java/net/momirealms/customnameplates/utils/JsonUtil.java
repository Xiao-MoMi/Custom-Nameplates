package net.momirealms.customnameplates.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class JsonUtil {

    public static void getText(JsonElement jsonElement, StringBuilder stringBuilder) {

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> set : jsonObject.entrySet()) {
                if (set.getKey().equals("text")) {
                    stringBuilder.append(set.getValue().getAsString());
                }
                else {
                    getText(set.getValue(), stringBuilder);
                }
            }
        }
        else if (jsonElement.isJsonArray()) {
            for (JsonElement jsonElement1 : jsonElement.getAsJsonArray()) {
                getText(jsonElement1, stringBuilder);
            }
        }
    }
}