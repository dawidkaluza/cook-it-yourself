package pl.dkaluza.userservice.adapters.in.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

class DefaultUserDetailsDeserializer extends JsonDeserializer<DefaultUserDetails> {
    @Override
    public DefaultUserDetails deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonNode node = jsonParser.readValueAsTree();
        return new DefaultUserDetails(
            node.get("id").asLong(),
            node.get("emailAddress").asText(),
            node.get("encodedPassword").asText(),
            node.get("name").asText()
        );
    }
}
