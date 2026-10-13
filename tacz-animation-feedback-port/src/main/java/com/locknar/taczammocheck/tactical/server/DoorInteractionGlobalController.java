package com.locknar.taczammocheck.tactical.server;

import com.locknar.taczammocheck.tactical.DoorInteractionGlobalState;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.fml.ModList;

public final class DoorInteractionGlobalController {
    private static final String COMMAND = "swat_shout_global";
    private static final String SHOUT_MOD_ID = "locknar_swat_shout";

    private DoorInteractionGlobalController() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (ModList.get().isLoaded(SHOUT_MOD_ID)) {
            return;
        }
        event.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)COMMAND).requires(source -> source.hasPermission(2))).executes(DoorInteractionGlobalController::toggle)).then(Commands.literal((String)"on").executes(ctx -> DoorInteractionGlobalController.set((CommandContext<CommandSourceStack>)ctx, true)))).then(Commands.literal((String)"off").executes(ctx -> DoorInteractionGlobalController.set((CommandContext<CommandSourceStack>)ctx, false)))).then(Commands.literal((String)"true").executes(ctx -> DoorInteractionGlobalController.set((CommandContext<CommandSourceStack>)ctx, true)))).then(Commands.literal((String)"false").executes(ctx -> DoorInteractionGlobalController.set((CommandContext<CommandSourceStack>)ctx, false)))).then(Commands.literal((String)"1").executes(ctx -> DoorInteractionGlobalController.set((CommandContext<CommandSourceStack>)ctx, true)))).then(Commands.literal((String)"0").executes(ctx -> DoorInteractionGlobalController.set((CommandContext<CommandSourceStack>)ctx, false))));
    }

    public static void onCommand(CommandEvent event) {
        if (!ModList.get().isLoaded(SHOUT_MOD_ID)) {
            return;
        }
        String raw = event.getParseResults().getReader().getString();
        if (raw == null) {
            return;
        }
        if ((raw = raw.trim()).startsWith("/")) {
            raw = raw.substring(1).trim();
        }
        if (raw.isEmpty()) {
            return;
        }
        String[] parts = raw.split("\\s+");
        if (parts.length == 0 || !COMMAND.equalsIgnoreCase(parts[0])) {
            return;
        }
        CommandSourceStack source = (CommandSourceStack)event.getParseResults().getContext().getSource();
        if (source == null || !source.hasPermission(2)) {
            return;
        }
        MinecraftServer server = source.getServer();
        if (server == null) {
            return;
        }
        if (parts.length == 1) {
            DoorInteractionGlobalState.toggle(server);
            return;
        }
        Boolean enabled = DoorInteractionGlobalController.parseBoolean(parts[1]);
        if (enabled != null) {
            DoorInteractionGlobalState.setServerEnabled(server, enabled);
        }
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            DoorInteractionGlobalState.syncTo(player2);
        }
    }

    private static int toggle(CommandContext<CommandSourceStack> context) {
        DoorInteractionGlobalState.toggle(((CommandSourceStack)context.getSource()).getServer());
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> context, boolean enabled) {
        DoorInteractionGlobalState.setServerEnabled(((CommandSourceStack)context.getSource()).getServer(), enabled);
        return 1;
    }

    private static Boolean parseBoolean(String value) {
        if (value == null) {
            return null;
        }
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "on", "true", "1", "enable", "enabled" -> Boolean.TRUE;
            case "off", "false", "0", "disable", "disabled" -> Boolean.FALSE;
            default -> null;
        };
    }
}
