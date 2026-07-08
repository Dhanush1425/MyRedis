package com.redis.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ConnectionManager {

    private ServerSocket serverSocket;

    private final ClientHandler clientHandler;

    public ConnectionManager(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void start(int port) {
        try {
            Selector selector = Selector.open();// selector tells the events and mointor for ready events ,os inform about SET name like, selector stores event
            ServerSocketChannel serverChannel = ServerSocketChannel.open();// this creates the actual server
            serverChannel.bind(new InetSocketAddress(port)); // this tells the os that reserve this port for my server ,
//            InetsocketAddress = IP + port
            serverChannel.configureBlocking(false);//non-blocking for effciency
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);// this tells the selector ,to watch this server for new connections only
            System.out.println("MyRedis started.");
            System.out.println("Listening on port " + port);
            while (true) {
                selector.select();// this waits for any events
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();// selectedkey is a event obkect which is accectable thatis having event .
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();// walk one by one events
                    iterator.remove();// remove finished processes
                    if (key.isAcceptable()) {// is someone connected?
                        SocketChannel client = serverChannel.accept();// this creates one communication pipe bet clients and server
                        //java creates a new obj and register it
                        client.configureBlocking(false);// every client is non-blocking
                        client.register(selector, SelectionKey.OP_READ);//register the clientwith selector and moniter when the client send data
                        System.out.println("Client Connected : " + client.getRemoteAddress());
                    } else if (key.isReadable()) {// if client sent some data
                        SocketChannel client = (SocketChannel) key.channel();// this tells the exact client like this earlier registered socketChannel is ready now with ata
                        //key.channel returns the selectionKeys from selectedkey.   selectedkeys is actually clients with data or ready state.
                        clientHandler.handle(client);// it tells the clientHandler to process it.
                    }
                }
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
}



//Client B connects
//        │
//                ▼
//                server.accept()
//        │
//                ▼
//SocketChannel B created
//        │
//                ▼
//SocketChannel B registered with Selector
//        │
//                ▼
//Client B sends "SET name John"
//        │
//        ▼
//Operating System marks SocketChannel B as readable
//        │
//                ▼
//Selector wakes up
//        │
//                ▼
//selectedKeys() contains SelectionKey for SocketChannel B
//        │
//                ▼
//                key.channel()
//        │
//                ▼
//Returns SocketChannel B
//        │
//                ▼
//                clientHandler.handle(SocketChannel B)