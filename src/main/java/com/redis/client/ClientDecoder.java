package com.redis.client;

public final class ClientDecoder {

    private ClientDecoder() {
    }

    /**
     * Decodes a RESP response into an appropriate Java object.
     *
     * Supported RESP Types:
     * + Simple String  -> String
     * - Error          -> RuntimeException
     * $ Bulk String    -> String / null
     * : Integer        -> Long
     */
    public static Object decode(String response) {

        if (response == null || response.isBlank()) {
            return null;
        }

        char prefix = response.charAt(0);

        switch (prefix) {

            case '+':
                return decodeSimpleString(response);

            case '-':
                throw new RuntimeException(response.substring(1).trim());

            case '$':
                return decodeBulkString(response);

            case ':':
                return decodeInteger(response);

            default:
                throw new IllegalArgumentException("Unsupported RESP response: " + response);
        }
    }

    /**
     * Decodes RESP Simple Strings.
     *
     * Example:
     * +OK\r\n
     * +PONG\r\n
     */
    private static String decodeSimpleString(String response) {

        return response.substring(1).trim();
    }

    /**
     * Decodes RESP Bulk Strings.
     *
     * Example:
     * $5\r\nhello\r\n
     *
     * Null Example:
     * $-1\r\n
     */
    private static String decodeBulkString(String response) {

        String[] lines = response.split("\r\n");

        // Null Bulk String
        if ("$-1".equals(lines[0])) {
            return null;
        }

        return lines[1];
    }

    /**
     * Decodes RESP Integers.
     *
     * Example:
     * :1\r\n
     * :100\r\n
     */
    private static Long decodeInteger(String response) {

        return Long.parseLong(response.substring(1).trim());
    }
}