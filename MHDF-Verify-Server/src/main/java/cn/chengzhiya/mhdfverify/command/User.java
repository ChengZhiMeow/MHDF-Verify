package cn.chengzhiya.mhdfverify.command;

import cn.chengzhiya.mhdfverify.Main;
import cn.chengzhiya.mhdfverify.entity.database.PluginData;
import cn.chengzhiya.mhdfverify.entity.database.UserBuyData;
import cn.chengzhiya.mhdfverify.entity.database.UserData;
import cn.chengzhiya.mhdfverify.util.Sha256Util;
import cn.chengzhiya.mhdfverify.util.database.PluginDataUtil;
import cn.chengzhiya.mhdfverify.util.database.UserBuyDataUtil;
import cn.chengzhiya.mhdfverify.util.database.UserDataUtil;

import java.util.List;

public final class User implements TabExecutor {
    @Override
    public void onCommand(String command, String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "list" -> {
                    List<String> userList = UserDataUtil.getUserDataList().stream()
                            .map(UserData::getUser)
                            .toList();

                    Main.getLoggerManager().getLogger().info("====================用户管理====================");
                    Main.getLoggerManager().getLogger().info("用户列表({}):", userList.size());
                    Main.getLoggerManager().getLogger().info(userList);
                    Main.getLoggerManager().getLogger().info("====================用户管理====================");
                    return;
                }
                case "info" -> {
                    if (args.length != 2) {
                        Main.getLoggerManager().getLogger().error("用法错误, 正确用法: user info <用户名称>!");
                        return;
                    }

                    UserData userData = UserDataUtil.getUserData(args[1]);
                    if (userData == null) {
                        Main.getLoggerManager().getLogger().error("用户不存在!");
                        return;
                    }
                    List<String> userBuyList = UserBuyDataUtil.getPluginUserBuyDataList(userData.getUser()).stream()
                            .map(UserBuyData::getPlugin)
                            .toList();

                    Main.getLoggerManager().getLogger().info("====================用户管理====================");
                    Main.getLoggerManager().getLogger().info("用户: {}", userData.getUser());
                    Main.getLoggerManager().getLogger().info("密码: {}", userData.getPassword());
                    Main.getLoggerManager().getLogger().info("购买插件({}):", userBuyList.size());
                    Main.getLoggerManager().getLogger().info(userBuyList);
                    Main.getLoggerManager().getLogger().info("====================用户管理====================");
                    return;
                }
                case "create" -> {
                    if (args.length != 3) {
                        Main.getLoggerManager().getLogger().error("用法错误, 正确用法: user create <用户名称> <密码>!");
                        return;
                    }

                    if (UserDataUtil.getUserData(args[0]) != null) {
                        Main.getLoggerManager().getLogger().error("用户已存在!");
                        return;
                    }

                    UserData userData = new UserData();
                    userData.setUser(args[1]);
                    userData.setPassword(Sha256Util.sha256(args[2]));
                    UserDataUtil.updateUserData(userData);

                    Main.getLoggerManager().getLogger().info("创建成功!");
                    return;
                }
                case "remove" -> {
                    if (args.length != 2) {
                        Main.getLoggerManager().getLogger().error("用法错误, 正确用法: user remove <用户名称>!");
                        return;
                    }

                    if (UserDataUtil.getUserData(args[1]) == null) {
                        Main.getLoggerManager().getLogger().error("用户不存在!");
                        return;
                    }

                    UserDataUtil.removeUserData(args[1]);
                    Main.getLoggerManager().getLogger().info("删除成功!");
                    return;
                }
                case "setPassword" -> {
                    if (args.length != 3) {
                        Main.getLoggerManager().getLogger().error("用法错误, 正确用法: user setPassword <用户名称> <密码>!");
                        return;
                    }

                    UserData userData = UserDataUtil.getUserData(args[1]);
                    if (userData == null) {
                        Main.getLoggerManager().getLogger().error("用户不存在!");
                        return;
                    }

                    userData.setPassword(Sha256Util.sha256(args[2]));
                    UserDataUtil.updateUserData(userData);

                    Main.getLoggerManager().getLogger().info("修改成功!");
                    return;
                }
                case "addBuy" -> {
                    if (args.length != 3) {
                        Main.getLoggerManager().getLogger().error("用法错误, 正确用法: user setPassword <用户名称> <密码>!");
                        return;
                    }

                    if (UserDataUtil.getUserData(args[1]) == null) {
                        Main.getLoggerManager().getLogger().error("用户不存在!");
                        return;
                    }

                    if (PluginDataUtil.getPluginData(args[2]) == null) {
                        Main.getLoggerManager().getLogger().error("插件不存在!");
                        return;
                    }

                    if (UserBuyDataUtil.getUserBuyData(args[1], args[2]) != null) {
                        Main.getLoggerManager().getLogger().error("目标已经购买了!");
                        return;
                    }

                    UserBuyData userBuyData = new UserBuyData();
                    userBuyData.setUser(args[1]);
                    userBuyData.setPlugin(args[2]);
                    UserBuyDataUtil.updateUserBuyData(userBuyData);

                    Main.getLoggerManager().getLogger().info("添加成功!");
                    return;
                }
            }
        }

        Main.getLoggerManager().getLogger().info("====================插件管理====================");
        Main.getLoggerManager().getLogger().info("user list - 查看用户列表");
        Main.getLoggerManager().getLogger().info("user info <用户名称> - 查看用户信息");
        Main.getLoggerManager().getLogger().info("user create <用户名称> <密码> - 创建用户");
        Main.getLoggerManager().getLogger().info("user remove <用户名称> - 移除用户");
        Main.getLoggerManager().getLogger().info("user setPassword <用户名称> <密码> - 修改用户密码");
        Main.getLoggerManager().getLogger().info("user addBuy <用户名称> <插件名称> - 添加用户购买");
        Main.getLoggerManager().getLogger().info("user help - 查看帮助");
        Main.getLoggerManager().getLogger().info("====================插件管理====================");
    }

    @Override
    public List<String> onTabComplete(String command, String[] args) {
        if (args.length == 1) {
            return List.of("list", "info", "create", "remove", "setPassword", "addBuy", "help");
        }
        if (args.length == 2) {
            switch (args[1]) {
                case "info", "remove", "setPassword", "addBuy" -> {
                    return UserDataUtil.getUserDataList().stream()
                            .map(UserData::getUser)
                            .toList();
                }
            }
        }
        if (args.length == 3) {
            switch (args[2]) {
                case "addBuy" -> {
                    return PluginDataUtil.getPluginDataList().stream()
                            .map(PluginData::getPlugin)
                            .toList();
                }
            }
        }
        return List.of();
    }
}
