package cn.chengzhiya.mhdfverify.command;

@SuppressWarnings("unused")
public interface CommandExecutor {
    void onCommand(String command, String[] args);
}
