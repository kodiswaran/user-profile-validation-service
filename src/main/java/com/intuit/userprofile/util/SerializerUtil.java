package com.intuit.userprofile.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.userprofile.model.exception.UserProfileKnownException;
import com.intuit.userprofile.model.exception.ErrorCode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SerializerUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Serialize the input
     *
     * @param value value to be serialized
     * @return serialized value
     * @param <T>  accepts any type as parameter
     */
    public static <T> String serialize(T value) {
        try {
            return MAPPER.writer().writeValueAsString(value);
        } catch ( Exception e ) {
            throw new UserProfileKnownException(ErrorCode.DATA_SERIALIZATION_ERROR, "not serializable object");
        }
    }

    /**
     * Deserialize the input
     *
     * @param value serialized value
     * @param type the class type to which the value has to be deserialized
     * @return the deserialized value
     * @param <T> accepts any type as the parameter to be deserialized
     */
    public static <T> T deSerialize(String value, Class<T> type) {
        try {
            return MAPPER.readValue(value, type);
        } catch ( Exception e ) {
            throw new UserProfileKnownException(ErrorCode.DATA_SERIALIZATION_ERROR, "not deserializable object");
        }
    }

}
