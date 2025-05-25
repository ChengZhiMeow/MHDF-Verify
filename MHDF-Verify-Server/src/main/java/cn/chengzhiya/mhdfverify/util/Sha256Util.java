package cn.chengzhiya.mhdfverify.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class Sha256Util {
    /**
     * 将指定数据进行SHA256处理
     *
     * @param bytes 数据
     * @return SHA256
     */
    public static String sha256(byte[] bytes) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes);

            StringBuilder stringBuffer = new StringBuilder();
            for (byte aByte : messageDigest.digest()) {
                final String temp = Integer.toHexString(aByte & 0xFF);
                if (temp.length() == 1) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(temp);
            }
            return stringBuffer.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将指定文本进行SHA256处理
     *
     * @param string 文本
     * @return SHA256
     */
    public static String sha256(String string) {
        return sha256(string.getBytes(StandardCharsets.UTF_8));
    }
}
