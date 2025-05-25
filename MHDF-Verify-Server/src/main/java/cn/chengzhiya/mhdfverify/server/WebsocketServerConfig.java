package cn.chengzhiya.mhdfverify.server;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.reflect.Field;

public final class WebsocketServerConfig extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        sec.getUserProperties().put("headers", request.getHeaders());
        try {
            Field requestField = request.getClass().getDeclaredField("request");
            requestField.setAccessible(true);

            HttpServletRequest servletRequest = (HttpServletRequest) requestField.get(request);
            sec.getUserProperties().put("ip", servletRequest.getRemoteAddr());
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}
