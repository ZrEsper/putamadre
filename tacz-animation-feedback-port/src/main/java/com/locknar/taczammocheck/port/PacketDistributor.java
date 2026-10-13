package com.locknar.taczammocheck.port;
import java.util.function.Supplier;
public final class PacketDistributor {
 public static final Destination PLAYER=new Destination(0,null), TRACKING_ENTITY_AND_SELF=new Destination(1,null),TRACKING_ENTITY=new Destination(2,null),ALL=new Destination(3,null);
 public record Destination(int kind,Object target){public Destination with(Supplier<?> target){return new Destination(kind,target.get());} public Destination noArg(){return this;}}
}
