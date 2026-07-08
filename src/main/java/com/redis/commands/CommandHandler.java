package com.redis.commands;

import java.util.List;

public interface CommandHandler {
    String execute(List<String > arguments);
}
