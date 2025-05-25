package cn.chengzhiya.mhdfverify;

import cn.chengzhiya.mhdfverify.client.WebsocketClient;
import cn.chengzhiya.mhdfverify.manager.FileManager;
import cn.chengzhiya.mhdfverify.manager.Sha256Manager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.client.WebSocketClient;

import java.io.File;
import java.util.UUID;

@Getter
public final class MHDFVerify {
    public static MHDFVerify instance;

    private final FileManager fileManager = new FileManager();
    private final Sha256Manager sha256Manager = new Sha256Manager();

    private final JavaPlugin plugin;
    private final String host;
    private final String user;
    private final String password;

    public MHDFVerify(JavaPlugin plugin, String host, String user, String password) {
        instance = this;
        this.plugin = plugin;
        this.host = host;
        this.user = user;
        this.password = password;

        File authFolder = new File(plugin.getDataFolder(), "auth");
        if (authFolder.exists()) {
            getFileManager().removeFiles(authFolder);
        }
        authFolder.mkdirs();

        String authFileName = UUID.randomUUID().toString().replace("-", "") + getLibraryFileNameSuffix();

        getFileManager().saveResource(authFolder, authFileName, getLibraryFileName(), true);
        File authFile = new File(authFolder, authFileName);

        System.load(authFile.getAbsolutePath());
    }

    /**
     * 检测验证
     */
    public boolean check() {
        Verify verify = new Verify(getHost(), getPlugin().getDescription().getName());
        String token = verify.startVerify(getUser(), getPassword());
        if (token == null) {
            return false;
        }

        new Thread(() -> {
            WebSocketClient client = new WebsocketClient(getHost(), token);
            client.connect();
        }).start();

        return true;
    }

    /**
     * 获取库文件名称
     *
     * @return 库文件名称
     */
    private String getLibraryFileName() {
        return "libmhdfverify" + getLibraryFileNameSuffix();
    }

    /**
     * 获取库文件后缀
     *
     * @return 库文件后缀
     */
    private String getLibraryFileNameSuffix() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return ".dll";
        } else {
            throw new RuntimeException("不支持的系统版本");
        }
    }
}