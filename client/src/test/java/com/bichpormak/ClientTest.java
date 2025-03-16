package com.bichpormak;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;

public class ClientTest {

    @Test
    public void testClientConnection() throws IOException, InterruptedException {

        try (ServerSocket serverSocket = new ServerSocket(0)) {

            int port = serverSocket.getLocalPort();

            final Thread clientThread = new Thread(() -> {
                String[] args = {"localhost", String.valueOf(port), "IntegrationTestClient", "1"};
                ChatClient.main(args);
            });

            clientThread.start();

            final Socket clientSocket = serverSocket.accept();
            assertNotNull(clientSocket);
            clientSocket.close();
            clientThread.join(500);

        }

    }

}
