package cn.nukkit.command.tree.node;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.tree.CommandResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Kanelucky
 */
public class CommandRouteNode {

    private final String name;
    private final List<CommandRouteNode> children = new ArrayList<>();
    private BiFunction<CommandSender, String[], CommandResult> executor;

    public CommandRouteNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<CommandRouteNode> getChildren() {
        return children;
    }

    public CommandRouteNode then(CommandRouteNode child) {
        children.add(child);
        return this;
    }

    public CommandRouteNode executes(
            BiFunction<CommandSender, String[], CommandResult> executor
    ) {
        this.executor = executor;
        return this;
    }

    public boolean isLeaf() {
        return executor != null;
    }

    public CommandResult execute(CommandSender sender, String[] args) {
        if (executor == null) {
            return CommandResult.INVALID_SYNTAX;
        }
        return executor.apply(sender, args);
    }
}
