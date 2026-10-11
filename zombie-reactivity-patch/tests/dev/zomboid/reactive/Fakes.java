package dev.zomboid.reactive;
import java.util.*;
public class Fakes {
 public static class Tag {Map<String,Object> data=new HashMap<>();public int getInt(String k){return ((Number)data.getOrDefault(k,0)).intValue();}public long getLong(String k){return ((Number)data.getOrDefault(k,0L)).longValue();}public double getDouble(String k){return ((Number)data.getOrDefault(k,0d)).doubleValue();}public void putInt(String k,int v){data.put(k,v);}public void putLong(String k,long v){data.put(k,v);}public void putDouble(String k,double v){data.put(k,v);}public void putUUID(String k,UUID v){data.put(k,v);}public void putBoolean(String k,boolean v){data.put(k,v);}public UUID getUUID(String k){return (UUID)data.get(k);}public boolean hasUUID(String k){return data.get(k) instanceof UUID;}public void remove(String k){data.remove(k);}public boolean contains(String k){return data.containsKey(k);}}
 public static class Level {public long now=100;public List<Object> players=new ArrayList<>();public List<Object> entities=new ArrayList<>();public long getGameTime(){return now;}public boolean isClientSide(){return false;}public List<Object> players(){return players;}public List<Object> getEntitiesOfClass(Class<?> type,net.minecraft.world.phys.AABB box){return entities;}}
 public static class Pos {final double x,y,z;public Pos(double x,double y,double z){this.x=x;this.y=y;this.z=z;}public double getX(){return x;}public double getY(){return y;}public double getZ(){return z;}public long asLong(){return ((long)x<<32)^((long)z&0xffffffffL);}}
 public static class Nav {public int stops;public void stop(){stops++;}}
 public static class Look {public void setLookAt(Object actor,float x,float z){}public void setLookAt(double x,double y,double z){}}
 public static class Abilities {public boolean flying;}
 public static class Character {public List<String> traits=new ArrayList<>();public List<String> perks(){return traits;}public Optional<Object> profession(){return Optional.empty();}}
 public static class Attribute {public double getBaseValue(){return .4;}}
 public static class Actor extends net.minecraft.world.entity.Mob {
  public Level level;public Tag tag=new Tag();public double x,y,z;public boolean los=true,crouch,sprint,water,ground=true;public UUID uuid=UUID.randomUUID();public Object target;public Character character=new Character();public Abilities abilities=new Abilities();public Nav nav=new Nav();public int tickCount;public Random random=new Random(42);public String message;
  public Actor(Level l){level=l;}public Level level(){return level;}public double getX(){return x;}public double getY(){return y;}public double getZ(){return z;}public float getYHeadRot(){return 0;}
  public boolean isAlive(){return true;}public boolean isCreative(){return false;}public boolean isSpectator(){return false;}public boolean isCrouching(){return crouch;}public boolean isSprinting(){return sprint;}public boolean isInWater(){return water;}public boolean isPassenger(){return false;}public boolean onGround(){return ground;}public Abilities getAbilities(){return abilities;}
  public UUID getUUID(){return uuid;}public Random getRandom(){return random;}public Tag getPersistentData(){return tag;}public int getId(){return 0;}public boolean hasLineOfSight(Object p){return los;}
  public Object getTarget(){return target;}public void setTarget(Object p){target=p;}public Attribute getAttribute(Object id){return new Attribute();}public Nav getNavigation(){return nav;}public Look getLookControl(){return new Look();}public Pos blockPosition(){return new Pos(x,y,z);}public Character getData(Object type){return character;}public void displayClientMessage(net.minecraft.network.chat.Component c,boolean overlay){message=c.text;}
 }
 public static class Zmob extends Actor {public Zmob(Level l){super(l);}}
 public static class Sound {String name;public Sound(String name){this.name=name;}public String getLocation(){return name;}}
 public static class Event {Object e;Event(Object e){this.e=e;}public Object getEntity(){return e;}}
}
