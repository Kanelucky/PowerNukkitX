package cn.nukkit.command.tree;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.tree.node.CommandRouteNode;

/**
 * @author Kanelucky
 */
public class CommandRouteTree {

    private final CommandRouteNode root;

    public CommandRouteTree(String rootName) {
        this.root = new CommandRouteNode(rootName);
    }

    public CommandRouteNode getRoot() {
        return root;
    }

    public CommandResult dispatch(CommandSender sender, String[] args) {
        CommandRouteNode current = root;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            CommandRouteNode next = null;
            for (CommandRouteNode child : current.getChildren()) {
                if (child.getName().equalsIgnoreCase(arg)) {
                    next = child;
                    break;
                }
            }

            if (next == null) {
                break;
            }

            current = next;
        }

        if (current.isLeaf()) {
            return current.execute(sender, args);
        }

        sender.sendMessage("§cWrong command syntax");
        return CommandResult.INVALID_SYNTAX;
    }

}
