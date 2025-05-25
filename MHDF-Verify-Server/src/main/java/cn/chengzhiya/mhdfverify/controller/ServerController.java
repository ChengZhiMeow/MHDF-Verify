package cn.chengzhiya.mhdfverify.controller;

import cn.chengzhiya.mhdfverify.Main;
import cn.chengzhiya.mhdfverify.annotation.CookieData;
import cn.chengzhiya.mhdfverify.annotation.RequestPath;
import cn.chengzhiya.mhdfverify.annotation.RequestType;
import cn.chengzhiya.mhdfverify.entity.http.JsonHttpData;
import cn.chengzhiya.mhdfverify.server.HttpServer;
import cn.chengzhiya.mhdfverify.util.JwtUtil;
import cn.chengzhiya.mhdfverify.util.database.PluginDataUtil;
import cn.chengzhiya.mhdfverify.util.database.UserDataUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestPath("/api/server")
public final class ServerController {
    /**
     * Map<时间, Map<接口, 数量>>
     */
    @Getter
    private static final ConcurrentHashMap<String, Map<String, Integer>> apiCallCountHashMap = new ConcurrentHashMap<>();
    /**
     * Map<插件, Map<用户, Set<客户端IP>>>
     */
    @Getter
    private static final ConcurrentHashMap<String, Map<String, List<String>>> pluginOnlineCountHashMap = new ConcurrentHashMap<>();

    @RequestPath("/get_info")
    @RequestType(RequestType.Type.GET)
    public static void getInfo(HttpServletRequest request, HttpServletResponse response,
                               @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        JSONObject data = new JSONObject();
        data.put("app", "MHDF-Verify");
        data.put("version", Main.getVersion());
        data.put("start_time", Main.getStartTime());

        HttpServer.returnJsonHttpData(response, new JsonHttpData(data));
    }

    @RequestPath("/get_plugin_online_count")
    @RequestType(RequestType.Type.GET)
    public static void getPluginOnlineCount(HttpServletRequest request, HttpServletResponse response,
                                            @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        JSONObject data = new JSONObject();
        for (Map.Entry<String, Map<String, List<String>>> entry : getPluginOnlineCountHashMap().entrySet()) {
            JSONObject timeData = new JSONObject();
            timeData.putAll(entry.getValue());

            data.put(entry.getKey(), timeData);
        }

        HttpServer.returnJsonHttpData(response, new JsonHttpData(data));
    }

    @RequestPath("/get_api_call_count")
    @RequestType(RequestType.Type.GET)
    public static void getApiCallCount(HttpServletRequest request, HttpServletResponse response,
                                       @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        JSONObject data = new JSONObject();
        for (Map.Entry<String, Map<String, Integer>> entry : getApiCallCountHashMap().entrySet()) {
            JSONObject timeData = new JSONObject();
            timeData.putAll(entry.getValue());

            data.put(entry.getKey(), timeData);
        }

        HttpServer.returnJsonHttpData(response, new JsonHttpData(data));
    }

    @RequestPath("/get_plugin_count")
    @RequestType(RequestType.Type.GET)
    public static void getPluginCount(HttpServletRequest request, HttpServletResponse response,
                                      @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        JSONObject data = new JSONObject();
        data.put("count", PluginDataUtil.getPluginDataList().size());

        HttpServer.returnJsonHttpData(response, new JsonHttpData(data));
    }

    @RequestPath("/get_user_count")
    @RequestType(RequestType.Type.GET)
    public static void getUserCount(HttpServletRequest request, HttpServletResponse response,
                                    @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        JSONObject data = new JSONObject();
        data.put("count", UserDataUtil.getUserDataList().size());

        HttpServer.returnJsonHttpData(response, new JsonHttpData(data));
    }

    @RequestPath("/get_client_count")
    @RequestType(RequestType.Type.GET)
    public static void getClientCount(HttpServletRequest request, HttpServletResponse response,
                                      @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        int count = 0;
        for (Map<String, List<String>> pluginOnlineMap : getPluginOnlineCountHashMap().values()) {
            for (List<String> onlineList : pluginOnlineMap.values()) {
                count += onlineList.size();
            }
        }
        JSONObject data = new JSONObject();
        data.put("count", count);

        HttpServer.returnJsonHttpData(response, new JsonHttpData(data));
    }
}