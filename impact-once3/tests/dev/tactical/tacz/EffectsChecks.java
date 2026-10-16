package dev.tactical.tacz;
import java.util.*;import net.minecraft.core.BlockPos;import net.minecraft.client.Minecraft;import com.tacz.guns.client.particle.BulletHoleParticle;
public class EffectsChecks{
 static int checks;static void ok(boolean b,String why){checks++;if(!b)throw new AssertionError(why);}
 public record State(String id,int light){public boolean isAir(){return id.equals("air");}public State setValue(Object property,Integer level){return new State(id,level);}}
 public static class World{public long time;public int sparks,writes;public Map<Object,State> states=new HashMap<>();public long getGameTime(){return time;}public boolean hasChunkAt(Object p){return true;}public State getBlockState(Object p){return states.getOrDefault(p,new State("air",0));}public boolean setBlock(Object p,State s,int flags){states.put(p,s);writes++;return true;}public void addParticle(Object type,double x,double y,double z,double a,double b,double c){sparks++;}}
 public static class Face{public int getStepX(){return 1;}public int getStepY(){return 0;}public int getStepZ(){return 0;}public String toString(){return "east";}}
 public static class Player{public double getX(){return 0;}public double getEyeY(){return 1.62;}public double getZ(){return 0;}public double distanceToSqr(double x,double y,double z){return 0;}}
 public static class Engine{public List<Object> added=new ArrayList<>();public void add(Object p){added.add(p);}}
 public static void main(String[]args)throws Exception{boolean old=args.length>0&&args[0].equals("old");World w=new World();Minecraft mc=Minecraft.getInstance();mc.level=w;mc.player=new Player();mc.particleEngine=new Engine();BlockPos wall=new BlockPos(0,1,1);w.states.put(wall,new State("stone",0));Face face=new Face();
  BulletHoleParticle first=new BulletHoleParticle(w,1.1,1.2,1.3,face,wall,"tacz:9mm","tacz:gun","tacz:gun");ok(w.sparks==4,"real impact emits four native sparks");ok(w.writes==1,"real impact emits light pulse");ok(first.age==0&&first.lifetime==3600,"fresh mark retains initial glow and three-minute lifetime");w.time=5;ClientEffects.tick();ok(w.writes==2&&w.getBlockState(new BlockPos(1,1,1)).isAir(),"fresh impact light expires normally");first.remove();for(int i=1;i<=20;i++){w.time=5+i;ImpactMarks.tick();}
  BulletHoleParticle restored=(BulletHoleParticle)ImpactMarks.marks.values().iterator().next().particle;
  if(old){ok(w.sparks==8&&w.writes==3,"original .2 replays sparks and light when restoring decal");ok(restored.age==0,"original .2 replays fullbright by resetting age");System.out.println("Native visual bug reproduced: "+checks);return;}
  ok(w.sparks==4&&w.writes==2,"restoring a decal does not emit new sparks or light");ok(restored.age>=5&&restored.age==TimedMarks.clock,"restored age avoids fresh glow and preserves actual age");ok(restored.lifetime==3600&&restored.quadSize==.09f,"restored lifetime and ammo-category size preserved");
  for(int k=0;k<100;k++){restored.remove();for(int i=0;i<20;i++){w.time++;ImpactMarks.tick();ClientEffects.tick();}restored=(BulletHoleParticle)ImpactMarks.marks.values().iterator().next().particle;ok(w.sparks==4&&w.writes==2,"repeated restore stays visually inert");ok(restored.age>=5,"no repeated fullbright");}
  while(TimedMarks.clock<3600){w.time++;ImpactMarks.tick();}ok(ImpactMarks.marks.isEmpty()&&!restored.alive,"original three-minute deadline still removes mark");
  ImpactMarks.replay=false;BulletHoleParticle next=new BulletHoleParticle(w,1.1,1.2,1.3,face,wall,"tacz:9mm","tacz:gun","tacz:gun");ok(w.sparks==8&&w.writes==3&&next.age==0,"subsequent real impact still flashes and emits particles");
  System.out.println("Native effects and decal replay checks: "+checks);
 }
}
