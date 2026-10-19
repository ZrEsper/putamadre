package dev.zomboid.gore;
import java.util.*;
/** Corpse owns inventory/lifetime. Native retained body owns physics under the SAME entity ID. */
public final class Linked45 {
 static final String MANAGER="com.raiiiden.ragdollified.server.ServerRagdollSyncManager",CLIENT="com.raiiiden.ragdollified.client.ClientRagdollManager",PACKET="com.raiiiden.ragdollified.network.RagdollSpawnPacket",KEY="linkedBody45";
 static final Map<Object,Object> SOURCES=Collections.synchronizedMap(new WeakHashMap<>());
 static final Map<Object,Boolean> READY=Collections.synchronizedMap(new WeakHashMap<>());
 static final Map<Object,Set<Integer>> DEAD_IDS=Collections.synchronizedMap(new WeakHashMap<>());
 static final ThreadLocal<Object> ACTIVE=new ThreadLocal<>();
 static Object c(Object o,String n,Object...a){return R.call(o,n,a);}
 static Object tag(Object o){return c(o,"getPersistentData");}
 static int id(Object o){return ((Number)c(o,"getId")).intValue();}
 static boolean client(Object o){return R.yes(R.get(c(o,"level"),"isClientSide"));}
 public static boolean supported(Object o){if(o==null)return false;for(Class<?> t=o.getClass();t!=null;t=t.getSuperclass())if(t.getName().equals("net.minecraft.world.entity.player.Player")||t.getName().equals("mod.cdv.tnpc.entity.AutoGunOperator"))return true;return false;}
 static boolean corpseAvailable(){try{Class.forName("de.maxhenkel.corpse.entities.CorpseEntity",false,Linked45.class.getClassLoader());return true;}catch(ClassNotFoundException e){return false;}}
 public static boolean skipDeath(Object o){return ACTIVE.get()==null&&supported(o)&&corpseAvailable();}
 public static boolean skipLocal(Object o){return supported(o)&&corpseAvailable()&&!R.yes(c(o,"isAlive"));}
 public static boolean damageable(Object packet){return ((Number)R.get(packet,"goreState12")).intValue()<0||R.get(packet,"medical45")instanceof String s&&!s.isEmpty();}
 public static void attach(Object corpse,Object source){if(corpse==null||!supported(source)||client(corpse)||Body48.duplicate(corpse,source))return;Body48.attached(corpse,source);c(tag(corpse),"putInt","linkedSource45",id(source));ACTIVE.set(corpse);try{Object sources=c(c(corpse,"level"),"damageSources");c(R.type("com.raiiiden.ragdollified.PhysicsHooks"),"spawnRagdoll",source,c(sources,"generic"));}finally{ACTIVE.remove();}}
 public static void register(Object source,Object packet){Object corpse=ACTIVE.get();if(corpse==null){c(R.type(MANAGER),"registerDeath",source,packet);return;}Trauma12.packet(source,packet);R.set(packet,"originalEntityId",id(corpse));
  int[] marks=new int[0];if(!Npc29.npc(source))try{marks=(int[])c(R.type("dev.zomboid.firstaid.Wounds45"),"snapshot",source);}catch(IllegalStateException e){if(!(e.getCause() instanceof ClassNotFoundException))throw e;}
  R.set(packet,"medical45",encode(Npc29.npc(source)&&R.yes(c(source,"isSlim"))?-id(source):id(source),marks));
  if(Npc29.npc(source)){R.set(packet,"modelType",R.get(R.type("com.raiiiden.ragdollified.MobModelHelper$ModelType"),"HUMANOID_STANDARD"));R.set(packet,"scale",R.yes(c(source,"isBaby"))?.5f:1f);R.set(packet,"goreTexture13",String.valueOf(c(source,"getPlayerSkin")));}
  c(R.type(MANAGER),"registerCorpse45",corpse,packet,Integer.MAX_VALUE,false);savePacket(corpse,packet);READY.put(corpse,true);
 }
 static String encode(int source,int[] a){StringBuilder s=new StringBuilder().append(source);for(int v:a)s.append(',').append(v);return s.toString();}
 public static int[] medical(Object packet){String s=(String)R.get(packet,"medical45");if(s==null||s.length()>32768)return new int[0];String[] p=s.split(",");if((p.length-1)%7!=0||p.length>1345)return new int[0];int[] a=new int[p.length-1];try{for(int i=1;i<p.length;i++)a[i-1]=Integer.parseInt(p[i]);}catch(NumberFormatException e){return new int[0];}return a;}
 static Object buffer(Object corpse,byte[] bytes){Object raw=bytes==null?c(R.type("io.netty.buffer.Unpooled"),"buffer"):c(R.type("io.netty.buffer.Unpooled"),"wrappedBuffer",bytes);return R.make("net.minecraft.network.RegistryFriendlyByteBuf",raw,c(corpse,"registryAccess"));}
 static void savePacket(Object corpse,Object packet){Object buf=buffer(corpse,null);try{c(R.type(PACKET),"encode",packet,buf);int n=((Number)c(buf,"readableBytes")).intValue();if(n>1048576)throw new IllegalStateException("corpse snapshot exceeds bound");byte[] bytes=new byte[n];c(buf,"getBytes",0,bytes);c(tag(corpse),"putByteArray",KEY,bytes);}finally{c(buf,"release");}}
 static Object loadPacket(Object corpse){byte[] bytes=(byte[])c(tag(corpse),"getByteArray",KEY);if(bytes.length==0||bytes.length>1048576)return null;Object b=buffer(corpse,bytes);try{Object p=c(R.type(PACKET),"decode",b);R.set(p,"originalEntityId",id(corpse));return p;}finally{c(b,"release");}}
 public static void tick(Object corpse){if(client(corpse))return;Object source=SOURCES.remove(corpse);if(source!=null){ACTIVE.set(corpse);try{Object sources=c(c(corpse,"level"),"damageSources");c(R.type("com.raiiiden.ragdollified.PhysicsHooks"),"spawnRagdoll",source,c(sources,"generic"));}finally{ACTIVE.remove();}return;}
  if(READY.containsKey(corpse)){if(((Number)R.get(corpse,"tickCount")).intValue()%100==0){Object retained=((Map<?,?>)R.get(R.type(MANAGER),"RETAINED")).get(id(corpse));if(retained!=null)savePacket(corpse,R.get(retained,"spawnPacket"));}return;}
  Object p=loadPacket(corpse);if(p!=null){c(R.type(MANAGER),"registerCorpse45",corpse,p,Integer.MAX_VALUE,false);READY.put(corpse,true);}
 }
 public static void save(Object corpse,Object nbt){if(!client(corpse)&&READY.containsKey(corpse)){Object r=((Map<?,?>)R.get(R.type(MANAGER),"RETAINED")).get(id(corpse));if(r!=null)savePacket(corpse,R.get(r,"spawnPacket"));}byte[] b=(byte[])c(tag(corpse),"getByteArray",KEY);if(b.length>0)c(nbt,"putByteArray",KEY,b);}
 public static void load(Object corpse,Object nbt){byte[] b=(byte[])c(nbt,"getByteArray",KEY);if(b.length<=1048576)c(tag(corpse),"putByteArray",KEY,b);READY.remove(corpse);}
 public static void removed(Object corpse){SOURCES.remove(corpse);READY.remove(corpse);if(client(corpse)){c(R.type(CLIENT),"setPersistent",id(corpse),false);c(R.type(CLIENT),"requestRemoveRagdoll",id(corpse));}else c(R.type(MANAGER),"remove",id(corpse));}
 public static boolean hide(Object corpse){if(!R.yes(c(R.type(CLIENT),"hasPendingOrActiveRagdoll",id(corpse))))return false;c(R.type(CLIENT),"setPersistent",id(corpse),true);return true;}
 public static boolean linked(Object corpse){return client(corpse)?R.yes(c(R.type(CLIENT),"hasPendingOrActiveRagdoll",id(corpse))):((byte[])c(tag(corpse),"getByteArray",KEY)).length>0;}
 public static Object source(Object original,Object packet){String s=(String)R.get(packet,"medical45");if(s==null||s.isEmpty())return original;int n;try{n=Math.abs(Integer.parseInt(s.split(",",2)[0]));}catch(NumberFormatException e){return original;}Object mc=c(R.type("net.minecraft.client.Minecraft"),"getInstance"),world=R.get(mc,"level");Object live=c(world,"getEntity",n);if(live!=null&&supported(live)&&!R.yes(c(live,"isAlive"))){Set<Integer>ids=DEAD_IDS.computeIfAbsent(world,w->new HashSet<>());if(ids.size()>1024)ids.clear();ids.add(n);c(R.type(CLIENT),"setEntityHidden",n,true);return live;}return null;}
 public static Object model(Object original,Object rag){Object text=R.get(rag,"medical45");if(!R.yes(c(rag,"isPlayer"))&&text instanceof String s&&!s.isEmpty()){Class<?>renderer=R.type("com.raiiiden.ragdollified.client.ClientRagdollRenderer");c(renderer,"initModels");return R.get(renderer,s.startsWith("-")?"slimModel":"normalModel");}return original;}
 public static void unhideAlive(int id){Object mc=c(R.type("net.minecraft.client.Minecraft"),"getInstance"),world=R.get(mc,"level");if(world==null)return;Set<Integer>ids=DEAD_IDS.get(world);if(ids==null||!ids.contains(id))return;Object entity=c(world,"getEntity",id);if(entity!=null&&supported(entity)&&!Npc29.npc(entity)&&R.yes(c(entity,"isAlive"))&&!R.yes(c(entity,"isDeadOrDying"))){ids.remove(id);c(R.type(CLIENT),"setEntityHidden",id,false);}}
 public static void clientCreated(Object rag){Body48.client(rag);Object t=R.get(rag,"medical45");if(t instanceof String s&&!s.isEmpty()){c(rag,"setPersistent",true);c(R.type(CLIENT),"setPersistent",((Number)c(rag,"getOriginalEntityId")).intValue(),true);}}
 public static void snapshot(Object from,Object to){R.set(to,"medical45",R.get(from,"medical45"));}
 public static void writeMedical(Object packet,Object buf){String s=(String)R.get(packet,"medical45");c(buf,"writeUtf",s==null?"":s,32768);}
 public static void readMedical(Object packet,Object buf){R.set(packet,"medical45",((Number)c(buf,"readableBytes")).intValue()==0?"":c(buf,"readUtf",32768));}
}
