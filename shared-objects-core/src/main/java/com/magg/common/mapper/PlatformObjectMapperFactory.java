package com.magg.common.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.jackson.nullable.JsonNullableModule;

/**
 * Provides a shared instance of a Jackson mapper to be used by all platform services.
 */
public final class PlatformObjectMapperFactory {
    private static final ObjectMapper INSTANCE = initializeMapper();

    private PlatformObjectMapperFactory() {
        // prevent instantiation
    }

    public static ObjectMapper getInstance() {
        return INSTANCE;
    }

    private static ObjectMapper initializeMapper() {
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JsonNullableModule())
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
