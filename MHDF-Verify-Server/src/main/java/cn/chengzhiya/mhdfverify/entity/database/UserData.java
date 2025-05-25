package cn.chengzhiya.mhdfverify.entity.database;

import com.alibaba.fastjson2.JSONObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DatabaseTable(tableName = "mhdfverify_user")
public final class UserData {
    @DatabaseField(id = true, canBeNull = false)
    private String user;
    @DatabaseField(canBeNull = false)
    private String password;

    /**
     * 转换为json实例
     *
     * @return json实例
     */
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", user);
        jsonObject.put("password", password);

        return jsonObject;
    }
}
