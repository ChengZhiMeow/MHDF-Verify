package cn.chengzhiya.mhdfverify.manager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public final class FileManager {
    /**
     * 删除指定目录文件实例
     *
     * @param directory 目录文件实例
     */
    public void removeFiles(File directory) {
        if (!directory.exists()) {
            return;
        }

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                removeFiles(file);
                continue;
            }
            file.delete();
        }
        directory.delete();
    }

    /**
     * 保存资源
     *
     * @param filePath     保存目录
     * @param resourcePath 资源目录
     * @param replace      替换文件
     */
    public void saveResource(@NotNull File folder, @NotNull String filePath, @NotNull String resourcePath, boolean replace) throws RuntimeException {
        File file = new File(folder, filePath);
        if (file.exists() && !replace) {
            return;
        }

        URL url = FileManager.class.getClassLoader().getResource(resourcePath);
        if (url == null) {
            throw new RuntimeException("找不到资源: " + resourcePath);
        }

        URLConnection connection;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        connection.setUseCaches(false);

        try (InputStream in = url.openStream()) {
            try (FileOutputStream out = new FileOutputStream(file)) {
                if (in == null) {
                    throw new RuntimeException("读取资源 " + resourcePath + " 的时候发生了错误");
                }

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("无法保存资源", e);
        }
    }
}
