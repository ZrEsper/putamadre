package dev.zomboid.gore;
import net.minecraft.world.phys.Vec3;import com.raiiiden.ragdollified.network.*;import com.raiiiden.ragdollified.server.ServerRagdollSyncManager;
/** Executes the actual Bullet13.tick and production FastReflection against Vec3's double signature. */
public class BulletRegression46 {
 public static class Miss{public Object getType(){return "MISS";}}
 public static class World{public boolean isClientSide;public Object dimension(){return "test";}public Object clip(Object context){return new Miss();}}
 public static class Bullet{public boolean explosion,discarded;public int pierce=1;public World level(){return new World();}public Vec3 getDeltaMovement(){return new Vec3(0,0,4);}public Vec3 position(){return new Vec3(0,.95,0);}public float getDamage(Object point){return 8;}public void discard(){discarded=true;}}
 public static class Body {public int entityId(){return 7;}public boolean live(){return false;}public Object pose(){return null;}public Vec3 anchor(){return new Vec3(0,0,2);}}
 public static void main(String[]a){var packet=new RagdollSpawnPacket();packet.goreState12=Integer.MIN_VALUE;ServerRagdollSyncManager.RETAINED.put(7,new ServerRagdollSyncManager.Body(packet));ServerRagdollSyncManager.NEAR.add(new Body());var b=new Bullet();if(!Bullet13.tick(b)||!b.discarded||RagdollImpulsePacket.sent!=1)throw new AssertionError("corpse hit not completed");Vec3 impulse=(Vec3)RagdollImpulsePacket.impulse;if(impulse.z!=8||impulse.x!=0||impulse.y!=0)throw new AssertionError("impulse value or direction changed");System.out.println("ACTUAL_BULLET_TICK_REFLECTION_CORPSE_HIT=PASS");}
}
