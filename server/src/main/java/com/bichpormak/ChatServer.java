package com.bichpormak;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class ChatServer {

    private static final ConcurrentHashMap<Integer, CopyOnWriteArrayList<ClientHandler>> chatRooms = new ConcurrentHashMap<>();


    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        startServer(port);

    }

    private static void startServer(int port) {

        try (final ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is running");

            while (true) {
                acceptClient(serverSocket);
            }

        } catch (IOException e) {

            RuntimeException runtimeException = new RuntimeException();
            runtimeException.initCause(e);
            throw runtimeException;

        }

    }

    private static void acceptClient(ServerSocket serverSocket) throws IOException {

        final Socket socket = serverSocket.accept();
        ClientHandler clientHandler = new ClientHandler(socket);
        clientHandler.start();

    }

    public static void broadcastMessage(int chatId, String message) {

        final CopyOnWriteArrayList<ClientHandler> clients = chatRooms.get(chatId);
        if (clients != null) {

            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }

        }

    }

    public static synchronized void addClientToChat(int chatId, ClientHandler clientHandler) {

        chatRooms.putIfAbsent(chatId, new CopyOnWriteArrayList<>());
        chatRooms.get(chatId).add(clientHandler);

    }

    public static synchronized void removeClientFromChat(int chatId, ClientHandler clientHandler) {

        final CopyOnWriteArrayList<ClientHandler> clients = chatRooms.get(chatId);
        if (clients != null) {
            clients.remove(clientHandler);

            if (clients.isEmpty()) {
                chatRooms.remove(chatId);
            }

        }

    }

}
