package blackoutInteractive.ema_08_.birgadierWrapper;

import blackoutInteractive.ema_08_.birgadierWrapper.CommandParts;
import java.util.Objects;

final class EndPoint
implements CommandParts.ExtensionCommandPart {
    protected final CommandParts.CommandArgument[] args;
    protected final CommandParts.CommandBody body;

    protected EndPoint(CommandParts.CommandArgument[] args, CommandParts.CommandBody body) {
        this.args = EndPoint.requireNonNull(args);
        this.body = Objects.requireNonNull(body);
    }

    private static CommandParts.CommandArgument[] requireNonNull(CommandParts.CommandArgument[] args) {
        for (CommandParts.CommandArgument arg : Objects.requireNonNull(args)) {
            Objects.requireNonNull(arg);
        }
        return args;
    }
}

