package cn.chengzhiya.mhdfverify.command;

import cn.chengzhiya.mhdfverify.Main;
import cn.chengzhiya.mhdfverify.entity.database.PluginData;
import cn.chengzhiya.mhdfverify.entity.database.UserBuyData;
import cn.chengzhiya.mhdfverify.util.database.PluginDataUtil;
import cn.chengzhiya.mhdfverify.util.database.UserBuyDataUtil;

import java.util.List;

public final class Plugin implements TabExecutor {
    @Override
    public void onCommand(String command, String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "list" -> {
                    List<String> pluginList = PluginDataUtil.getPluginDataList().stream()
                            .map(PluginData::getPlugin)
                            .toList();

                    Main.getLoggerManager().getLogger().info("====================插件管理====================");
                    Main.getLoggerManager().getLogger().info("插件列表({}):", pluginList.size());
                    Main.getLoggerManager().getLogger().info(pluginList);
                    Main.getLoggerManager().getLogger().info("====================插件管理====================");
                    return;
                }
                case "info" -> {
                    if (args.length != 2) {
                        Main.getLoggerManager().getLogger().error("用法错误, 正确用法: plugin info <插件名称>!");
                        return;
                    }

                    PluginData pluginData = PluginDataUtil.getPluginData(args[1]);
                    if (pluginData == null) {
                        Main.getLoggerManager().getLogger().error("插件不存在!");
                        return;
                    }
                    List<String> userBuyList = UserBuyDataUtil.getPluginUserBuyDataList(pluginData.getPlugin()).stream()
                            .map(UserBuyData::getUser)
                            .toList();

                    Main.getLoggerManager().getLogger().info("====================插件管理====================");
                    Main.getLoggerManager().getLogger().info("插件: {}", pluginData.getPlugin());
                    Main.getLoggerManager().getLogger().info("版本: {}", pluginData.getVersion());
                    Main.getLoggerManager().getLogger().info("最大IP数量: {}", pluginData.getClient());
                    Main.getLoggerManager().getLogger().info("购买用户({}):", userBuyList.size());
                    Main.getLoggerManager().getLogger().info(userBuyList);
                    Main.getLoggerManager().getLogger().info("====================插件管理====================");
                    return;
                }
                case "create" -> {
                    if (args.length < 3) {
                        Main.getLoggerManager().getLogger().error("用法错误, 正确用法: plugin create <插件名称> <版本号> [最大IP数量]!");
                        return;
                    }

                    if (PluginDataUtil.getPluginData(args[0]) != null) {
                        Main.getLoggerManager().getLogger().error("插件已存在!");
                        return;
                    }

                    PluginData pluginData = new PluginData();
                    pluginData.setPlugin(args[1]);
                    pluginData.setVersion(args[2]);

                    if (args.length >= 4) {
                        pluginData.setClient(Integer.parseInt(args[3]));
                    } else {
                        pluginData.setClient(-1);
                    }

                    PluginDataUtil.updatePluginData(pluginData);

                    Main.getLoggerManager().getLogger().info("创建成功!");
                    return;
                }
                case "remove" -> {
                    if (args.length != 2) {
                        Main.getLoggerManager().getLogger().error("用法错误, 正确用法: plugin remove <插件名称>!");
                        return;
                    }

                    if (PluginDataUtil.getPluginData(args[1]) == null) {
                        Main.getLoggerManager().getLogger().error("插件不存在!");
                        return;
                    }

                    PluginDataUtil.removePluginData(args[1]);
                    Main.getLoggerManager().getLogger().info("删除成功!");
                    return;
                }
                case "setVersion" -> {
                    if (args.length != 3) {
                        Main.getLoggerManager().getLogger().error("用法错误, 正确用法: plugin setVersion <插件名称> <版本号>!");
                        return;
                    }

                    PluginData pluginData = PluginDataUtil.getPluginData(args[1]);
                    if (pluginData == null) {
                        Main.getLoggerManager().getLogger().error("插件不存在!");
                        return;
                    }

                    pluginData.setVersion(args[2]);
                    PluginDataUtil.updatePluginData(pluginData);

                    Main.getLoggerManager().getLogger().info("修改成功!");
                    return;
                }
            }
        }

        Main.getLoggerManager().getLogger().info("====================插件管理====================");
        Main.getLoggerManager().getLogger().info("plugin list - 查看插件列表");
        Main.getLoggerManager().getLogger().info("plugin info <插件名称> - 查看插件信息");
        Main.getLoggerManager().getLogger().info("plugin create <插件名称> <版本号> - 创建插件");
        Main.getLoggerManager().getLogger().info("plugin remove <插件名称> - 移除插件");
        Main.getLoggerManager().getLogger().info("plugin setVersion <插件名称> <版本号> - 修改指定插件版本号");
        Main.getLoggerManager().getLogger().info("plugin help - 查看帮助");
        Main.getLoggerManager().getLogger().info("====================插件管理====================");
    }

    @Override
    public List<String> onTabComplete(String command, String[] args) {
        if (args.length == 1) {
            return List.of("list", "info", "create", "remove", "setVersion", "help");
        }
        if (args.length == 2) {
            switch (args[1]) {
                case "info", "remove", "setVersion" -> {
                    return PluginDataUtil.getPluginDataList().stream()
                            .map(PluginData::getPlugin)
                            .toList();
                }
            }
        }
        return List.of();
    }
}
