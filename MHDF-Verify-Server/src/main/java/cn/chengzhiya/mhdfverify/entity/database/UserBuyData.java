package cn.chengzhiya.mhdfverify.entity.database;

import com.alibaba.fastjson2.JSONObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "mhdfverify_userbuy")
public final class UserBuyData {
    @DatabaseField(generatedId = true, canBeNull = false)
    private int id;
    @DatabaseField(index = true, canBeNull = false)
    private String user;
    @DatabaseField(index = true, canBeNull = false)
    private String plugin;

    /**
     * 转换为json实例
     *
     * @return json实例
     */
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("user", user);
        jsonObject.put("plugin", plugin);

        return jsonObject;
    }
}
