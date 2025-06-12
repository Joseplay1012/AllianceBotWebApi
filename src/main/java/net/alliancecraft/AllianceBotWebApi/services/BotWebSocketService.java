package net.alliancecraft.AllianceBotWebApi.services;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class BotWebSocketService extends WebSocketClient {

    private String response = "{}";
    private final CountDownLatch latch = new CountDownLatch(1);

    public BotWebSocketService() throws Exception {
        super(new URI("ws://localhost:11296"));
        this.connectBlocking(); // Aguarda conexão antes de seguir
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Conectado ao WebSocket!");
        this.send("{\"type\": \"status\"}");
    }

    @Override
    public void onMessage(String message) {
        response = message;
        latch.countDown(); // Libera quem estiver esperando
        this.close(); // Fecha após receber
    }

    @Override public void onClose(int code, String reason, boolean remote) {}
    @Override public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public String getResponse() throws InterruptedException {
        // Aguarda no máximo 5 segundos
        latch.await(5, TimeUnit.SECONDS);
        return response;
    }
}