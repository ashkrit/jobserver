package jobserver.client.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import static jobserver.client.lang.Panic.raise;

public class JsonConverter {

    public static <T> String toJson(T value) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            raise(e);
        }
        return null;
    }

}
