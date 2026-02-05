package cn.nukkit.command.tree;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.tree.node.CommandNode;

import org.incendo.cloud.execution.CommandResult;

import java.util.List;

/**
 * @author Kanelucky
 */
public interface CommandTree {

    /**
     * Parse and execute the command tree.
     *
     * @param sender Command sender
     * @param args   Command arguments
     * @return result of execution
     */
    CommandResult parse(CommandSender sender, String[] args);

    /**
     * Root node of this command tree.
     */
    CommandNode getRoot();

    /**
     * All leaf nodes (executable endpoints).
     */
    List<CommandNode> getLeaves();

    interface Factory {
        CommandTree create(Command command);
    }
}
