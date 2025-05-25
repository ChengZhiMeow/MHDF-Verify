package cn.chengzhiya.mhdfverify.entity.http;

import cn.chengzhiya.mhdfverify.entity.config.YamlConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class SSLConfig {
    private boolean enable;
    private String alias;
    private String file;
    private String key;

    public SSLConfig() {
    }

    public SSLConfig(YamlConfiguration config) {
        this.enable = config.getBoolean("enable");
        this.alias = config.getString("alias");
        this.file = config.getString("file");
        this.key = config.getString("key");
    }
}
