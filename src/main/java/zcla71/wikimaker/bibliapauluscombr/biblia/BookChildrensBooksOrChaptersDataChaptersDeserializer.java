package zcla71.wikimaker.bibliapauluscombr.biblia;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class BookChildrensBooksOrChaptersDataChaptersDeserializer extends StdDeserializer<Map<String, String>> {
    public BookChildrensBooksOrChaptersDataChaptersDeserializer() {
        super(Map.class);
    }

    @Override
    public Map<String, String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);
        if ((node instanceof ArrayNode) && node.size() == 0) {
            return null;
        }
        if ((node instanceof ObjectNode objectNode)) {
            Map<String, String> result = new LinkedHashMap<>();
            Iterator<String> iterator = objectNode.fieldNames();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JsonNode value = objectNode.get(key);
                if (value instanceof TextNode textNode) {
                    result.put(key, textNode.textValue());
                } else {
                    throw new RuntimeException("Não sei o que fazer...");
                }
            }
            return result;
        }
        throw new RuntimeException("Não sei o que fazer...");
    }
}
