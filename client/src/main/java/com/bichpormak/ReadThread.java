package com.bichpormak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;



class ReadThread extends Thread {

    private final Socket socket;

    public ReadThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (final BufferedReader bufferedReader = openReader()) {

            readMessages(bufferedReader);

        } catch (IOException e) {
            System.out.println("Error reading from server: " + e.getMessage());
        }

    }

    private BufferedReader openReader() throws IOException {

        final InputStream inputStream = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream));

    }


    private void readMessages(BufferedReader reader) throws IOException {

        String response;
        while ((response = reader.readLine()) != null) {
            handleIncomingMessage(response);
        }

    }

    private void handleIncomingMessage(String message) {
        System.out.println(message);
    }


}
