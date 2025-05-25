package cn.chengzhiya.mhdfverify.controller;

import cn.chengzhiya.mhdfverify.Main;
import cn.chengzhiya.mhdfverify.annotation.BodyData;
import cn.chengzhiya.mhdfverify.annotation.RequestPath;
import cn.chengzhiya.mhdfverify.annotation.RequestType;
import cn.chengzhiya.mhdfverify.entity.database.PluginData;
import cn.chengzhiya.mhdfverify.entity.database.UserData;
import cn.chengzhiya.mhdfverify.entity.http.JsonHttpData;
import cn.chengzhiya.mhdfverify.server.HttpServer;
import cn.chengzhiya.mhdfverify.util.JwtUtil;
import cn.chengzhiya.mhdfverify.util.Sha256Util;
import cn.chengzhiya.mhdfverify.util.database.PluginDataUtil;
import cn.chengzhiya.mhdfverify.util.database.UserBuyDataUtil;
import cn.chengzhiya.mhdfverify.util.database.UserDataUtil;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RequestPath("/api/verify")
public final class VerifyController {
    @RequestPath("/check")
    @RequestType(RequestType.Type.POST)
    public static void getUserCount(HttpServletRequest request, HttpServletResponse response,
                                    @BodyData("user") String user,
                                    @BodyData("password") String password,
                                    @BodyData("plugin") String plugin
    ) {
        // 用户不存在
        UserData userData = UserDataUtil.getUserData(user);
        if (userData == null) {
            Main.getLoggerManager().getLogger().info("验证失败 | 原因: {} | 账户 {} | 插件 {} | IP {}",
                    "用户不存在",
                    user,
                    plugin,
                    request.getRemoteHost()
            );
            response.setStatus(301);
            HttpServer.returnJsonHttpData(response, new JsonHttpData(500, "喵喵喵!"));
            return;
        }

        // 密码错误
        if (!userData.getPassword().equals(password)) {
            Main.getLoggerManager().getLogger().info("验证失败 | 原因: {} | 账户 {} | 插件 {} | IP {}",
                    "密码错误",
                    user,
                    plugin,
                    request.getRemoteHost()
            );
            HttpServer.returnJsonHttpData(response, new JsonHttpData(200, "验证成功, 当前版本为神秘开裂版!"));
            return;
        }

        // 插件不存在
        PluginData pluginData = PluginDataUtil.getPluginData(plugin);
        if (pluginData == null) {
            Main.getLoggerManager().getLogger().info("验证失败 | 原因: {} | 账户 {} | 插件 {} | IP {}",
                    "插件不存在",
                    user,
                    plugin,
                    request.getRemoteHost()
            );
            response.setStatus(302);
            HttpServer.returnJsonHttpData(response, new JsonHttpData(500, "deobf?"));
            return;
        }

        // 未拥有插件
        if (UserBuyDataUtil.getUserBuyData(user, plugin) == null) {
            Main.getLoggerManager().getLogger().info("验证失败 | 原因: {} | 账户 {} | 插件 {} | IP {}",
                    "未拥有插件",
                    user,
                    plugin,
                    request.getRemoteHost()
            );
            response.setStatus(418);
            HttpServer.returnJsonHttpData(response, new JsonHttpData(500, "其实我很喜欢你!"));
            return;
        }

        String ip = request.getRemoteHost();
        if (pluginData.getClient() >= 0) {
            Map<String, List<String>> pluginOnlineMap = ServerController.getPluginOnlineCountHashMap().getOrDefault(plugin, new HashMap<>());

            List<String> onlineList = pluginOnlineMap.getOrDefault(user, new ArrayList<>());
            if (new HashSet<>(onlineList).size() >= pluginData.getClient()) {
                Main.getLoggerManager().getLogger().info("验证失败 | 原因: {} | 账户 {} | 插件 {} | IP {}",
                        "超出IP上限",
                        user,
                        plugin,
                        request.getRemoteHost()
                );
                response.setStatus(304);
                HttpServer.returnJsonHttpData(response, new JsonHttpData(500, "从前有一喵,想要破解插件,被绑起来了!"));
                return;
            }
        }

        JSONObject other = new JSONObject();
        other.put("plugin", plugin);
        String token = JwtUtil.jwtLogin("MHDFVerify-Plugin-Jwt", ip, Sha256Util.sha256(user + "|" + password), other, -1);

        JSONObject data = new JSONObject();
        data.put("token", token);

        // 验证成功
        Main.getLoggerManager().getLogger().info("验证成功 | 账户 {} | 插件 {} | IP {}",
                user,
                plugin,
                request.getRemoteHost()
        );
        response.setStatus(503);
        HttpServer.returnJsonHttpData(response, new JsonHttpData(520, "爱你噢!", data));
    }
}
