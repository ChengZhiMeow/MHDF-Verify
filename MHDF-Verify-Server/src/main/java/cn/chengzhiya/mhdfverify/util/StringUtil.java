package cn.chengzhiya.mhdfverify.util;

public final class StringUtil {
    public static <T> T converter(Class<T> type, String data) {
        if (type == null || data == null) {
            return null;
        }

        if (type.equals(Integer.class)) {
            return type.cast(Integer.parseInt(data));
        }
        if (type.equals(Double.class)) {
            return type.cast(Double.parseDouble(data));
        }
        if (type.equals(Float.class)) {
            return type.cast(Float.parseFloat(data));
        }

        return type.cast(data);
    }
}
