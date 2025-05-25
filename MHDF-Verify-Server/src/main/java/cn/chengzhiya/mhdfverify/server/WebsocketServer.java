package cn.chengzhiya.mhdfverify.server;

import cn.chengzhiya.mhdfverify.controller.ServerController;
import cn.chengzhiya.mhdfverify.util.Base64Util;
import cn.chengzhiya.mhdfverify.util.JwtUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServerEndpoint(
        value = "/api/verify/client",
        configurator = WebsocketServerConfig.class
)
public final class WebsocketServer {
    @Getter
    private static final HashMap<Session, JSONObject> sessionHashMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        Map<String, List<String>> headers = (Map<String, List<String>>) session.getUserProperties().get("headers");
        String token = headers.get("token").get(0);
        String ip = (String) session.getUserProperties().get("ip");

        if (!JwtUtil.checkLogin("MHDFVerify-Plugin-Jwt", ip, token)) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.RESERVED, "坏人,哼不理你了!"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        new Thread(() -> {
            while (session.isOpen()) {
                session.getAsyncRemote().sendText("喵?");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        String loginDataString = new String(Base64Util.decode(token));
        JSONObject loginData = JSONObject.parseObject(loginDataString);
        JSONObject params = loginData.getJSONObject("params");
        JSONObject other = params.getJSONObject("other");

        {
            Map<String, List<String>> pluginOnlineMap = ServerController.getPluginOnlineCountHashMap().getOrDefault(other.getString("plugin"), new HashMap<>());

            List<String> onlineList = pluginOnlineMap.getOrDefault(params.getString("data"), new ArrayList<>());
            onlineList.add(ip);
            pluginOnlineMap.put(params.getString("data"), onlineList);

            ServerController.getPluginOnlineCountHashMap().put(other.getString("plugin"), pluginOnlineMap);
        }

        getSessionHashMap().put(session, params);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
    }

    @OnClose
    public void onClose(Session session) {
        JSONObject params = getSessionHashMap().get(session);

        if (params != null) {
            JSONObject other = params.getJSONObject("other");
            {
                Map<String, List<String>> pluginOnlineMap = ServerController.getPluginOnlineCountHashMap().getOrDefault(other.getString("plugin"), new HashMap<>());

                List<String> onlineList = pluginOnlineMap.getOrDefault(params.getString("data"), new ArrayList<>());
                onlineList.remove(params.getString("ip"));
                pluginOnlineMap.put(params.getString("data"), onlineList);

                ServerController.getPluginOnlineCountHashMap().put(other.getString("plugin"), pluginOnlineMap);
            }
        }

        getSessionHashMap().remove(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        onClose(session);
    }
}
