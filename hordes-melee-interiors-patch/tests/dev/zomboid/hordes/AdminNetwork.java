package dev.zomboid.hordes;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
public class AdminNetwork{public record View(String json)implements CustomPacketPayload{}}
