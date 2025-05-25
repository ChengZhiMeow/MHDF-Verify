package cn.chengzhiya.mhdfverify.entity.http;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class JsonHttpData {
    public static final JsonHttpData serverError = new JsonHttpData(500, "服务器发生内部错误");
    public static final JsonHttpData noInterface = new JsonHttpData(404, "找不到目标接口");
    public static final JsonHttpData noData = new JsonHttpData(404, "找不到数据");
    public static final JsonHttpData noAuth = new JsonHttpData(401, "未登录或登录失效");
    public static final JsonHttpData noCookie = new JsonHttpData(400, "找不到cookie");
    public static final JsonHttpData noParam = new JsonHttpData(400, "传参错误");

    private int id = 200;
    private String msg = "";
    private JSONObject data = new JSONObject();

    public JsonHttpData() {
    }

    public JsonHttpData(int id, String msg, JSONObject data) {
        this.id = id;
        this.msg = msg;
        this.data = data;
    }

    public JsonHttpData(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public JsonHttpData(String msg, JSONObject data) {
        this.msg = msg;
        this.data = data;
    }

    public JsonHttpData(JSONObject data) {
        this.data = data;
    }

    public JsonHttpData(String msg) {
        this.msg = msg;
    }

    public JsonHttpData(int id) {
        this.id = id;
    }

    public String toString() {
        JSONObject dataJson = new JSONObject();
        dataJson.put("id", id);
        dataJson.put("msg", msg);
        dataJson.put("data", data);

        return dataJson.toString();
    }
}
