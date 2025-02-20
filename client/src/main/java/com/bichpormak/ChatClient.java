package com.bichpormak;

import java.io.IOException;
import java.net.Socket;



public class ChatClient {

    public static void main(String[] args) {

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String clientName = args[2];
        int chatId = parseChatId(args);

        connectToServer(host, port, clientName, chatId);

    }

    private static int parseChatId(String[] args) {
        return (args.length == 4) ? Integer.parseInt(args[3]) : 0;
    }

    private static void connectToServer(String host, int port, String clientName, int chatId) {

        try {

            final Socket socket = new Socket(host, port);
            System.out.println("Connected to the chat server");

            startReadingThread(socket);
            startWritingThread(socket, clientName, chatId);

        } catch (IOException e) {

            RuntimeException runtimeException = new RuntimeException("Error connecting to the server");
            runtimeException.initCause(e);
            throw runtimeException;

        }

    }

    private static void startReadingThread(Socket socket) {

        final ReadThread readThread = new ReadThread(socket);
        readThread.start();

    }

    private static void startWritingThread(Socket socket, String clientName, int chatId) {

        final WriteThread writeThread = new WriteThread(socket, clientName, chatId);
        writeThread.start();

    }
}
