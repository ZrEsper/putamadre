package blackoutInteractive.ema_08_.birgadierWrapper;

import blackoutInteractive.ema_08_.birgadierWrapper.AbstractExtensibleCommandPart;
import blackoutInteractive.ema_08_.birgadierWrapper.CommandParts;
import blackoutInteractive.ema_08_.birgadierWrapper.EndPoint;
import blackoutInteractive.ema_08_.birgadierWrapper.SubCommand;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class CommandBuilder
extends AbstractExtensibleCommandPart {
    private final String name;
    private final CommandParts.PermissionChecker checker;

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuilder cmd) {
        LiteralArgumentBuilder root = (LiteralArgumentBuilder)Commands.literal((String)cmd.name).requires(source -> cmd.checker.canExecute((CommandSourceStack)source));
        for (CommandParts.CommandPart part : cmd.parts) {
            CommandBuilder.resolveAndAdd(root, part);
        }
        return root;
    }

    private static void resolveAndAdd(ArgumentBuilder<CommandSourceStack, ?> parent, CommandParts.CommandPart part) {
        if (part == null) {
            throw new RuntimeException("A null command part was encountered while building");
        }
        if (part instanceof EndPoint) {
            EndPoint ep = (EndPoint)part;
            if (ep.args.length == 0) {
                parent.executes(ctx -> ep.body.execute((CommandContext<CommandSourceStack>)ctx));
            } else {
                parent.then(CommandBuilder.resolve(ep.body, ep.args));
            }
        } else if (part instanceof SubCommand) {
            SubCommand sc = (SubCommand)part;
            LiteralArgumentBuilder sub = Commands.literal((String)sc.subName);
            for (CommandParts.CommandPart subPart : sc.parts) {
                CommandBuilder.resolveAndAdd(sub, subPart);
            }
            parent.then((ArgumentBuilder)sub);
        } else {
            throw new RuntimeException("Unrecognised command part of type " + part.getClass().getName());
        }
    }

    private static ArgumentBuilder<CommandSourceStack, ?> resolve(CommandParts.CommandBody body, CommandParts.CommandArgument[] args) {
        RequiredArgumentBuilder currentStackPiece = Commands.argument((String)args[args.length - 1].argName, args[args.length - 1].type);
        currentStackPiece.executes(ctx -> body.execute((CommandContext<CommandSourceStack>)ctx));
        for (int i = args.length - 2; i >= 0; --i) {
            RequiredArgumentBuilder newStackPiece = Commands.argument((String)args[i].argName, args[i].type);
            newStackPiece.then((ArgumentBuilder)currentStackPiece);
            currentStackPiece = newStackPiece;
        }
        return currentStackPiece;
    }

    public CommandBuilder(String name, CommandParts.PermissionChecker checker) {
        this.name = Objects.requireNonNull(name);
        this.checker = Objects.requireNonNull(checker);
    }
}

