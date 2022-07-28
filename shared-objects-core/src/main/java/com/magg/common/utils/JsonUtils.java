package com.magg.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.magg.common.mapper.PlatformObjectMapperFactory;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.trimWhitespace;

/**
 * Utility class for json and Map operations
 */
public class JsonUtils {

    protected static final ObjectMapper OBJECT_MAPPER = PlatformObjectMapperFactory.getInstance();

    /**
     * Puts or replaces the values of source into target.
     * @param source The source of new values
     * @param target The object where new values are replaced.
     */
    @SuppressWarnings("unchecked")
    public static void mapProperties(Map<String, Object> source, Map<String, Object> target) {
        source.forEach((key, value) -> {
            if (value instanceof Map) {
                //noinspection rawtypes
                mapProperties((Map)source.get(key), (Map)target.get(key));
            } else {
                if (value == Optional.empty()) {
                    target.put(key, null);
                } else {
                    target.put(key, value);
                }

            }
        });
    }

    /**
     * Transforms the object into its json string representation
     * @param object The object to transform
     */
    public static String convertToJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns the field names from apiFieldMap keys
     * @param apiFieldMap The map containing the properties to look for
     * @param prefix A prefix to prepend.
     */
    public static List<String> extractFieldResourceNames(Map<String, Object> apiFieldMap, String prefix) {
        List<String> resourceNames = Lists.newArrayList();
        apiFieldMap.forEach((key, value) -> {
            if (value instanceof Map) {
                String newPrefix = format("%s.%s", prefix, trimWhitespace(key).toLowerCase(Locale.ROOT));
                //noinspection unchecked
                resourceNames.addAll(extractFieldResourceNames((Map<String, Object>) value, newPrefix));
            } else if (value != null) {
                resourceNames.add(format("%s.%s", prefix, trimWhitespace(key).toLowerCase(Locale.ROOT)));
            }
        });

        return resourceNames;
    }
}
