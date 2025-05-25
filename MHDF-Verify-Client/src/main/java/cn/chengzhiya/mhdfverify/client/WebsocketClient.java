package cn.chengzhiya.mhdfverify.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public final class WebsocketClient extends WebSocketClient {
    public WebsocketClient(String host, String token) {
        super(URI.create("ws://" + host + "/api/verify/client"));
        addHeader("token", token);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }

    @Override
    public void onMessage(String message) {
        if (message.equals("喵?")) {
            send("喵!");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception e) {
    }
}
