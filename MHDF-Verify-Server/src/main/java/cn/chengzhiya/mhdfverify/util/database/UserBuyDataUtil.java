package cn.chengzhiya.mhdfverify.util.database;

import cn.chengzhiya.mhdfverify.Main;
import cn.chengzhiya.mhdfverify.entity.database.UserBuyData;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class UserBuyDataUtil {
    private static final ThreadLocal<Dao<UserBuyData, Integer>> daoThread =
            ThreadLocal.withInitial(() -> {
                try {
                    return DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), UserBuyData.class);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

    /**
     * 获取dao实例
     *
     * @return dao实例
     */
    public static Dao<UserBuyData, Integer> getDao() {
        return daoThread.get();
    }

    /**
     * 获取指定用户名称下所有用户购买数据实例
     *
     * @param user 用户名称
     * @return 用户购买数据实例
     */
    public static List<UserBuyData> getUserUserBuyDataList(String user) {
        try {
            List<UserBuyData> userBuyDataList = getDao().queryBuilder().where()
                    .eq("user", user)
                    .query();
            return Objects.requireNonNullElseGet(userBuyDataList, ArrayList::new);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定插件名称下所有用户购买数据实例
     *
     * @param plugin 插件名称
     * @return 用户购买数据实例
     */
    public static List<UserBuyData> getPluginUserBuyDataList(String plugin) {
        try {
            List<UserBuyData> userBuyDataList = getDao().queryBuilder().where()
                    .eq("plugin", plugin)
                    .query();
            return Objects.requireNonNullElseGet(userBuyDataList, ArrayList::new);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定用户名称下的指定插件名称的用户购买数据实例
     *
     * @param user   用户名称
     * @param plugin 插件名称
     * @return 用户购买数据实例
     */
    public static UserBuyData getUserBuyData(String user, String plugin) {
        try {
            return getDao().queryBuilder().where()
                    .eq("user", user)
                    .and()
                    .eq("plugin", plugin)
                    .queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除指定用户名称的用户购买数据实例
     *
     * @param user   用户名称
     * @param plugin 插件名称
     */
    public static void removeUserBuyData(String user, String plugin) {
        try {
            getDao().delete(getUserBuyData(user, plugin));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 更新用户购买数据实例在数据库中的数据
     *
     * @param userData 用户购买数据实例
     */
    public static void updateUserBuyData(UserBuyData userData) {
        try {
            getDao().createOrUpdate(userData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
