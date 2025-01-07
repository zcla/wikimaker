package zcla71.utils;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonUtils {
    public static void enableJavaTime(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static ObjectMapper getObjectMapperInstance() {
        ObjectMapper result = new ObjectMapper();
        result.setSerializationInclusion(Include.NON_NULL);
        result.enable(SerializationFeature.INDENT_OUTPUT);
        return result;
    }

    public static PrettyPrinter getPrettyPrinter() {
        DefaultPrettyPrinter result = new DefaultPrettyPrinter();
        result.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        return result;
    }

    public static void saveJsonPretty(File file, Object object) throws StreamWriteException, DatabindException, IOException {
        getObjectMapperInstance().writer(getPrettyPrinter()).writeValue(file, object);
    }
}
