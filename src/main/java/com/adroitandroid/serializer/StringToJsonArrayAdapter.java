package com.adroitandroid.serializer;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by pv on 03/12/16.
 */
public class StringToJsonArrayAdapter extends TypeAdapter<String> {
    /**
     * Writes one JSON value (an array, object, string, number, boolean or null)
     * for {@code value}.
     *
     * @param out
     * @param value the Java object to write. May be null.
     */
    @Override
    public void write(JsonWriter out, String value) throws IOException {
        out.beginArray();
        if (value != null) {
            List<Long> valueAsLongList;
            Type collectionType = new TypeToken<List<Long>>() {
            }.getType();
            Gson gson = new Gson();
            valueAsLongList = gson.fromJson(value, collectionType);
            for (Long currentVal : valueAsLongList) {
                out.value(currentVal);
            }
        }
        out.endArray();
    }

    /**
     * Reads one JSON value (an array, object, string, number, boolean or null)
     * and converts it to a Java object. Returns the converted object.
     *
     * @param in
     * @return the converted Java object. May be null.
     */
    @Override
    public String read(JsonReader in) throws IOException {
        in.beginArray();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        while (in.hasNext()) {
            stringBuilder.append(in.nextLong());
            if (in.hasNext()) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");
        in.endArray();
        return stringBuilder.toString();
    }
}
