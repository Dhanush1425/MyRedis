package com.redis.persistence;

import com.redis.command.Command;
import com.redis.executer.CommandExecuter;
import com.redis.protocol.RESPParser;

import java.io.*;

public class AOFRecovery {

    private final RESPParser parser = new RESPParser();

    private final CommandExecuter executer;

    private final String filename;

    public AOFRecovery(CommandExecuter executer, String filename) {
        this.executer = executer;
        this.filename = filename;
    }


    public void recover(){
        File file = new File(filename);
        if (!file.exists()){
            System.out.println("No AOF file found!");
            return;
        }
        System.out.println("Recovering from AOF...");

        RecoveryContext.startRecovery();

        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            StringBuilder request = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null){
                request.append(line).append("\r\n");
                if (isCompleteRESP(request.toString())){
                    Command command = parser.parse(request.toString());
                    executer.execute(command);
                    request.setLength(0);
                }
            }
            System.out.println("Recovery completed..");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            RecoveryContext.finishRecovery();
        }
    }

    private boolean isCompleteRESP(String request) {

        String[] lines = request.split("\r\n");
        if (lines.length == 0) {
            return false;
        }

        if (!lines[0].startsWith("*")) {
            return false;
        }
        int count = Integer.parseInt(lines[0].substring(1));

        return lines.length >= (count * 2 + 1);
    }
}
