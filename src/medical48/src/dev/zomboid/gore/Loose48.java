package dev.zomboid.gore;
import java.util.*;
/** Runs on native physics tick, never mutates Jolt constraints on render/network threads. */
public final class Loose48 {
 static final Map<Object,Integer> DONE=new WeakHashMap<>();
 public static void tick(Object rag){if(R.yes(R.call(rag,"isDestroyed"))||R.yes(R.call(rag,"isReplicated")))return;int mask=Damage25.mask(Cuts24.field(rag));int before=DONE.getOrDefault(rag,0);int added=mask&~before;if(added==0)return;DONE.put(rag,mask|before);Object v=R.get(rag,"ragdollParts"),joints=R.get(rag,"ragdollJoints"),world=R.get(rag,"world");if(!(v instanceof List<?> parts)||!(joints instanceof List<?> list))return;Set<Object> removed=Collections.newSetFromMap(new IdentityHashMap<>());for(int i=0;i<Math.min(6,parts.size());i++)if((added&1<<i)!=0)removed.add(parts.get(i));boolean changed=false;for(Iterator<?> it=list.iterator();it.hasNext();){Object joint=it.next();if(removed.contains(R.call(joint,"bodyA"))||removed.contains(R.call(joint,"bodyB"))){it.remove();R.call(world,"destroyConstraint",joint);changed=true;}}if(changed){R.set(rag,"settled",false);R.set(rag,"settledTicks",0);if(R.yes(R.get(rag,"bodiesFrozen")))R.call(rag,"unfreezeBodies");R.call(rag,"markSettledPoseDirty");for(Object part:parts)R.call(part,"activate");}}
}
