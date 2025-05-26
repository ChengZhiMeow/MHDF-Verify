package cn.chengzhiya.mhdfverify.controller;

import cn.chengzhiya.mhdfverify.annotation.*;
import cn.chengzhiya.mhdfverify.entity.database.PluginData;
import cn.chengzhiya.mhdfverify.entity.http.JsonHttpData;
import cn.chengzhiya.mhdfverify.server.HttpServer;
import cn.chengzhiya.mhdfverify.util.JwtUtil;
import cn.chengzhiya.mhdfverify.util.database.PluginDataUtil;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequestPath("/api/plugin")
public final class PluginController {
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

        List<PluginData> userDataList = PluginDataUtil.getPluginDataList();
        int start = (page - 1) * 10;
        int end = Math.min(userDataList.size(), page * 10);

        JSONObject data = new JSONObject();
        data.put("maxPages", (int) Math.ceil(userDataList.size() / 10.0));

        JSONObject plugins = new JSONObject();
        for (PluginData pluginData : userDataList.subList(start, end)) {
            JSONObject plugin = new JSONObject();
            plugin.put("version", pluginData.getVersion());
            plugin.put("client", pluginData.getClient());

            plugins.put(pluginData.getPlugin(), plugin);
        }

        data.put("plugins", plugins);

        HttpServer.returnJsonHttpData(response, new JsonHttpData(data));
    }

    @RequestPath("/get_info")
    @RequestType(RequestType.Type.GET)
    public static void getInfo(HttpServletResponse response,
                               @RequestParam("plugin") String plugin
    ) {
        PluginData pluginData = PluginDataUtil.getPluginData(plugin);
        if (pluginData == null) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noData);
            return;
        }

        HttpServer.returnJsonHttpData(response, new JsonHttpData(pluginData.toJSONObject()));
    }

    @RequestPath("/create")
    @RequestType(RequestType.Type.POST)
    public static void create(HttpServletRequest request, HttpServletResponse response,
                              @BodyData("plugin") String plugin,
                              @BodyData("version") String version,
                              @BodyData("client") Integer client,
                              @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        if (PluginDataUtil.getPluginData(plugin) != null) {
            HttpServer.returnJsonHttpData(response, new JsonHttpData(400, "这个插件已经被创建了"));
            return;
        }

        PluginData pluginData = new PluginData();
        pluginData.setPlugin(plugin);
        pluginData.setVersion(version);
        pluginData.setClient(client);
        PluginDataUtil.updatePluginData(pluginData);

        HttpServer.returnJsonHttpData(response, new JsonHttpData("创建成功"));
    }

    @RequestPath("/remove")
    @RequestType(RequestType.Type.POST)
    public static void remove(HttpServletRequest request, HttpServletResponse response,
                              @BodyData("plugin") String plugin,
                              @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        if (PluginDataUtil.getPluginData(plugin) == null) {
            HttpServer.returnJsonHttpData(response, new JsonHttpData(400, "这个插件没有被创建了"));
            return;
        }

        PluginDataUtil.removePluginData(plugin);

        HttpServer.returnJsonHttpData(response, new JsonHttpData("删除成功"));
    }

    @RequestPath("/change_data")
    @RequestType(RequestType.Type.POST)
    public static void changeData(HttpServletRequest request, HttpServletResponse response,
                                  @BodyData("plugin") String plugin,
                                  @BodyData("version") String version,
                                  @BodyData("client") Integer client,
                                  @CookieData("token") String token
    ) {
        if (!JwtUtil.checkLogin("MHDFVerify-WebManager-Jwt", request.getRemoteHost(), token)) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noAuth);
            return;
        }

        PluginData pluginData = PluginDataUtil.getPluginData(plugin);
        if (pluginData == null) {
            HttpServer.returnJsonHttpData(response, JsonHttpData.noData);
            return;
        }

        pluginData.setVersion(version);
        pluginData.setClient(client);
        PluginDataUtil.updatePluginData(pluginData);

        HttpServer.returnJsonHttpData(response, new JsonHttpData("修改成功"));
    }
}
