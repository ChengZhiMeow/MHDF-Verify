package cn.chengzhiya.mhdfverify.command;

import java.util.List;

@SuppressWarnings("unused")
public interface TabCompleter {
    List<String> onTabComplete(String command, String[] args);
}
