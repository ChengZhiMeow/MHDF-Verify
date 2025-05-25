package cn.chengzhiya.mhdfverify.entity.database;

import com.alibaba.fastjson2.JSONObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "mhdfverify_plugin")
public final class PluginData {
    @DatabaseField(id = true, canBeNull = false)
    private String plugin;
    @DatabaseField(canBeNull = false)
    private String version;
    @DatabaseField(canBeNull = false, defaultValue = "-1")
    private int client;

    /**
     * 转换为json实例
     *
     * @return json实例
     */
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("plugin", plugin);
        jsonObject.put("version", version);
        jsonObject.put("client", client);

        return jsonObject;
    }
}
