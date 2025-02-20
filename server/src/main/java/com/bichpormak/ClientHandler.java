package com.bichpormak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;


class ClientHandler extends Thread {

    private final Socket socket;
    private PrintWriter printWriter;
    private String clientName;
    private int chatId = 0;


    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final PrintWriter tempWriter = new PrintWriter(socket.getOutputStream(), true)) {

            this.printWriter = tempWriter;
            readClientNameAndChatId(bufferedReader);
            joinChatRoom();
            listenForMessages(bufferedReader);

        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            closeConnection();
        }

    }

    private void readClientNameAndChatId(BufferedReader reader) throws IOException {

        clientName = readName(reader);
        socket.setSoTimeout(200);

        try {

            final String chatIdLine = reader.readLine();
            if (chatIdLine != null && !chatIdLine.isEmpty()) {

                chatId = Integer.parseInt(chatIdLine);

            }

        } catch (SocketTimeoutException e) {

        } finally {
            socket.setSoTimeout(0);
        }
    }

    private String readName(BufferedReader reader) throws IOException {

        final String nameLine = reader.readLine();
        if (nameLine == null || nameLine.isEmpty()) {

            closeConnection();
            throw new IOException("Client name not provided.");

        }

        return nameLine;

    }

    private void joinChatRoom() {

        ChatServer.addClientToChat(chatId, this);
        ChatServer.broadcastMessage(chatId, clientName + " joined the chat");

    }

    private void listenForMessages(BufferedReader reader) throws IOException {

        String clientMessage;
        while ((clientMessage = reader.readLine()) != null) {

            String fullMessage = "[" + getTimestamp() + "] " + clientName + ": " + clientMessage;
            ChatServer.broadcastMessage(chatId, fullMessage);

        }

    }

    private void closeConnection() {

        try {

            if (!socket.isClosed()) {
                socket.close();
            }

        } catch (IOException e) {

            RuntimeException runtimeException = new RuntimeException();
            runtimeException.initCause(e);
            throw runtimeException;

        }

        ChatServer.removeClientFromChat(chatId, this);
        ChatServer.broadcastMessage(chatId, clientName + " left the chat");

    }

    public void sendMessage(String message) {

        if (printWriter != null) {
            printWriter.println(message);
        }

    }

    private String getTimestamp() {
        return new SimpleDateFormat("mm:ss.SSS").format(new Date());
    }


}
