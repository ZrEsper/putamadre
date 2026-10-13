package blackoutInteractive.brimmArmors.server.commands;

import blackoutInteractive.brimmArmors.BrimmArmors;
import blackoutInteractive.brimmArmors.common.items.BrimmArmor;
import blackoutInteractive.brimmArmors.common.packets.PatchesAdjusterDebugPacket;
import blackoutInteractive.brimmArmors.common.registries.ItemRegistry;
import blackoutInteractive.ema_08_.birgadierWrapper.CommandBuilder;
import blackoutInteractive.ema_08_.birgadierWrapper.CommandParts;
import blackoutInteractive.ema_08_.birgadierWrapper.IRegistrableCommand;
import blackoutInteractive.ema_08_.rendering.overlay.OverlayPos;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class BrimmDebugCommand
implements IRegistrableCommand {
    public static final String LITERAL = "brimmDebug";
    private static final CommandParts.PermissionChecker permChecker = src -> src.hasPermission(2);

    @Override
    public CommandBuilder getBuilt() {
        CommandBuilder builder = new CommandBuilder(LITERAL, permChecker);
        CommandParts.ExtensibleCommandPart patchesAdjusterSub = builder.addSubCommand("patchesAdjuster");
        patchesAdjusterSub.addEndPoint(CommandParts.CommandArgument.group(CommandParts.CommandArgument.newArg("armorItem", StringArgumentType.word()), CommandParts.CommandArgument.newArg("overlayPos", StringArgumentType.word())), ctx -> {
            if (((CommandSourceStack)ctx.getSource()).isPlayer()) {
                OverlayPos op;
                String armorItemName = (String)ctx.getArgument("armorItem", String.class);
                String overlayPos = (String)ctx.getArgument("overlayPos", String.class);
                try {
                    op = OverlayPos.valueOf(overlayPos);
                }
                catch (IllegalArgumentException e) {
                    ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.literal((String)("Invalid overlay position " + overlayPos + " was specified: only acceptable positions are: " + Arrays.stream(OverlayPos.values()).map(Enum::name).collect(Collectors.joining(", ")) + ".")));
                    return 0;
                }
                Item armor = ItemRegistry.get(armorItemName).orElse(null);
                if (armor instanceof BrimmArmor) {
                    ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)("Initialising patch adjuster for " + armorItemName + " armor.")), false);
                    BrimmArmors.network.sendTo(new PatchesAdjusterDebugPacket(armorItemName, op), ((CommandSourceStack)ctx.getSource()).getPlayer());
                } else {
                    ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.literal((String)(armor == null ? "No Item named " + armorItemName + " could be found." : "Item named " + armorItemName + " is not an armor.")));
                }
            } else {
                ((CommandSourceStack)ctx.getSource()).sendFailure((Component)Component.literal((String)"Only players can execute this command."));
            }
            return 1;
        });
        return builder;
    }
}

