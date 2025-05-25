package cn.chengzhiya.mhdfverify.util.database;

import cn.chengzhiya.mhdfverify.Main;
import cn.chengzhiya.mhdfverify.entity.database.PluginData;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

public final class PluginDataUtil {
    private static final ThreadLocal<Dao<PluginData, String>> daoThread =
            ThreadLocal.withInitial(() -> {
                try {
                    return DaoManager.createDao(Main.getDatabaseManager().getConnectionSource(), PluginData.class);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

    /**
     * 获取dao实例
     *
     * @return dao实例
     */
    public static Dao<PluginData, String> getDao() {
        return daoThread.get();
    }

    /**
     * 获取插件数据实例列表
     *
     * @return 插件数据实例列表
     */
    public static List<PluginData> getPluginDataList() {
        try {
            return getDao().queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定插件名称的插件数据实例
     *
     * @param name 插件名称
     * @return 插件数据实例
     */
    public static PluginData getPluginData(String name) {
        try {
            return getDao().queryForId(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除指定插件名称的插件数据实例
     *
     * @param name 插件名称
     */
    public static void removePluginData(String name) {
        try {
            getDao().deleteById(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 更新插件数据实例在数据库中的数据
     *
     * @param pluginData 插件数据实例
     */
    public static void updatePluginData(PluginData pluginData) {
        try {
            getDao().createOrUpdate(pluginData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
