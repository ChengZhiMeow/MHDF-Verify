package cn.chengzhiya.mhdfverify.util.database;

import cn.chengzhiya.mhdfverify.Main;
import cn.chengzhiya.mhdfverify.entity.database.UserData;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

public final class UserDataUtil {
    private static final ThreadLocal<Dao<UserData, String>> daoThread =
            ThreadLocal.withInitial(() -> {
                try {
                    return DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), UserData.class);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

    /**
     * 获取dao实例
     *
     * @return dao实例
     */
    public static Dao<UserData, String> getDao() {
        return daoThread.get();
    }

    /**
     * 获取用户数据实例列表
     *
     * @return 用户数据实例列表
     */
    public static List<UserData> getUserDataList() {
        try {
            return getDao().queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定用户名称的用户数据实例
     *
     * @param name 用户名称
     * @return 用户数据实例
     */
    public static UserData getUserData(String name) {
        try {
            return getDao().queryForId(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除指定用户名称的用户数据实例
     *
     * @param name 用户名称
     */
    public static void removeUserData(String name) {
        try {
            getDao().deleteById(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 更新用户数据实例在数据库中的数据
     *
     * @param userData 用户数据实例
     */
    public static void updateUserData(UserData userData) {
        try {
            getDao().createOrUpdate(userData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
