package jobserver.client.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static jobserver.client.lang.Panic.raise;

public class JsonConverter {

    public static <T> String toJson(T value) {
        try {
            ObjectMapper mapper = makeNewObjectMapper();
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            raise(e);
        }
        return null;
    }

    public static <T> T fromJson(byte[] data, Class<T> type) {
        try {
            ObjectMapper mapper = makeNewObjectMapper();

            configure(mapper);
            return mapper.readValue(data, type);
        } catch (Exception e) {
            raise(e);
        }
        return null;
    }

    private static ObjectMapper makeNewObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        configure(mapper);
        return mapper;
    }

    private static void configure(ObjectMapper mapper) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        mapper.registerModule(module);
    }
}
