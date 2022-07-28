package com.magg.common.mapper;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.validation.Valid;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Interface for common service implementation.
 */
public interface ObjectConverter<DataT, ApiT> {

    /**
     * Mapping a JsonNullable to its wrapped type.
     * @param jsonNullable JsonNullable wrapped value
     * @param <T> The type being wrapped
     * @return Returns the wrapped value
     */
    default <T> T fromJsonNullable(final JsonNullable<T> jsonNullable) {
        return jsonNullable.orElse(null);
    }

    /**
     * Mapping a value to a JsonNullable.
     * @param value Value
     * @param <T> The value type
     * @return The value wrapped as a JsonNullable
     */
    default <T> JsonNullable<T> asJsonNullable(final T value) {
        return JsonNullable.of(value);
    }

    /**
     * Map JsonNullable URI to String
     * @param uri {@link JsonNullable}
     * @return URI @{link URI}
     */
    default String map(JsonNullable<URI> uri) {
        URI uriValue = fromJsonNullable(uri);
        return uriValue == null ? null : uriValue.toString();
    }

    /**
     * Map String to JsonNullable URI
     * @param uriValue {@link String}
     * @return Returns JsonNullable wrapped URI
     */
    default JsonNullable<URI> map(String uriValue) {
        return uriValue == null ? JsonNullable.of(null) : JsonNullable.of(URI.create(uriValue));
    }

    /**
     * Map {@link Instant} to {@link OffsetDateTime}.
     * @param value {@link Instant}
     * @return {@link OffsetDateTime}
     */
    default OffsetDateTime map(Instant value) {
        return value == null ? null : OffsetDateTime.ofInstant(value, ZoneId.systemDefault());
    }

    /**
     * Map {@link Date} to {@link Instant}.
     * @param value {@link Date}
     * @return {@link Instant}
     */
    default Instant map(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }


    /**
     * Map {@link Integer} to {@link Long}.
     * @param value {@link Integer}
     * @return {@link Long}
     */
    default Long map(Integer value) {
        return value == null ? null : value.longValue();
    }

    /**
     * Map {@link Long} to {@link Integer}.
     * @param value {@link Long}
     * @return {@link Integer}
     */
    default Integer map(Long value) {
        return value == null ? null : value.intValue();
    }

    /**
     * Transform public API model to persistence model. Custom fields are ignored for transformation here but
     * should be transformed afterwards.
     * @param object public model.
     * @return a persistence object implementing
     */
    DataT apiModelToDataModel(@Valid ApiT object);

    /**
     * Transform public persistence model to API model. Custom fields are ignored for transformation here but
     * should be transformed afterwards.
     * @param object persistence model.
     * @return a public model.
     */
    ApiT dataModelToApiModel(DataT object);

}
