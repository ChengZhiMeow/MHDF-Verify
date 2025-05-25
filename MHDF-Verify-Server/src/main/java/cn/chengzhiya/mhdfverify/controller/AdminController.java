package cn.chengzhiya.mhdfverify.controller;

import cn.chengzhiya.mhdfverify.annotation.BodyData;
import cn.chengzhiya.mhdfverify.annotation.CookieData;
import cn.chengzhiya.mhdfverify.annotation.RequestPath;
import cn.chengzhiya.mhdfverify.annotation.RequestType;
import cn.chengzhiya.mhdfverify.entity.config.YamlConfiguration;
import cn.chengzhiya.mhdfverify.entity.http.JsonHttpData;
import cn.chengzhiya.mhdfverify.server.HttpServer;
import cn.chengzhiya.mhdfverify.util.Base64Util;
import cn.chengzhiya.mhdfverify.util.JwtUtil;
import cn.chengzhiya.mhdfverify.util.Sha256Util;
import cn.chengzhiya.mhdfverify.util.config.ConfigUtil;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestPath("/api/admin")
public final class AdminController {
    @RequestPath("/get_user_info")
    @RequestType(RequestType.Type.GET)
    public static void getUserInfo(HttpServletRequest request, HttpServletResponse response,
                                   @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        YamlConfiguration config = ConfigUtil.getConfig().getConfigurationSection("serverSettings.adminWeb");
        if (config == null) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.serverError);
            return;
        }

        JSONObject data = new JSONObject();
        data.put("name", config.getString("user"));

        HttpServer.returnJsonHttpData(response, new JsonHttpData(data));
    }

    @RequestPath("/login")
    @RequestType(RequestType.Type.POST)
    public static void login(HttpServletRequest request, HttpServletResponse response,
                             @BodyData("data") String data
    ) {
        YamlConfiguration config = ConfigUtil.getConfig().getConfigurationSection("serverSettings.adminWeb");
        if (config == null) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.serverError);
            return;
        }

        String hash = Sha256Util.sha256(config.getString("user") + "|" + config.getString("password"));
        if (!data.equals(hash)) {
            HttpServer.returnJsonHttpData(response, new JsonHttpData(401, "账户或密码错误"));
            return;
        }

        String token = JwtUtil.jwtLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), hash, new JSONObject(), 86400000);

        response.addHeader("Set-Cookie", "token=" + token + "; secure; samesite=None; path=/api");
        HttpServer.returnJsonHttpData(response, new JsonHttpData("登录成功"));
    }

    @RequestPath("/logout")
    @RequestType(RequestType.Type.POST)
    public static void logout(HttpServletRequest request, HttpServletResponse response,
                              @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        JSONObject loginData = JSONObject.parseObject(new String(Base64Util.decode(token)));
        JwtUtil.getUserLoginHashMap().remove(loginData.getJSONObject("params").getInteger("id"));

        response.addHeader("Set-Cookie", "token=" + "removed" + "; secure; samesite=None; path=/api");
        HttpServer.returnJsonHttpData(response, new JsonHttpData("退出成功"));
    }
}