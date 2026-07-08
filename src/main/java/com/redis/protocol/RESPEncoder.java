package com.redis.protocol;

public class RESPEncoder {

    public String encode(String response){

        if (response == null || response.equals("(nil)")){
            return "$-1\r\n";
        }
        if (response.equals("OK") || response.equals("PONG")){
            return "+"+response+"\r\n";
        }
        if (response.startsWith("ERROR")){
            return "-"+response+"\r\n";
        }
        if (response.equals("0")|| response.equals("1")){
            return ":"+response+"\r\n";
        }
        return "$"+response.length()+"\r\n"
                +response+"\r\n";
    }
}
