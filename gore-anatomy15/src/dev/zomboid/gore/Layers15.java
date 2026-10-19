package dev.zomboid.gore;
/** Reuses existing synchronized three-bit stages; no new packet or world schema. */
public final class Layers15 {
 public static Damage13.Result hit(int state,int details,int surfaces,int region,int hits,int kind,double position){
  // A finite body can be fully consumed. No permanent indestructible bone tier.
  int stage=Math.min(7,Math.max(Trauma12.stage(details,region),Math.max(1,(hits+1)/2)));
  details=Trauma12.withStage(details,region,stage);
  state|=1<<(8+region);surfaces|=1<<(region+(kind==0?8:16));
  // Preserve existing dismemberment, but never create severance just for exposing muscle.
  return new Damage13.Result(state,details,surfaces);
 }
}
