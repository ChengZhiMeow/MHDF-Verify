package cn.chengzhiya.mhdfverify.controller;

import cn.chengzhiya.mhdfverify.annotation.*;
import cn.chengzhiya.mhdfverify.entity.database.UserBuyData;
import cn.chengzhiya.mhdfverify.entity.database.UserData;
import cn.chengzhiya.mhdfverify.entity.http.JsonHttpData;
import cn.chengzhiya.mhdfverify.server.HttpServer;
import cn.chengzhiya.mhdfverify.util.JwtUtil;
import cn.chengzhiya.mhdfverify.util.Sha256Util;
import cn.chengzhiya.mhdfverify.util.database.UserBuyDataUtil;
import cn.chengzhiya.mhdfverify.util.database.UserDataUtil;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequestPath("/api/user")
public final class UserController {
    @RequestPath("/get_list")
    @RequestType(RequestType.Type.GET)
    public static void getList(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam("page") @DefaultValue("1") Integer page,
                               @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        List<UserData> userDataList = UserDataUtil.getUserDataList();
        int start = (page - 1) * 10;
        int end = Math.min(userDataList.size(), page * 10);

        JSONObject data = new JSONObject();
        data.put("maxPages", (int) Math.ceil(userDataList.size() / 10.0));

        JSONObject users = new JSONObject();
        for (UserData userData : userDataList.subList(start, end)) {
            JSONObject user = new JSONObject();
            user.put("buy_plugin_list", UserBuyDataUtil.getUserUserBuyDataList(userData.getUser()).stream()
                    .map(UserBuyData::getPlugin)
                    .toList()
            );

            users.put(userData.getUser(), user);
        }
        data.put("users", users);

        HttpServer.returnJsonHttpData(response, new JsonHttpData(data));
    }

    @RequestPath("/create")
    @RequestType(RequestType.Type.POST)
    public static void create(HttpServletRequest request, HttpServletResponse response,
                              @BodyData("user") String user,
                              @BodyData("password") String password,
                              @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        if (UserDataUtil.getUserData(user) != null) {
            HttpServer.returnJsonHttpData(response, new JsonHttpData(400, "这个用户已经被创建了"));
            return;
        }

        UserData userData = new UserData();
        userData.setUser(user);
        userData.setPassword(Sha256Util.sha256(password));
        UserDataUtil.updateUserData(userData);

        HttpServer.returnJsonHttpData(response, new JsonHttpData("创建成功"));
    }

    @RequestPath("/remove")
    @RequestType(RequestType.Type.POST)
    public static void remove(HttpServletRequest request, HttpServletResponse response,
                              @BodyData("user") String user,
                              @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        if (UserDataUtil.getUserData(user) == null) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noData);
            return;
        }

        UserDataUtil.removeUserData(user);

        HttpServer.returnJsonHttpData(response, new JsonHttpData("创建成功"));
    }

    @RequestPath("/change_data")
    @RequestType(RequestType.Type.POST)
    public static void changeData(HttpServletRequest request, HttpServletResponse response,
                                  @BodyData("user") String user,
                                  @BodyData("password") String password,
                                  @BodyData("plugins") String pluginsData,
                                  @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        UserData userData = UserDataUtil.getUserData(user);
        if (userData == null) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noData);
            return;
        }
        List<String> plugins = List.of(pluginsData.split(","));

        if (password.equals("noChange")) {
            userData.setPassword(Sha256Util.sha256(password));
            UserDataUtil.updateUserData(userData);
        }

        List<UserBuyData> userBuyDataList = UserBuyDataUtil.getUserUserBuyDataList(user);
        for (UserBuyData userBuyData : userBuyDataList) {
            String plugin = userBuyData.getPlugin();
            if (plugins.contains(plugin)) {
                continue;
            }

            UserBuyDataUtil.removeUserBuyData(user, plugin);
        }

        for (String plugin : plugins) {
            if (UserBuyDataUtil.getUserBuyData(user, plugin) != null) {
                continue;
            }

            UserBuyData userBuyData = new UserBuyData();
            userBuyData.setUser(user);
            userBuyData.setPlugin(plugin);

            UserBuyDataUtil.updateUserBuyData(userBuyData);
        }

        HttpServer.returnJsonHttpData(response, new JsonHttpData("修改成功"));
    }
}
