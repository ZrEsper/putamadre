package dev.zomboid.hordes;
import java.util.*;import dev.zomboidmelee.combat.PanelCooldown;import dev.zomboid.hordes.IndoorGraph.Cell;import net.minecraft.core.BlockPos;import net.neoforged.fml.util.thread.EffectiveSide;import net.minecraft.client.Minecraft;
public class Checks{
 static int checks;static void check(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}static void eq(double a,double b,String s){check(Math.abs(a-b)<1e-6,s+" actual="+a);}
 public static class Item{public String getDescriptionId(){return "item.minecraft.iron_sword";}public Object getItem(){return this;}}
 public static class Tag{Map<String,Object> data=new HashMap<>();public void remove(String k){data.remove(k);}public void putBoolean(String k,boolean v){data.put(k,v);}public boolean getBoolean(String k){return Boolean.TRUE.equals(data.get(k));}}
 public static class Entity{
  double x,y,z;Tag tag=new Tag();Object target=new Object();public double getX(){return x;}public double getY(){return y;}public double getZ(){return z;}public float getYRot(){return 0;}public float getXRot(){return 0;}public void moveTo(double x,double y,double z,float yaw,float pitch){this.x=x;this.y=y;this.z=z;}public Tag getPersistentData(){return tag;}public void setTarget(Object o){target=o;}public Nav getNavigation(){return new Nav();}public boolean hasLineOfSight(Object p){return visible;}public boolean visible;public double getEyeY(){return y+1.6;}public Look getLookControl(){return new Look();}
 }
 public static class Look{public void setLookAt(double x,double y,double z){}}
 public static class Nav{public void stop(){}}
 public static class Player extends Entity{public boolean isAlive(){return true;}public boolean isCreative(){return false;}public boolean isSpectator(){return false;}public BlockPos blockPosition(){return new BlockPos((int)Math.floor(x),(int)y,(int)Math.floor(z));}public double distanceToSqr(double x,double y,double z){return Math.pow(this.x-x,2)+Math.pow(this.y-y,2)+Math.pow(this.z-z,2);}}
 public static class Block{String name;Block(String name){this.name=name;}public String toString(){return "Block{minecraft:"+name+"}";}}
 public static class State{String name;State(String n){name=n;}public boolean isAir(){return name.equals("air");}public Block getBlock(){return new Block(name);}public boolean isValidSpawn(Object w,Object p,Object t){return name.equals("stone");}}
 public static class Fluid{boolean empty;Fluid(boolean b){empty=b;}public boolean isEmpty(){return empty;}}
 public static class Border{public boolean isWithinBounds(Object p){return true;}}
 public static class World{
  public Random random=new Random(1);public Player player=new Player();public boolean loaded=true,collision=true,water=false,natural=false,openRoom=false;public long getGameTime(){return 100;}public Border getWorldBorder(){return new Border();}public boolean hasChunkAt(Object p){return loaded;}public Iterable<Player> players(){return List.of(player);}public boolean noCollision(Object e){return collision;}
  public State getBlockState(BlockPos p){boolean house=p.x()>=10&&p.x()<=26&&p.z()>=10&&p.z()<=20;if(p.y()==63)return new State("stone");if(!house)return new State("air");if(p.y()==67)return new State(natural?"stone":"oak_planks");if(p.y()>=64&&p.y()<=65){if(p.x()==10||p.x()==26||p.z()==10||p.z()==20)return new State("stone_bricks");if(p.x()==19&&!openRoom)return new State(p.z()==15?"oak_door":"stone_bricks");}return new State("air");}
  public Fluid getFluidState(BlockPos p){return new Fluid(!water);}public World(){player.moveTo(12.5,64,15.5,0,0);}
 }
 public static class Server {public int ticks=100;public final PlayerList list=new PlayerList();public int getTickCount(){return ticks;}public PlayerList getPlayerList(){return list;}}
 public static class PlayerList {public List<Object> players=new ArrayList<>(List.of(new Object(),new Object()));public List<Object> getPlayers(){return players;}}
 public static class Tick {public Server server=new Server();public Server getServer(){return server;}}
 static Entity mob(){Entity e=new Entity();e.moveTo(50.5,64,15.5,0,0);e.tag.data.put("zh_player","old ticket");e.tag.data.put("zh_until",999L);return e;}
 public static void main(String[]args){
  Map<String,Double> d=new HashMap<>(),min=new HashMap<>(),max=new HashMap<>(),v=WorldTuning.values;CombatPanel.install(d,min,max,v);check(d.size()==9,"nine new controls");eq(v.get(CombatPanel.ENABLE),0,"interior off by default");eq(v.get(CombatPanel.CHANCE),20,"probability default");
  check(!IndoorGraph.roll(false,100,0),"disabled no spawn");check(!IndoorGraph.roll(true,0,0),"zero probability");check(IndoorGraph.roll(true,100,.999),"100 percent");int hits=0;for(int i=0;i<1000;i++)if(IndoorGraph.roll(true,20,(i+.5)/1000))hits++;check(hits==200,"20 percent roll");
  World w=new World();IndoorSpawn.WorldGrid grid=new IndoorSpawn.WorldGrid(w);Cell player=new Cell(12,64,15);
  check(grid.inside(player),"roofed house detected");check(!grid.inside(new Cell(40,64,15)),"outdoors rejected");List<Cell> candidates=IndoorGraph.candidates(grid,player);check(!candidates.isEmpty(),"other room found through door");Set<Cell> room=IndoorGraph.room(grid,player,1600);for(Cell c:candidates)check(!room.contains(c)&&c.x()>19&&!grid.door(c),"only other room allowed");
  w.openRoom=true;check(IndoorGraph.candidates(new IndoorSpawn.WorldGrid(w),player).isEmpty(),"open plan is same room");w.openRoom=false;w.natural=true;check(!new IndoorSpawn.WorldGrid(w).inside(player),"natural cave rejected");w.natural=false;w.loaded=false;check(!new IndoorSpawn.WorldGrid(w).inside(player),"unloaded chunk rejected");w.loaded=true;w.water=true;check(!new IndoorSpawn.WorldGrid(w).inside(player),"water rejected");w.water=false;
  check(IndoorGraph.building(grid,player,2).isEmpty(),"truncated structure rejected");
  v.put(CombatPanel.ENABLE,1.);v.put(CombatPanel.CHANCE,100.);Entity m=mob();IndoorSpawn.relocate(w,m);check(m.x>19&&m.x<26,"new entity moved indoors");check(m.tag.getBoolean("zh_interior29"),"interior marker");check(m.target==null&&!m.tag.data.containsKey("zh_player")&&!m.tag.data.containsKey("zh_until"),"no forced player target or ticket noise");
  World blocked=new World();blocked.collision=false;Entity blockedMob=mob();IndoorSpawn.relocate(blocked,blockedMob);eq(blockedMob.x,50.5,"unsafe body restores outdoor spawn");
  World seen=new World();Entity visibleMob=mob();visibleMob.visible=true;IndoorSpawn.relocate(seen,visibleMob);eq(visibleMob.x,50.5,"visible spawn rejected and restored");
  v.put(CombatPanel.CHANCE,0.);Entity disabled=mob();IndoorSpawn.relocate(w,disabled);eq(disabled.x,50.5,"zero chance original position");
  eq(PanelCooldown.attack(20,false,new Item()),20,"default cooldown preserved");v.put("melee_normal",.5);v.put("melee_espadas",2.);eq(PanelCooldown.attack(20,false,new Item()),20,"normal and family combine");v.put("melee_pesado",2.);eq(PanelCooldown.attack(20,true,new Item()),80,"heavy independent");eq(PanelCooldown.shove(),15,"default shove");v.put("melee_empujon",2.);eq(PanelCooldown.shove(),30,"server shove configurable");
  eq(PanelCooldown.scaled(1,.25),1,"one tick minimum");eq(PanelCooldown.scaled(2000,4),1200,"safe upper bound");check(PanelCooldown.family(new Item()).equals("espadas"),"sword classification");
  String wire=CombatSync.encode(v);check(CombatSync.decode(wire).get("melee_pesado")==2.,"network round trip");check(!CombatSync.accept("{\"revision\":1}"),"admin snapshots keep original route");
  EffectiveSide.server=false;check(CombatSync.accept(wire),"combat snapshot consumed without admin screen");eq(CombatSync.multiplier("melee_pesado"),2,"client synchronized value");eq(PanelCooldown.shove(),30,"client shove matches server");Minecraft.INSTANCE.connection=new Object();eq(CombatSync.multiplier("melee_pesado"),1,"new connection drops stale settings");
  for(String bad:new String[]{"!melee29:NaN,1,1,1,1,1,1,","!melee29:1,1,","!melee29:0,1,1,1,1,1,1,"}){boolean rejected=false;try{CombatSync.decode(bad);}catch(IllegalArgumentException e){rejected=true;}check(rejected,"invalid network data rejected");}
  check(PanelHelp.loot("medicinas").contains("First Aid")&&PanelHelp.loot("medicinas").contains("kits medicos"),"medicine examples actual packs");check(PanelHelp.loot("herramientas").contains("antorchas"),"survival tools explained");
  EffectiveSide.server=true;Tick tick=new Tick();CombatSync.tick(tick);eq(net.neoforged.neoforge.network.PacketDistributor.recipients.size(),2,"all players receive login sync, not only admin");CombatSync.tick(tick);eq(net.neoforged.neoforge.network.PacketDistributor.recipients.size(),2,"unchanged settings are not resent");
  tick.server.list.players.add(new Object());tick.server.ticks=120;CombatSync.tick(tick);eq(net.neoforged.neoforge.network.PacketDistributor.recipients.size(),3,"newly joined player gets snapshot");WorldTuning.revision++;tick.server.ticks=140;CombatSync.tick(tick);eq(net.neoforged.neoforge.network.PacketDistributor.recipients.size(),6,"setting change broadcasts to everyone");CombatSync.reset();check(IndoorSpawn.cache.isEmpty()&&IndoorSpawn.attempts.isEmpty(),"world caches cleared");
  System.out.println("Passed "+checks+" behavior checks with world/client API doubles; Minecraft runtime untested.");
 }
}
