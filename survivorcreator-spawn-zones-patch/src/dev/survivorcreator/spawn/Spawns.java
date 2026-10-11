package dev.survivorcreator.spawn;
import java.util.*;import java.io.*;import java.nio.charset.StandardCharsets;
import dev.survivorcreator.net.Net;import net.minecraft.resources.ResourceLocation;
public final class Spawns{
 static final Map<Object,int[]> pending=Collections.synchronizedMap(new WeakHashMap<>());
 public static final String PREFIX="survivorcreator:spawn_";
 static List<int[]> homes(int zone){List<int[]> out=new ArrayList<>();try(var in=Spawns.class.getResourceAsStream("/survivor-spawn-homes.csv")){if(in==null)throw new IllegalStateException("Missing home catalog");for(String line:new String(in.readAllBytes(),StandardCharsets.UTF_8).split("\\R")){if(line.isBlank())continue;String[] a=line.split(",");if(Integer.parseInt(a[0])!=zone)continue;int[] h=new int[6];for(int i=0;i<6;i++)h[i]=Integer.parseInt(a[i+1]);out.add(h);}}catch(IOException e){throw new IllegalStateException(e);}return out;}
 static Object pos(int x,int y,int z){return R.make("net.minecraft.core.BlockPos",x,y,z);}
 static boolean safe(Object level,int[] h){
  R.call(level,"getChunk",Math.floorDiv(h[0],16),Math.floorDiv(h[2],16));
  Object p=pos(h[0],h[1],h[2]),head=pos(h[0],h[1]+1,h[2]),floor=pos(h[0],h[1]-1,h[2]);
  Object foot=R.call(level,"getBlockState",p),above=R.call(level,"getBlockState",head),ground=R.call(level,"getBlockState",floor);
  if(!(Boolean)R.call(foot,"isAir")||!(Boolean)R.call(above,"isAir"))return false;
  if(!(Boolean)R.call(R.call(foot,"getFluidState"),"isEmpty")||!(Boolean)R.call(R.call(ground,"getFluidState"),"isEmpty"))return false;
  if((Boolean)R.call(R.call(ground,"getCollisionShape",level,floor),"isEmpty"))return false;
  Object box=R.make("net.minecraft.world.phys.AABB",h[0]-16.0,h[1]-8.0,h[2]-16.0,h[0]+16.0,h[1]+8.0,h[2]+16.0);
  List<?> mobs=(List<?>)R.call(level,"getEntitiesOfClass",R.type("net.minecraft.world.entity.monster.Monster"),box);
  return mobs.isEmpty();
 }
 public static Net.Confirm filter(Object player,Net.Confirm c){
  int zone=-1;List<ResourceLocation> perks=new ArrayList<>();
  for(ResourceLocation id:c.perks()){String s=id.toString();if(s.startsWith(PREFIX)){try{int n=Integer.parseInt(s.substring(PREFIX.length()));if(zone>=0||n<0||n>=Zones.NAMES.length)throw new IllegalArgumentException();zone=n;}catch(Exception e){reject(player,"Zona de aparición inválida.");return null;}}else perks.add(id);}
  pending.remove(player);if(zone<0)return c;
  Object character=R.call(player,"getData",R.field(R.type("dev.survivorcreator.SurvivorCreator"),"CHARACTER"));
  if((Boolean)R.call(character,"completed"))return new Net.Confirm(c.profession(),List.copyOf(perks));
  Object level=R.call(player,"serverLevel");
  if(!R.call(level,"dimension").equals(R.field(R.type("net.minecraft.world.level.Level"),"OVERWORLD"))){reject(player,"La selección de casas está preparada para Mosslorn en el Overworld.");return null;}
  List<int[]> candidates=homes(zone);Collections.shuffle(candidates);
  try{for(int[] h:candidates)if(safe(level,h)){pending.put(player,h);return new Net.Confirm(c.profession(),List.copyOf(perks));}}
  catch(Exception e){System.err.println("[SurvivorCreator] Safe spawn validation failed: "+e);}
  reject(player,"No hay una casa segura disponible en esa zona. Elige otra zona.");return null;
 }
 static void reject(Object p,String message){R.call(R.type("net.neoforged.neoforge.network.PacketDistributor"),"sendToPlayer",p,new Net.Result(false,"survivorcreator.spawn.error",message),new net.minecraft.network.protocol.common.custom.CustomPacketPayload[0]);}
 public static void place(Object player){
  int[] h=pending.remove(player);if(h==null)return;Object level=R.call(player,"serverLevel");
  // Recheck immediately before teleport; never replace unsafe terrain or delete entities.
  if(!safe(level,h)){R.call(player,"displayClientMessage",R.call(R.type("net.minecraft.network.chat.Component"),"literal","La casa dejó de estar segura; se conserva tu posición actual."),false);return;}
  try{R.call(player,"teleportTo",level,h[0]+.5,h[1]+.0,h[2]+.5,0f,0f);}catch(IllegalStateException e){R.call(player,"teleportTo",level,h[0]+.5,h[1]+.0,h[2]+.5,Set.of(),0f,0f);}
  R.call(R.type("dev.survivorcreator.GraceProtection"),"give",player);
  Flashlight.equip(player);
  // Preserve existing household container inventories. Starter provisions are personal, once per creation.
  Object items=R.type("net.minecraft.world.item.Items");for(String item:new String[]{"BREAD","APPLE","TORCH"}){Object stack=R.make("net.minecraft.world.item.ItemStack",R.field(items,item),item.equals("TORCH")?4:2);if(!(Boolean)R.call(R.call(player,"getInventory"),"add",stack))R.call(player,"drop",stack,false);}
 }
}
