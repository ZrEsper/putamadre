package dev.zomboid.clans;
import java.util.*;import java.io.*;
/** Stored in the real faction manager without creating fake human members. */
public final class Companions16 {
 static final Map<Object,Properties> CACHE=new WeakHashMap<>();
 static Object manager(Object server){return R.call(R.type("me.danialisntcool.factionfriction.data.FactionManager"),"get",server);}
 static Properties data(Object m){return CACHE.computeIfAbsent(m,k->{Properties p=new Properties();String raw=(String)R.get(k,"npcSocial17");if(raw!=null)try{p.load(new StringReader(raw));}catch(IOException e){throw new IllegalStateException(e);}return p;});}
 static void dirty(Object m){R.call(m,"setDirty");}
 public static UUID identity(Object p){return (UUID)R.call(R.type("me.danialisntcool.factionfriction.compat.switchy.SwitchyIdentity"),"id",p);}
 public static Object playerFaction(Object server,Object p){return R.call(manager(server),"getFactionByPlayer",identity(p));}
 public static int reputation(Object server,UUID player){return Integer.parseInt(data(manager(server)).getProperty("rep."+player,"0"));}
 public static int change(Object server,UUID player,int delta){Object m=manager(server);int value=Math.max(-100,Math.min(100,reputation(server,player)+delta));data(m).setProperty("rep."+player,""+value);dirty(m);return value;}
 public static String faction(Object server,UUID npc){Object m=manager(server);String id=data(m).getProperty("npc."+npc,"");return id.isEmpty()||R.call(m,"getFactionById",UUID.fromString(id))==null?"":id;}
 public static boolean member(Object server,Object player,UUID npc){Object f=playerFaction(server,player);return f!=null&&String.valueOf(R.call(f,"getId")).equals(faction(server,npc));}
 public static String enroll(Object server,UUID npc,Object player,String name){Object m=manager(server),f=playerFaction(server,player);if(f==null||!faction(server,npc).isEmpty())return"";String id=R.call(f,"getId").toString();Properties p=data(m);p.setProperty("npc."+npc,id);p.setProperty("name."+npc,name.replace('\n',' '));dirty(m);return id;}
 public static void remove(Object server,UUID npc){Object m=manager(server);Properties p=data(m);p.remove("npc."+npc);p.remove("name."+npc);dirty(m);}
 public static void save(Object m,Object tag){Properties p=data(m);try{StringWriter out=new StringWriter();p.store(out,"NPC relationships and companion roster");R.call(tag,"putString","TNPCSocial17",out.toString());}catch(IOException e){throw new IllegalStateException(e);}}
 public static void load(Object m,Object tag){R.set(m,"npcSocial17",R.call(tag,"getString","TNPCSocial17"));CACHE.remove(m);}
}
