package cn.chengzhiya.mhdfverify.util;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class JwtUtil {
    @Getter
    private static final String jwtToken = UUID.randomUUID().toString();
    @Getter
    private static final ConcurrentHashMap<Integer, JSONObject> userLoginHashMap = new ConcurrentHashMap<>();

    /**
     * 获取指定文本的签名
     *
     * @param string 文本
     * @return 签名值
     */
    private static String sign(String string) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    getJwtToken().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKey);

            return Sha256Util.sha256(mac.doFinal(string.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 在指定IP登录指定账户登录数据
     *
     * @param ip   ip
     * @param data 账户登录数据
     * @return 账户密钥
     */
    public static String jwtLogin(String type, String ip, String data, JSONObject other, long outTime) {
        long loginTime = System.currentTimeMillis();

        JSONObject params = new JSONObject();
        params.put("type", type);
        params.put("ip", ip);
        params.put("data", data);
        params.put("other", other);
        params.put("start", loginTime);
        params.put("end", outTime >= 0 ? (loginTime + outTime) : Long.MAX_VALUE);
        params.put("id", getUserLoginHashMap().size());

        JSONObject loginData = new JSONObject();
        loginData.put("params", params);
        loginData.put("sign", sign(params.toJSONString()));

        getUserLoginHashMap().put(getUserLoginHashMap().size(), loginData);
        return Base64Util.encode(loginData.toJSONString());
    }

    /**
     * 检测指定IP是否登录了指定账户密钥
     *
     * @param ip    IP
     * @param token 账户密钥
     * @return 结果
     */
    public static boolean checkLogin(String type, String ip, String token) {
        try {
            if (type == null || ip == null || token == null) {
                return false;
            }
            String loginDataString = new String(Base64Util.decode(token));
            JSONObject loginData = JSONObject.parseObject(loginDataString);
            JSONObject params = loginData.getJSONObject("params");

            if (!params.getString("type").equals(type)) {
                return false;
            }

            if (!params.getString("ip").equals(ip)) {
                return false;
            }

            if (!getUserLoginHashMap().containsKey(params.getInteger("id"))) {
                return false;
            }

            if (params.getLong("start") >= params.getLong("end") || params.getLong("start") >= System.currentTimeMillis()) {
                return false;
            }

            if (params.getLong("end") <= System.currentTimeMillis()) {
                return false;
            }

            return loginData.getString("sign").equals(sign(params.toString()));
        } catch (JSONException ignored) {
            return false;
        }
    }
}
