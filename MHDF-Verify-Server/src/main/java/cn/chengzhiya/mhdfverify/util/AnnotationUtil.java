package cn.chengzhiya.mhdfverify.util;

import cn.chengzhiya.mhdfverify.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class AnnotationUtil {
    public static String getRequestPath(Method method) {
        RequestPath annotation = method.getAnnotation(RequestPath.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    public static RequestType.Type getRequestType(Method method) {
        RequestType annotation = method.getAnnotation(RequestType.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    public static String getRequestParamName(Parameter parameter) {
        RequestParam annotation = parameter.getAnnotation(RequestParam.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    public static String getCookieDataName(Parameter parameter) {
        CookieData annotation = parameter.getAnnotation(CookieData.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    public static String getBodyDataName(Parameter parameter) {
        BodyData annotation = parameter.getAnnotation(BodyData.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    public static String getDefaultValue(Parameter parameter) {
        DefaultValue annotation = parameter.getAnnotation(DefaultValue.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }
}
