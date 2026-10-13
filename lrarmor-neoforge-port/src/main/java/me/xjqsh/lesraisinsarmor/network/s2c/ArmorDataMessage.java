package me.xjqsh.lesraisinsarmor.network.s2c;
import java.util.*;
import me.xjqsh.lesraisinsarmor.resource.ArmorDataManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
public record ArmorDataMessage(Map<ResourceLocation,String> networkCache) implements CustomPacketPayload {
 public static final Type<ArmorDataMessage> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("lrarmor","armor_data"));
 public static final StreamCodec<FriendlyByteBuf,ArmorDataMessage> CODEC=StreamCodec.of(ArmorDataMessage::encode,ArmorDataMessage::decode);
 public ArmorDataMessage{networkCache=Map.copyOf(networkCache);}
 public static void encode(FriendlyByteBuf buf,ArmorDataMessage message){buf.writeVarInt(message.networkCache.size());message.networkCache.forEach((key,value)->{buf.writeResourceLocation(key);buf.writeUtf(value);});}
 public static ArmorDataMessage decode(FriendlyByteBuf buf){int count=buf.readVarInt();if(count<0||count>4096)throw new IllegalArgumentException("Armor sync count");Map<ResourceLocation,String> values=new HashMap<>();for(int i=0;i<count;i++)values.put(buf.readResourceLocation(),buf.readUtf());return new ArmorDataMessage(values);}
 @Override public Type<ArmorDataMessage> type(){return TYPE;}
 public static void onReceive(ArmorDataMessage message){ArmorDataManager.fromNetwork(message.networkCache);}
}
