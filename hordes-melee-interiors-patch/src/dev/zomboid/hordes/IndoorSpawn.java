package dev.zomboid.hordes;
import java.util.*;
import dev.zomboid.hordes.IndoorGraph.Cell;
/** Only relocates an unadded new entity, never creates an extra zombie. */
public final class IndoorSpawn {
 record Cached(java.lang.ref.WeakReference<Object> level,Cell origin,long until,List<Cell> candidates){}
 static final Map<Object,Long> attempts=new WeakHashMap<>();
 static final Map<Object,Cached> cache=new WeakHashMap<>();static final Map<Object,long[]> budgets=new WeakHashMap<>();static boolean warned;
 static Object c(Object o,String m,Object...a){return ZoneReflection.call(o,m,a);}
 static double n(Object o){return ((Number)o).doubleValue();}static boolean yes(Object o){return Boolean.TRUE.equals(o);}
 static Object pos(Cell p){try{return ZoneReflection.type("net.minecraft.core.BlockPos").getConstructor(int.class,int.class,int.class).newInstance(p.x(),p.y(),p.z());}catch(ReflectiveOperationException e){throw new IllegalStateException(e);}}
 static Cell cell(Object p){return new Cell((int)n(c(p,"getX")),(int)n(c(p,"getY")),(int)n(c(p,"getZ")));}
 static String id(Object state){return String.valueOf(c(c(state,"getBlock"),"toString")).toLowerCase(Locale.ROOT);}
 static boolean door(Object state){String s=id(state);return s.contains("door")&&!s.contains("trapdoor");}
 static boolean builtRoof(String id){return id.contains("planks")||id.contains("stairs")||id.contains("slab")||id.contains("bricks")||id.contains("terracotta")||id.contains("concrete")||id.contains("cobblestone")||id.contains("glass")||id.contains("copper")||id.contains("iron_block")||id.contains("quartz")||id.contains("wool");}
 static final class WorldGrid implements IndoorGraph.Grid{
  final Object world;final Map<Cell,Boolean> inside=new HashMap<>(),doors=new HashMap<>();WorldGrid(Object world){this.world=world;}
  public boolean door(Cell p){return doors.computeIfAbsent(p,k->IndoorSpawn.door(c(world,"getBlockState",pos(k))));}
  public boolean inside(Cell p){return inside.computeIfAbsent(p,k->check(k));}
  boolean check(Cell k){Object at=pos(k);if(!yes(c(world,"hasChunkAt",at))||!yes(c(c(world,"getWorldBorder"),"isWithinBounds",at)))return false;
   Object feet=c(world,"getBlockState",at),head=c(world,"getBlockState",pos(new Cell(k.x(),k.y()+1,k.z())));
   if((!yes(c(feet,"isAir"))&&!IndoorSpawn.door(feet))||(!yes(c(head,"isAir"))&&!IndoorSpawn.door(head)))return false;
   if(!yes(c(c(world,"getFluidState",at),"isEmpty"))||!yes(c(c(world,"getFluidState",pos(new Cell(k.x(),k.y()+1,k.z()))),"isEmpty")))return false;
   Object below=pos(new Cell(k.x(),k.y()-1,k.z())),floor=c(world,"getBlockState",below);
   if(!yes(c(floor,"isValidSpawn",world,below,ZoneReflection.get(ZoneReflection.type("net.minecraft.world.entity.EntityType"),"ZOMBIE")))||!yes(c(c(world,"getFluidState",below),"isEmpty")))return false;
   for(int y=k.y()+2;y<=k.y()+16;y++){Cell q=new Cell(k.x(),y,k.z());Object roof=c(world,"getBlockState",pos(q));if(!yes(c(roof,"isAir"))){return builtRoof(id(roof));}}
   return false;
  }
 }
 static List<Cell> candidates(Object world,Object player,long now){Cell start=cell(c(player,"blockPosition"));Cached old=cache.get(player);if(old!=null&&old.level.get()==world&&old.origin.equals(start)&&now<old.until)return old.candidates;
  long[] budget=budgets.computeIfAbsent(world,w->new long[]{-1,0});if(budget[0]!=now){budget[0]=now;budget[1]=0;}if(budget[1]>=1)return List.of();budget[1]++;
  List<Cell> cells=IndoorGraph.candidates(new WorldGrid(world),start);cache.put(player,new Cached(new java.lang.ref.WeakReference<>(world),start,now+40,cells));return cells;
 }
 public static void clear(){cache.clear();budgets.clear();attempts.clear();}
 public static Object[] before(Object[] args,Object world){if(args.length>0)relocate(world,args[0]);return args;}
 public static void relocate(Object world,Object mob){
  if(!WorldTuning.flag(CombatPanel.ENABLE)||WorldTuning.get(CombatPanel.CHANCE)<=0)return;
  double ox=n(c(mob,"getX")),oy=n(c(mob,"getY")),oz=n(c(mob,"getZ"));float yaw=((Number)c(mob,"getYRot")).floatValue(),pitch=((Number)c(mob,"getXRot")).floatValue();boolean moved=false,accepted=false;
  try{Object random=ZoneReflection.get(world,"random");if(!IndoorGraph.roll(true,WorldTuning.get(CombatPanel.CHANCE),n(c(random,"nextDouble"))))return;
   Object nearest=null;double best=96*96;List<Object> players=new ArrayList<>();for(Object player:(Iterable<?>)c(world,"players")){if(!yes(c(player,"isAlive"))||yes(c(player,"isSpectator"))||yes(c(player,"isCreative")))continue;players.add(player);double d=Math.pow(n(c(player,"getX"))-ox,2)+Math.pow(n(c(player,"getZ"))-oz,2);if(d<best){best=d;nearest=player;}}
   if(nearest==null)return;long now=((Number)c(world,"getGameTime")).longValue();if(attempts.getOrDefault(world,-1L)==now)return;attempts.put(world,now);List<Cell> options=new ArrayList<>(candidates(world,nearest,now));Collections.shuffle(options,new Random(((Number)c(random,"nextLong")).longValue()));
   WorldGrid grid=new WorldGrid(world);Set<Cell> forbidden=new HashSet<>();for(Object player:players)forbidden.addAll(IndoorGraph.room(grid,cell(c(player,"blockPosition")),1600));
   int tries=0;for(Cell p:options){if(++tries>24)break;if(forbidden.contains(p)||Math.floorDiv(p.x(),96)!=Math.floorDiv((int)Math.floor(ox),96)||Math.floorDiv(p.z(),96)!=Math.floorDiv((int)Math.floor(oz),96)||!grid.inside(p))continue;
    boolean close=false;for(Object player:players)if(n(c(player,"distanceToSqr",p.x()+.5,(double)p.y(),p.z()+.5))<36){close=true;break;}if(close)continue;
    c(mob,"moveTo",p.x()+.5,(double)p.y(),p.z()+.5,yaw,pitch);moved=true;
    if(!yes(c(world,"noCollision",mob)))continue;boolean seen=false;for(Object player:players)if(yes(c(mob,"hasLineOfSight",player))){seen=true;break;}if(seen)continue;
    Object tag=c(mob,"getPersistentData");for(String key:List.of("zh_player","zh_until","zi_seen","zi_seen_until","zi_pending","zi_notice_at","zi_anger_until","zi_angry","zpr_seen_player","zpr_seen_at","za_heard_until","zh_siege_target","zh_siege_seen"))c(tag,"remove",key);
    c(mob,"setTarget",(Object)null);c(c(mob,"getNavigation"),"stop");c(c(mob,"getLookControl"),"setLookAt",p.x()+.5-Math.sin(Math.toRadians(yaw))*4,n(c(mob,"getEyeY")),p.z()+.5+Math.cos(Math.toRadians(yaw))*4);c(tag,"putBoolean","zh_interior29",true);accepted=true;return;
   }
  }catch(RuntimeException e){if(!warned){warned=true;System.err.println("[Hordes interior] Exterior conservado: "+e);}}
  finally{if(moved&&!accepted)c(mob,"moveTo",ox,oy,oz,yaw,pitch);}
 }
}
