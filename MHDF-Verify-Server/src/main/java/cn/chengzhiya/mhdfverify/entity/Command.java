package cn.chengzhiya.mhdfverify.entity;

import cn.chengzhiya.mhdfverify.command.CommandExecutor;
import cn.chengzhiya.mhdfverify.command.TabCompleter;
import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public final class Command {
    private final String command;
    private String description;
    private String usage;
    private CommandExecutor executor;
    private TabCompleter tabCompleter;

    public Command(String command) {
        this.command = command;
    }

    /**
     * 设置命令实例描述
     *
     * @param description 命令描述
     */
    public Command description(String description) {
        this.description = description;
        return this;
    }

    /**
     * 设置命令实例用法
     *
     * @param usage 命令用法
     */
    public Command usage(String usage) {
        this.usage = usage;
        return this;
    }

    /**
     * 设置命令实例命令执行实例
     *
     * @param executor 执行实例
     */
    public Command executor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    /**
     * 设置命令实例命令补全实例
     *
     * @param tabCompleter 补全实例
     */
    public Command completer(TabCompleter tabCompleter) {
        this.tabCompleter = tabCompleter;
        return this;
    }
}
