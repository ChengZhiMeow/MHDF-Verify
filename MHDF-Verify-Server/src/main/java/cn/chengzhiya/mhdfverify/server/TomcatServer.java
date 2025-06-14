package cn.chengzhiya.mhdfverify.server;

import cn.chengzhiya.mhdfverify.Main;
import cn.chengzhiya.mhdfverify.annotation.RequestPath;
import cn.chengzhiya.mhdfverify.entity.http.SSLConfig;
import cn.chengzhiya.mhdfverify.filter.CorsFilter;
import lombok.Getter;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.websocket.server.WsSci;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public final class TomcatServer {
    private Tomcat server;
    private final HashMap<String, Class<?>> controllerHashMap = new HashMap<>();

    public TomcatServer() {
        try {
            Reflections reflections = new Reflections(Main.class.getPackageName());

            for (Class<?> clazz : reflections.getTypesAnnotatedWith(RequestPath.class)) {
                String path = clazz.getAnnotation(RequestPath.class).value();
                getControllerHashMap().put(path, clazz);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取服务器实例
     *
     * @param port      服务器端口
     * @param sslConfig SSL配置实例
     * @return 服务器实例
     */
    private static Connector getConnector(int port, SSLConfig sslConfig) {
        Connector connector = new Connector("HTTP/1.1");
        connector.setPort(port);
        if (sslConfig.isEnable()) {
            connector.setSecure(true);
            connector.setScheme("https");
            connector.setAttribute("keyAlias", sslConfig.getAlias());
            connector.setAttribute("keystorePass", sslConfig.getKey());
            connector.setAttribute("keystoreFile", sslConfig.getFile());
            connector.setAttribute("clientAuth", "false");
            connector.setAttribute("sslProtocol", "TLS");
            connector.setAttribute("SSLEnabled", true);
        }
        return connector;
    }

    /**
     * 启动服务器
     *
     * @param port      服务器端口
     * @param sslConfig SSL配置实例
     */
    public void start(int port, SSLConfig sslConfig) {
        new Thread(() -> {
            try {
                Logger tomcatLogger = Logger.getLogger("org.apache");
                tomcatLogger.setLevel(Level.OFF);
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.OFF);
                tomcatLogger.addHandler(consoleHandler);

                this.server = new Tomcat();

                Connector connector = getConnector(port, sslConfig);
                this.server.getService().addConnector(connector);

                Context context = this.server.addContext("", null);

                context.addServletContainerInitializer(new WsSci(), Set.of(WebsocketServer.class));
                Tomcat.addServlet(context, "dispatcherServlet", new HttpServer());
                context.addServletMappingDecoded("/", "dispatcherServlet");

                // 允许跨域
                {
                    FilterDef corsFilterDef = new FilterDef();
                    corsFilterDef.setFilterName("corsFilter");
                    corsFilterDef.setFilter(new CorsFilter());
                    context.addFilterDef(corsFilterDef);

                    FilterMap corsFilterMap = new FilterMap();
                    corsFilterMap.setFilterName("corsFilter");
                    corsFilterMap.addURLPattern("/*");
                    context.addFilterMap(corsFilterMap);
                }

                this.server.start();
                this.server.getServer().await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
