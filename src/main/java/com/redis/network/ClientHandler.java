package com.redis.network;

import com.redis.command.Command;
import com.redis.executer.CommandExecuter;
import com.redis.protocol.CommandParser;
import com.redis.protocol.RESPEncoder;
import com.redis.protocol.RESPParser;
import com.redis.protocol.RESPReader;
import com.redis.storage.MemoryDatabase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ClientHandler {

//    private final MemoryDatabase database = new MemoryDatabase();
//    private final CommandParser parser = new CommandParser();

    private final RESPReader reader = new RESPReader();
    private final RESPParser parser = new RESPParser();

    private final RESPEncoder encoder = new RESPEncoder();

    private final CommandExecuter executer;

    public ClientHandler(CommandExecuter executer) {
        this.executer = executer;
    }


    public void handle(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024); // temp buffer space for writung
        int bytesRead = clientChannel.read(buffer);//this actually read the data from buffer if client enter anything , it
//        gives int values .like size of buffer.
        if (bytesRead == -1) {
            reader.remove(clientChannel);
            // when client close the terminal ,that is conn closed ,so close the conn
            clientChannel.close();
            return;
        }
        buffer.flip();//initally pos is 0,clent sends Hello -> pos :5 .Now if java starts reading
//        we need to move point back , so flip

//        String message = StandardCharsets.UTF_8.decode(buffer).toString().trim();//bytes to string // for normal strings

//        System.out.println("Received : " + message);
//        Command command = parser.parse(message); for normal string


        String incoming = StandardCharsets.UTF_8.decode(buffer).toString();// for resp
        reader.append(clientChannel, incoming); // add to buffer

        // Try to extract one complete RESP request
        String request = reader.nextRequest(clientChannel);

        if (request == null) {
             return;       // Wait for more TCP data
        }

        System.out.println("Received RESP Request:");
        System.out.println(request);

        Command command = parser.parse(request);// resp
        System.out.println("Command : "+command);

        String result = executer.execute(command);

        String resp = encoder.encode(result);
        ByteBuffer response = ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8));

//        ByteBuffer response = ByteBuffer.wrap(          // for normal string respose
//                ("Received Command : " + result + "\n").getBytes(StandardCharsets.UTF_8));// string to bytes


        clientChannel.write(response);// now response send back
    }
}