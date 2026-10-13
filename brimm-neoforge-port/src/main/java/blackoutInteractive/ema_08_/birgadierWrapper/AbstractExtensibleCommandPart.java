package blackoutInteractive.ema_08_.birgadierWrapper;

import blackoutInteractive.ema_08_.birgadierWrapper.CommandParts;
import blackoutInteractive.ema_08_.birgadierWrapper.EndPoint;
import blackoutInteractive.ema_08_.birgadierWrapper.SubCommand;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractExtensibleCommandPart
implements CommandParts.ExtensibleCommandPart {
    protected final List<CommandParts.CommandPart> parts = new ArrayList<CommandParts.CommandPart>();

    AbstractExtensibleCommandPart() {
    }

    @Override
    public void addEndPoint(CommandParts.CommandArgument[] args, CommandParts.CommandBody body) {
        this.parts.add(new EndPoint(args, body));
    }

    @Override
    public CommandParts.ExtensibleCommandPart addSubCommand(String subName) {
        SubCommand subCommand = new SubCommand(subName);
        this.parts.add(subCommand);
        return subCommand;
    }
}

