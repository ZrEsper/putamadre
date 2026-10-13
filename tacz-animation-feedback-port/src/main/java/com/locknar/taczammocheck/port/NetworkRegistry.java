package com.locknar.taczammocheck.port;
import java.util.function.*;
import net.minecraft.resources.ResourceLocation;
public final class NetworkRegistry {
 public static class ChannelBuilder {
  ResourceLocation name;
  public static ChannelBuilder named(ResourceLocation name){var b=new ChannelBuilder();b.name=name;return b;}
  public ChannelBuilder networkProtocolVersion(Supplier<String> s){return this;}
  public ChannelBuilder clientAcceptedVersions(Predicate<String> p){return this;}
  public ChannelBuilder serverAcceptedVersions(Predicate<String> p){return this;}
  public SimpleChannel simpleChannel(){return new SimpleChannel(name);}
 }
}
