package net.neoforged.neoforge.network;import java.util.*;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
public class PacketDistributor{public static final List<Object> recipients=new ArrayList<>();public static void sendToPlayer(Object player,CustomPacketPayload payload,CustomPacketPayload...extra){recipients.add(player);}}
