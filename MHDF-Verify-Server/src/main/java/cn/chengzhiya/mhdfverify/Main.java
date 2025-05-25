package cn.chengzhiya.mhdfverify;

import cn.chengzhiya.mhdfverify.command.Plugin;
import cn.chengzhiya.mhdfverify.command.User;
import cn.chengzhiya.mhdfverify.console.CommandCompleter;
import cn.chengzhiya.mhdfverify.entity.Command;
import cn.chengzhiya.mhdfverify.entity.http.SSLConfig;
import cn.chengzhiya.mhdfverify.manager.CommandManager;
import cn.chengzhiya.mhdfverify.manager.DatabaseManager;
import cn.chengzhiya.mhdfverify.manager.LoggerManager;
import cn.chengzhiya.mhdfverify.manager.RSAManager;
import cn.chengzhiya.mhdfverify.server.TomcatServer;
import cn.chengzhiya.mhdfverify.util.config.ConfigUtil;
import lombok.Getter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

import java.util.Objects;

public class Main {
    @Getter
    private static final String version = "1.1.0";
    @Getter
    private static final LoggerManager loggerManager = new LoggerManager();
    @Getter
    private static final CommandManager commandManager = new CommandManager();
    @Getter
    private static final TomcatServer tomcatServer = new TomcatServer();
    @Getter
    private static final RSAManager rsaManager = new RSAManager();
    @Getter
    private static final DatabaseManager databaseManager = new DatabaseManager();
    @Getter
    private static long startTime = -1;

    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();

        ConfigUtil.saveDefaultConfig();
        ConfigUtil.reloadConfig();

        getCommandManager().registerCommand(new Command("user")
                .executor(new User())
                .completer(new User())
        );

        getCommandManager().registerCommand(new Command("plugin")
                .executor(new Plugin())
                .completer(new Plugin())
        );

        getDatabaseManager().connect();
        getDatabaseManager().initTable();

        int port = ConfigUtil.getConfig().getInt("serverSettings.port");
        getTomcatServer().start(
                port,
                new SSLConfig(Objects.requireNonNull(ConfigUtil.getConfig().getConfigurationSection("serverSettings.ssl")))
        );

        Long endTime = System.currentTimeMillis();
        getLoggerManager().getLogger().info("启动成功,本次启动时长: {}ms", endTime - startTime);
        getLoggerManager().getLogger().info("后台管理: http://127.0.0.1:{}/admin/login", port);

        Main.startTime = endTime;

        try {
            LineReader lineReader = LineReaderBuilder.builder()
                    .completer(new CommandCompleter())
                    .build();

            String line;
            while ((line = lineReader.readLine()) != null) {
                getCommandManager().executeCommand(line);
            }
        } catch (UserInterruptException e) {
            System.exit(0);
        }
    }
}