package com.bichpormak;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


class WriteThread extends Thread {

    private final Socket socket;
    private final String clientName;
    private final int chatId;

    public WriteThread(Socket socket, String clientName, int chatId) {
        this.socket = socket;
        this.clientName = clientName;
        this.chatId = chatId;
    }

    @Override
    public void run() {

        try (final PrintWriter printWriter = openWriter()) {

            sendInitialInfo(printWriter);
            readConsoleAndSend(printWriter);

        } catch (IOException e) {
            System.out.println("Error writing to server: " + e.getMessage());
        } finally {
            closeSocket();
        }

    }

    private PrintWriter openWriter() throws IOException {

        final OutputStream outputStream = socket.getOutputStream();
        return new PrintWriter(outputStream, true);

    }


    private void sendInitialInfo(final PrintWriter printWriter) {

        printWriter.println(clientName);
        printWriter.println(chatId);

    }

    private void readConsoleAndSend(final PrintWriter writer) {

        final Scanner in = new Scanner(System.in);
        while (!socket.isClosed()) {

            if (in.hasNextLine()) {

                final String message = in.nextLine();
                writer.println(message);

            }

        }

        in.close();

    }

    private void closeSocket() {

        try {

            if (!socket.isClosed()) {
                socket.close();
            }

        } catch (IOException e) {

            RuntimeException runtimeException = new RuntimeException();
            runtimeException.initCause(e);
            throw runtimeException;

        }

    }


}
