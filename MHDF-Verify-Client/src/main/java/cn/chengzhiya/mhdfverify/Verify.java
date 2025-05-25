package cn.chengzhiya.mhdfverify;

public final class Verify {
    private final String host;
    private final String plugin;

    public Verify(String host, String plugin) {
        this.host = host;
        this.plugin = plugin;
    }

    public String startVerify(String user, String password) {
        return this.check(host, user, MHDFVerify.instance.getSha256Manager().sha256(password), plugin);
    }

    public native String check(String host, String user, String password, String plugin);
}
