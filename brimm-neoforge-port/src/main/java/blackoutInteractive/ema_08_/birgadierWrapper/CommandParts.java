package blackoutInteractive.ema_08_.birgadierWrapper;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;

public final class CommandParts {
    private CommandParts() {
    }

    @FunctionalInterface
    public static interface PermissionChecker {
        public boolean canExecute(CommandSourceStack var1);
    }

    @FunctionalInterface
    public static interface CommandBody {
        public int execute(CommandContext<CommandSourceStack> var1);
    }

    public static final class CommandArgument {
        public final String argName;
        public final ArgumentType<?> type;

        public static CommandArgument newArg(String argName, ArgumentType<?> type) {
            return new CommandArgument(Objects.requireNonNull(argName), Objects.requireNonNull(type));
        }

        private CommandArgument(String argName, ArgumentType<?> type) {
            this.argName = argName;
            this.type = type;
        }

        public static CommandArgument[] group(CommandArgument ... args) {
            return args;
        }

        public static CommandArgument[] empty() {
            return new CommandArgument[0];
        }
    }

    public static interface ExtensibleCommandPart
    extends CommandPart {
        public void addEndPoint(CommandArgument[] var1, CommandBody var2);

        public ExtensibleCommandPart addSubCommand(String var1);
    }

    public static interface ExtensionCommandPart
    extends CommandPart {
    }

    public static interface CommandPart {
    }
}

