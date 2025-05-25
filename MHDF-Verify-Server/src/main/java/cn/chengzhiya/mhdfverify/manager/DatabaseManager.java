package cn.chengzhiya.mhdfverify.manager;

import cn.chengzhiya.mhdfverify.entity.database.PluginData;
import cn.chengzhiya.mhdfverify.entity.database.UserBuyData;
import cn.chengzhiya.mhdfverify.entity.database.UserData;
import cn.chengzhiya.mhdfverify.util.config.ConfigUtil;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;
import java.util.TimeZone;

@Getter
@SuppressWarnings("unused")
public final class DatabaseManager {
    @Setter
    private String type = "none";
    private String databaseUrl = "";
    private HikariDataSource hikariDataSource;
    private DataSourceConnectionSource connectionSource;

    /**
     * 连接并初始化数据库
     */
    public void connect() {
        String type = ConfigUtil.getConfig().getString("databaseSettings.type");

        if (type == null) {
            throw new RuntimeException("数据库类型未设置");
        }

        switch (type) {
            // 初始化MariaDB/MySQL数据库的连接
            case "mariadb", "mysql" -> {
                this.type = "mysql";

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("数据库驱动加载失败");
                }

                databaseUrl = "jdbc:mysql://" + ConfigUtil.getConfig().getString("databaseSettings.mysql.host") + "/" + ConfigUtil.getConfig().getString("databaseSettings.mysql.database");
                HikariConfig config = getHikariConfig(this.databaseUrl);

                config.setUsername(ConfigUtil.getConfig().getString("databaseSettings.mysql.user"));
                config.setPassword(ConfigUtil.getConfig().getString("databaseSettings.mysql.password"));

                initDataSource(config);
            }
            // 初始化Sqlite/H2数据库的连接
            case "sqlite", "h2" -> {
                this.type = "h2";

                try {
                    Class.forName("org.h2.Driver");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("数据库驱动加载失败");
                }

                File file = new File(Objects.requireNonNull(ConfigUtil.getConfig().getString("databaseSettings.h2.file")));
                databaseUrl = "jdbc:h2:" + file.getAbsolutePath();

                initDataSource(getHikariConfig(this.databaseUrl));
            }
            default -> throw new RuntimeException("不支持的数据库类型: " + type);
        }
    }

    /**
     * 关闭数据库连接源
     */
    public void close() {
        try {
            this.connectionSource.close();
            this.hikariDataSource.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取数据库配置
     *
     * @return 数据库配置实例
     */
    public HikariConfig getHikariConfig(String databaseUrl) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("serverTimezone", TimeZone.getDefault().getID());
        return config;
    }

    /**
     * 根据数据库配置实例初始化数据源
     *
     * @param config 数据库配置实例
     */
    public void initDataSource(HikariConfig config) {
        try {
            this.hikariDataSource = new HikariDataSource(config);
            this.connectionSource = new DataSourceConnectionSource(this.hikariDataSource, this.databaseUrl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化数据库数据实例
     */
    public void initTable() {
        try {
            TableUtils.createTableIfNotExists(getConnectionSource(), PluginData.class);
        } catch (SQLException ignored) {
        }
        try {
            TableUtils.createTableIfNotExists(getConnectionSource(), UserData.class);
        } catch (SQLException ignored) {
        }
        try {
            TableUtils.createTableIfNotExists(getConnectionSource(), UserBuyData.class);
        } catch (SQLException ignored) {
        }
    }
}
