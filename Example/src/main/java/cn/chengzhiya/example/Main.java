package cn.chengzhiya.example;

import cn.chengzhiya.mhdfverify.MHDFVerify;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static Main instance;
    private MHDFVerify mhdfVerify;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        saveDefaultConfig();
        reloadConfig();

        ConfigurationSection config = getConfig().getConfigurationSection("verifySettings");
        if (config == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        mhdfVerify = new MHDFVerify(
                this,
                "127.0.0.1:8888",
                config.getString("user"),
                config.getString("password")
        );

        if (!mhdfVerify.check()) {
            System.out.println("验证失败");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        System.out.println("验证成功");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        mhdfVerify.close();

        mhdfVerify = null;
        instance = null;
    }
}
