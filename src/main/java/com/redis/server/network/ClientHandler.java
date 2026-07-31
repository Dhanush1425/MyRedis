package com.redis.server.network;

import com.redis.server.command.Command;
import com.redis.server.executer.CommandExecuter;
import com.redis.server.protocol.RESPParser;
import com.redis.server.protocol.RESPReader;
import com.redis.server.protocol.RESPResponseEncoder;
import com.redis.server.pubsub.PubSubManager;
import com.redis.server.replication.ReplicaManager;
import com.redis.server.response.ErrorResponse;
import com.redis.server.response.IntegerResponse;
import com.redis.server.response.Response;
import com.redis.server.response.SimpleStringResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ClientHandler {

//    private final MemoryDatabase database = new MemoryDatabase();
//    private final CommandParser parser = new CommandParser();

    private final RESPReader reader = new RESPReader();
    private final RESPParser parser = new RESPParser();

//    private final RESPEncoder encoder = new RESPEncoder();

    private final CommandExecuter executer;

    private final PubSubManager pubSubManager;

    private final ReplicaManager replicaManager;

    private final RESPResponseEncoder responseEncoder = new RESPResponseEncoder();

    public ClientHandler(CommandExecuter executer, PubSubManager pubSubManager, ReplicaManager replicaManager) {
        this.executer = executer;
        this.pubSubManager = pubSubManager;
        this.replicaManager = replicaManager;
    }


    public void handle(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024); // temp buffer space for writung
        int bytesRead = clientChannel.read(buffer);//this actually read the data from buffer if client enter anything , it
//        gives int values .like size of buffer.
        if (bytesRead == -1) {
            reader.remove(clientChannel);
            // when client close the terminal ,that is conn closed ,so close the conn

            pubSubManager.removeClient(clientChannel);

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

        String commandName = command.commandName().toUpperCase();

        if (commandName.equals("REPLICA")) {
            replicaManager.addReplica(clientChannel);
            Response response = new SimpleStringResponse("OK");
            String resp = responseEncoder.encode(response);
            clientChannel.write(ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8)));
            return;
        }

        if (commandName.equals("SUBSCRIBE")) {  // for subscribe
            if (command.arguments().size() != 1) {
                Response error = new ErrorResponse("SUBSCRIBE requires channel");
                String resp = responseEncoder.encode(error);
                clientChannel.write(ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8)));
                return;
            }

            String channel = command.arguments().get(0);
            pubSubManager.subscribe(channel, clientChannel);
            Response ok = new SimpleStringResponse("OK");
            String resp = responseEncoder.encode(ok);
            clientChannel.write(ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8)));
            return;
        }

        if (commandName.equals("PUBLISH")) {
            if (command.arguments().size() != 2) {
                Response error = new ErrorResponse("PUBLISH requires channel and message");
                String resp = responseEncoder.encode(error);
                clientChannel.write(ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8)));
                return;
            }

            String channel = command.arguments().get(0);
            String message = command.arguments().get(1);

            int delivered = pubSubManager.publish(channel, message);

            Response response = new IntegerResponse(delivered);

            String resp = responseEncoder.encode(response);
            clientChannel.write(ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8)));
            return;
        }

        if (commandName.equals("UNSUBSCRIBE")) {
            if (command.arguments().size() != 1) {
                Response error = new ErrorResponse("UNSUBSCRIBE requires channel");
                String resp = responseEncoder.encode(error);
                clientChannel.write(ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8)));
                return;
            }

            String channel = command.arguments().get(0);
            pubSubManager.unsubscribe(channel, clientChannel);
            Response ok = new SimpleStringResponse("OK");
            String resp = responseEncoder.encode(ok);
            clientChannel.write(ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8)));
            return;
        }



        Response result = executer.execute(command); // for normal commands ,

        String resp = responseEncoder.encode(result);
        ByteBuffer response = ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8));

//        ByteBuffer response = ByteBuffer.wrap(          // for normal string respose
//                ("Received Command : " + result + "\n").getBytes(StandardCharsets.UTF_8));// string to bytes


        clientChannel.write(response);// now response send back
    }
}