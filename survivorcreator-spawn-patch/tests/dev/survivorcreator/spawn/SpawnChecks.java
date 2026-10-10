package dev.survivorcreator.spawn;
import java.util.*;import net.minecraft.core.BlockPos;import net.minecraft.resources.ResourceLocation;import dev.survivorcreator.net.Net;import net.minecraft.world.level.Level;
public class SpawnChecks{
 public static class Character{public boolean completed(){return false;}}
 public static class Fluid{public boolean isEmpty(){return true;}}
 public static class Shape{boolean empty;Shape(boolean e){empty=e;}public boolean isEmpty(){return empty;}}
 public static class State{boolean air;State(boolean a){air=a;}public boolean isAir(){return air;}public Fluid getFluidState(){return new Fluid();}public Shape getCollisionShape(Object l,BlockPos p){return new Shape(air);}}
 public static class World{boolean unsafe;int y=66;public Object dimension(){return Level.OVERWORLD;}public void getChunk(int x,int z){}public State getBlockState(BlockPos p){return new State(p.y()>=y);}public List<?> getEntitiesOfClass(Class<?> c,Object box){return unsafe?List.of(new Object()):List.of();}}
 public static class Player{public World level=new World();public Character getData(Object key){return new Character();}public World serverLevel(){return level;}}
 static int cases;static void check(boolean b){cases++;if(!b)throw new AssertionError("case "+cases);}
 public static void main(String[] args){
  Player p=new Player();int[] h={-342,66,-284,-342,66,-282};check(Spawns.safe(p.level,h));p.level.unsafe=true;check(!Spawns.safe(p.level,h));p.level.unsafe=false;p.level.y=67;check(!Spawns.safe(p.level,h));
  for(int zone=0;zone<4;zone++)check(!Spawns.homes(zone).isEmpty());
  ResourceLocation medical=new ResourceLocation("survivorcreator:runner"),marker=new ResourceLocation("survivorcreator:spawn_0");
  p.level.y=66;Net.Confirm c=new Net.Confirm(Optional.empty(),List.of(medical,marker));Net.Confirm filtered=Spawns.filter(p,c);check(filtered!=null&&filtered.perks().equals(List.of(medical)));check(Spawns.pending.containsKey(p));
  check(Spawns.filter(p,new Net.Confirm(Optional.empty(),List.of(new ResourceLocation("survivorcreator:spawn_99"))))==null);
  check(Spawns.filter(p,new Net.Confirm(Optional.empty(),List.of(marker,marker)))==null);
  p.level.unsafe=true;check(Spawns.filter(p,c)==null);p.level.unsafe=false;
  Net.Confirm plain=new Net.Confirm(Optional.empty(),List.of(medical));check(Spawns.filter(p,plain)==plain);
  System.out.println("PASS "+cases+" actual helper checks: unsafe hostiles, blocked feet, home catalogs, marker stripping, invalid/duplicate zones and empty safe selection");
 }
}