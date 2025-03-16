package com.bichpormak;

import org.junit.Test;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ServerTest {

    private final Socket dummySocket = new Socket();


    private static class TestClientHandler extends ClientHandler {

        private final List<String> receivedMessages = new ArrayList<>();

        public TestClientHandler(Socket socket) {
            super(socket);
        }

        @Override
        public void sendMessage(String message) {
            receivedMessages.add(message);
        }

        public List<String> getReceivedMessages() {
            return receivedMessages;
        }
    }

    @Test
    public void testAddClientToChatAndBroadcast() {

        int chatId = 1;
        final TestClientHandler client1 = new TestClientHandler(dummySocket);
        final TestClientHandler client2 = new TestClientHandler(dummySocket);

        ChatServer.addClientToChat(chatId, client1);
        ChatServer.addClientToChat(chatId, client2);

        String testMessage = "Test Message";
        ChatServer.broadcastMessage(chatId, testMessage);

        assertTrue(client1.getReceivedMessages().contains(testMessage));
        assertTrue(client2.getReceivedMessages().contains(testMessage));

    }

    @Test
    public void testRemoveClientFromChat() {

        int chatId = 2;
        final TestClientHandler client1 = new TestClientHandler(dummySocket);
        final TestClientHandler client2 = new TestClientHandler(dummySocket);

        ChatServer.addClientToChat(chatId, client1);
        ChatServer.addClientToChat(chatId, client2);

        ChatServer.removeClientFromChat(chatId, client1);
        String testMessage = "Message after removal";
        ChatServer.broadcastMessage(chatId, testMessage);

        assertFalse(client1.getReceivedMessages().contains(testMessage));
        assertTrue(client2.getReceivedMessages().contains(testMessage));

    }
}
