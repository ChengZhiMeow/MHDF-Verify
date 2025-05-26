package cn.chengzhiya.mhdfverify.server;

import cn.chengzhiya.mhdfverify.Main;
import cn.chengzhiya.mhdfverify.annotation.RequestType;
import cn.chengzhiya.mhdfverify.controller.ServerController;
import cn.chengzhiya.mhdfverify.entity.http.JsonHttpData;
import cn.chengzhiya.mhdfverify.util.AnnotationUtil;
import cn.chengzhiya.mhdfverify.util.StringUtil;
import cn.chengzhiya.mhdfverify.util.config.ConfigUtil;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.*;

public final class HttpServer extends HttpServlet {
    /**
     * 返回JSON数据实例
     *
     * @param response 请求实例
     * @param data     JSON数据实例
     */
    public static void returnJsonHttpData(HttpServletResponse response, JsonHttpData data) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(data.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回文件数据
     *
     * @param response 请求实例
     * @param file     文件实例
     */
    public static void returnFileHttpData(ServletResponse response, File file) {
        if (file == null || !file.exists()) {
            return;
        }

        try {
            try (InputStream in = Files.newInputStream(file.toPath())) {
                try (OutputStream out = response.getOutputStream()) {
                    int len;
                    byte[] byteData = new byte[1042];
                    while ((len = in.read(byteData)) != -1) {
                        out.write(byteData, 0, len);
                    }
                    out.flush();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理请求
     *
     * @param request  请求实例
     * @param response 回应实例
     * @param type     请求类型实例
     */
    private void handleRequest(HttpServletRequest request, HttpServletResponse response, RequestType.Type type) {
        String uri = request.getRequestURI();
        String path = uri.substring(0, uri.lastIndexOf("/"));
        String methodPath = uri.substring(path.length());

        Main.getLoggerManager().getLogger().info("HTTP Server {}请求({}) - {}", type, request.getRemoteAddr(), uri);

        Class<?> controller = Main.getTomcatServer().getControllerHashMap().get(path);
        if (controller != null) {
            // API使用次数统计
            {
                LocalTime time = LocalTime.now();
                String key = time.getHour() + ":" + time.getMinute();

                Map<String, Integer> apiCallCountMap = ServerController.getApiCallCountHashMap().getOrDefault(key, new HashMap<>());

                if (apiCallCountMap.size() >= 60) {
                    List<String> keys = new ArrayList<>(apiCallCountMap.keySet());
                    apiCallCountMap.remove(keys.get(keys.size() - 1));
                }

                int callCount = apiCallCountMap.getOrDefault(uri, 0);
                callCount++;
                apiCallCountMap.put(uri, callCount);

                ServerController.getApiCallCountHashMap().put(key, apiCallCountMap);
            }

            for (Method method : controller.getMethods()) {
                if (AnnotationUtil.getRequestType(method) != type) {
                    continue;
                }

                if (!Objects.equals(AnnotationUtil.getRequestPath(method), methodPath)) {
                    continue;
                }

                List<Object> data = new ArrayList<>();

                JSONObject body = null;
                try {
                    InputStream in = request.getInputStream();
                    byte[] bytes = in.readAllBytes();
                    body = JSONObject.parseObject(new String(bytes));
                } catch (IOException ignored) {
                }

                for (Parameter parameter : method.getParameters()) {
                    if (parameter.getType().equals(HttpServletRequest.class)) {
                        data.add(request);
                        continue;
                    }
                    if (parameter.getType().equals(HttpServletResponse.class)) {
                        data.add(response);
                        continue;
                    }

                    String paramData = AnnotationUtil.getDefaultValue(parameter);

                    // 获取cookie中的数据
                    {
                        String paramName = AnnotationUtil.getCookieDataName(parameter);
                        if (paramName != null && request.getCookies() != null) {
                            for (Cookie cookie : request.getCookies()) {
                                if (!cookie.getName().equals(paramName)) {
                                    continue;
                                }

                                if (cookie.getValue() == null) {
                                    continue;
                                }

                                paramData = cookie.getValue();
                                break;
                            }

                            if (paramData == null || paramData.isEmpty()) {
                                returnJsonHttpData(response, JsonHttpData.noCookie);
                                return;
                            }
                        }
                    }

                    // 获取请求参数中的数据
                    {
                        String paramName = AnnotationUtil.getRequestParamName(parameter);
                        if (paramName != null) {
                            if (request.getParameter(paramName) != null) {
                                paramData = request.getParameter(paramName);
                            }

                            if (paramData == null || paramData.isEmpty()) {
                                returnJsonHttpData(response, JsonHttpData.noParam);
                                return;
                            }
                        }
                    }

                    // 获取请求数据中的数据
                    {
                        String paramName = AnnotationUtil.getBodyDataName(parameter);
                        if (paramName != null) {
                            if (body == null) {
                                data.add(null);
                                continue;
                            }

                            Object bodyData = null;
                            if (body.getObject(paramName, parameter.getType()) != null) {
                                bodyData = body.getObject(paramName, parameter.getType());
                            }

                            if (bodyData == null) {
                                returnJsonHttpData(response, JsonHttpData.noParam);
                                return;
                            }

                            data.add(bodyData);
                            continue;
                        }
                    }

                    data.add(StringUtil.converter(
                            parameter.getType(),
                            paramData
                    ));
                }

                try {
                    method.invoke(null, data.toArray());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return;
            }
        }

        if (ConfigUtil.getConfig().getBoolean("serverSettings.adminWeb.enable")) {
            if (type == RequestType.Type.GET) {
                int i = uri.lastIndexOf(".");
                if (i == -1) {
                    response.setContentType("text/html");
                    returnFileHttpData(response, new File("./dist/index.html"));
                    return;
                }

                String suffix = uri.substring(i + 1);
                String finalSuffix = switch (suffix) {
                    case "js" -> "javascript";
                    default -> suffix;
                };

                response.setContentType("text/" + finalSuffix);
                try {
                    returnFileHttpData(response, new File("./dist/" + uri));
                    return;
                } catch (Exception ignored) {
                }
            }
        }

        returnJsonHttpData(response, JsonHttpData.noInterface);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        handleRequest(request, response, RequestType.Type.GET);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        handleRequest(request, response, RequestType.Type.POST);
    }
}
