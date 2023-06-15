package kanban.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class InstantAdapter extends TypeAdapter<Instant> {

    @Override
    public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
        if (instant == null) {
            jsonWriter.value("null");
        } else {
            jsonWriter.value(instant.toEpochMilli());
        }
    }

    @Override
    public Instant read(JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        if (value.equals("null")) {
            return null;
        } else {
            var timeAsLong = Long.parseLong(value);
            return Instant.ofEpochMilli(timeAsLong);
        }
    }
}
