package org.elder.sourcerer.samples.todo.query.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JsonConverter<T> implements AttributeConverter<T, String> {
    private static ObjectMapper OBJECT_MAPPER = null;

    public static void setObjectMapper(final ObjectMapper objectMapper) {
        Preconditions.checkNotNull(objectMapper);
        OBJECT_MAPPER = objectMapper;
    }

    private final Type objectType;

    @SuppressWarnings("unchecked")
    public JsonConverter() {
        // TODO: Validate that we're subclassing ourselves...
        ParameterizedType parameterizedSelf =
                (ParameterizedType) this
                        .getClass()
                        .getGenericSuperclass();
        objectType = parameterizedSelf.getActualTypeArguments()[0];
    }

    @Override
    public String convertToDatabaseColumn(final T meta) {
        ObjectMapper mapper = getMapper();
        try {
            return mapper.writeValueAsString(meta);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Error writing object as json", ex);
        }
    }

    @Override
    public T convertToEntityAttribute(final String dbData) {
        ObjectMapper mapper = getMapper();
        try {
            JavaType javaType = mapper.getTypeFactory().constructType(objectType);
            return mapper.readValue(dbData, javaType);
        } catch (IOException ex) {
            throw new RuntimeException("Error reading attribute as JSON", ex);
        }
    }

    private static ObjectMapper getMapper() {
        // TODO: Implement this in a cleaner way ...
        ObjectMapper mapper = OBJECT_MAPPER;
        if (mapper == null) {
            throw new IllegalStateException(
                    "The JsonConverter needs to be configured with an Object Mapper before use");
        }
        return mapper;
    }
}
