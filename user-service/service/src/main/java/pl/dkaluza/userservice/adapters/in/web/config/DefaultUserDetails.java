package pl.dkaluza.userservice.adapters.in.web.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.dkaluza.userservice.domain.User;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

class DefaultUserDetails implements UserDetails {
    private final Long id;
    private final String emailAddress;
    private final String encodedPassword;
    private final String name;

    DefaultUserDetails(Long id, String emailAddress, String encodedPassword, String name) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.encodedPassword = encodedPassword;
        this.name = name;
    }

    public static DefaultUserDetails of(User user) {
        return new DefaultUserDetails(
            user.getId().getId(),
            user.getEmail().getValue(),
            new String(user.getPassword().getValue()),
            user.getName().getValue()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return encodedPassword;
    }

    @Override
    public String getUsername() {
        return emailAddress;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

//    static class MySerializer extends StdSerializer<DefaultUserDetails> {
//        public MySerializer() {
//            super(DefaultUserDetails.class);
//        }
//
//        @Override
//        public void serialize(DefaultUserDetails userDetails, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
//            jsonGenerator.writeStartObject();
//            jsonGenerator.writeNumberField("id", userDetails.getId());
//            jsonGenerator.writeStringField("emailAddress", userDetails.getEmailAddress());
//            jsonGenerator.writeStringField("encodedPassword", userDetails.getEncodedPassword());
//            jsonGenerator.writeStringField("name", userDetails.getName());
//            jsonGenerator.writeEndObject();
//        }
//
//        @Override
//        public void serializeWithType(DefaultUserDetails value, JsonGenerator jsonGenerator, SerializerProvider serializers, TypeSerializer typeSerializer) throws IOException {
//            var typeIdDef = typeSerializer.writeTypePrefix(
//                jsonGenerator, typeSerializer.typeId(value, DefaultUserDetails.class, JsonToken.VALUE_STRING)
//            );
//            serialize(value, jsonGenerator, serializers);
//            typeSerializer.writeTypeSuffix(jsonGenerator, typeIdDef);
//        }
//    }
//
//    static class MyDeserializer extends StdDeserializer<DefaultUserDetails> {
//        protected MyDeserializer() {
//            super(DefaultUserDetails.class);
//        }
//
//        @Override
//        public DefaultUserDetails deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
//            JsonNode node = jsonParser.readValueAsTree();
//            System.out.println("Node: " + node.asText());
//            return new DefaultUserDetails(
//                node.get("id").asLong(),
//                node.get("emailAddress").asText(),
//                node.get("encodedPassword").asText(),
//                node.get("name").asText()
//            );
//        }
//    }
}
