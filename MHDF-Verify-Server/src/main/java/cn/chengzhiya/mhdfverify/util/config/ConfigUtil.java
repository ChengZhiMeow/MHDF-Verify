package cn.chengzhiya.mhdfverify.util.config;

import cn.chengzhiya.mhdfverify.entity.config.YamlConfiguration;
import cn.chengzhiya.mhdfverify.exception.ResourceException;
import lombok.Getter;

import java.io.File;

public final class ConfigUtil {
    @Getter
    private static YamlConfiguration config;

    /**
     * 保存默认配置文件
     */
    public static void saveDefaultConfig() {
        try {
            FileUtil.saveResource("config.yml", "config.yml", false);
        } catch (ResourceException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 重载配置文件
     */
    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File("config.yml"));
    }
}
