package com.redis.server.protocol;

import com.redis.server.response.ArrayResponse;
import com.redis.server.response.BulkStringResponse;
import com.redis.server.response.ErrorResponse;
import com.redis.server.response.IntegerResponse;
import com.redis.server.response.Response;
import com.redis.server.response.SimpleStringResponse;

public class RESPResponseEncoder {

    public String encode(Response response) {

        if (response instanceof SimpleStringResponse simple) {
            return encodeSimpleString(simple);
        }

        if (response instanceof BulkStringResponse bulk) {
            return encodeBulkString(bulk);
        }

        if (response instanceof IntegerResponse integer) {
            return encodeInteger(integer);
        }

        if (response instanceof ArrayResponse array) {
            return encodeArray(array);
        }

        if (response instanceof ErrorResponse error) {
            return encodeError(error);
        }

        throw new IllegalArgumentException("Unknown response type");
    }

    private String encodeSimpleString(SimpleStringResponse response) {

        return "+" +
                response.getValue() +
                "\r\n";
    }

    private String encodeBulkString(BulkStringResponse response) {

        String value = response.getValue();

        if (value == null) {
            return "$-1\r\n";
        }

        return "$" +
                value.length() +
                "\r\n" +
                value +
                "\r\n";
    }

    private String encodeInteger(IntegerResponse response) {

        return ":" +
                response.getValue() +
                "\r\n";
    }

    private String encodeArray(ArrayResponse response) {

        StringBuilder builder = new StringBuilder();

        builder.append("*")
                .append(response.getValues().size())
                .append("\r\n");

        for (String value : response.getValues()) {

            builder.append("$")
                    .append(value.length())
                    .append("\r\n")
                    .append(value)
                    .append("\r\n");
        }

        return builder.toString();
    }

    private String encodeError(ErrorResponse response) {

        return "-" +
                response.getMessage() +
                "\r\n";
    }
}