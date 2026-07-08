package com.redis.persistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AppendOnlyFile {

    private final BufferedWriter writer;

    public AppendOnlyFile(String fileName) {

        try {
            this.writer = new BufferedWriter(
                    new FileWriter(fileName, true));
        } catch (IOException e) {
            throw new RuntimeException("Unable to open AOF file", e);
        }

    }

    public synchronized void append(String command) {

        try {

            writer.write(command);
//            writer.newLine();

            // Immediately persist to disk
            writer.flush();

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

    }

    public void close() {

        try {
            writer.close();
        } catch (IOException ignored) {
        }

    }

}