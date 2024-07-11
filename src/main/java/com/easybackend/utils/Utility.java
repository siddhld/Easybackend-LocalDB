package com.easybackend.utils;

import com.easybackend.exception.DataNotFoundException;
import com.easybackend.model.DynamicData;
import com.easybackend.model.EndpointData;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Utility {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int STRING_LENGTH = 4;
    private static SecureRandom random = new SecureRandom();

    public static String generateRandomString() {
        StringBuilder sb = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static void checkForEmptyListData(Optional<List<DynamicData>> data, String message) {
        if (data.isPresent() && data.get().size() == 0) {
            throw new DataNotFoundException(message);
        }
    }

    public static void checkForEmptyData(Optional<DynamicData> data, String message) {
        if (data.isEmpty()) {
            throw new DataNotFoundException(message);
        }
    }

    public static void checkForEmptyEndpoint(Optional<EndpointData> endpointData, String message) {
        if (endpointData.isEmpty()) {
            throw new DataNotFoundException(message);
        }
    }

    public static void validateUniqueKey(String uniqueKey) {
        if (uniqueKey == null || uniqueKey.isEmpty() || uniqueKey.isBlank()) {
            throw new DataNotFoundException("Please Add your unique Endpoint in Url");
        }
    }

    public static void validateData(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            throw new DataNotFoundException("Empty data found");
        }
    }

}
