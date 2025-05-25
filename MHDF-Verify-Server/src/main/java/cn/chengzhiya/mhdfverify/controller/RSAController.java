package cn.chengzhiya.mhdfverify.controller;

import cn.chengzhiya.mhdfverify.Main;
import cn.chengzhiya.mhdfverify.annotation.RequestPath;
import cn.chengzhiya.mhdfverify.annotation.RequestType;
import cn.chengzhiya.mhdfverify.entity.http.JsonHttpData;
import cn.chengzhiya.mhdfverify.server.HttpServer;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.http.HttpServletResponse;

@RequestPath("/api/rsa")
public final class RSAController {
    @RequestPath("/get_public")
    @RequestType(RequestType.Type.GET)
    public static void getPublic(HttpServletResponse response) {
        JSONObject data = new JSONObject();
        data.put("key", Main.getRsaManager().getPublicKey().getEncoded());

        HttpServer.returnJsonHttpData(response, new JsonHttpData(data));
    }
}
