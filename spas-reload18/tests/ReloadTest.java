import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;
import dev.zomboid.tacz.Reload18;
import java.nio.file.*;

public class ReloadTest {
 static int checks;
 static void check(boolean b,String why){checks++;if(!b)throw new AssertionError(why);}
 public static class API {
  private String gunId="tacz:spas_12";
  public LuaValue cache=LuaValue.NIL;
  public int inventory=64,magazine=0,barrel=0,fireMode=0,state=1,needed=7;
  public long time;
  public LuaTable params;
  public API(LuaTable params){this.params=params;}
  public LuaValue getCachedScriptData(){return cache;}
  public void cacheScriptData(LuaValue v){cache=v;}
  public LuaTable getScriptParams(){return params;}
  public int getNeededAmmoAmount(){return needed;}
  public int getReloadStateType(){return state;}
  public long getReloadTime(){return time;}
  public int getFireMode(){return fireMode;}
  public boolean hasAmmoToConsume(){return inventory>0;}
  public void consumeAmmoFromPlayer(int n){if(inventory<n)throw new AssertionError("ammo overdraft");inventory-=n;}
  public void putAmmoInMagazine(int n){magazine+=n;}
  public void setAmmoInBarrel(boolean b){barrel=b?1:0;}
  public String snapshot(){return inventory+":"+magazine+":"+barrel+":"+cache.get("reloaded_count")+":"+cache.get("interrupted_time");}
 }
 static Globals globals(){Globals g=JsePlatform.standardGlobals();g.set("NOT_RELOADING",0);g.set("EMPTY_RELOAD_FEEDING",1);g.set("EMPTY_RELOAD_FINISHING",2);g.set("TACTICAL_RELOAD_FEEDING",3);g.set("TACTICAL_RELOAD_FINISHING",4);g.set("BURST",2);return g;}
 static LuaTable params(){LuaTable t=new LuaTable();String[]keys={"intro_empty","intro_empty_semi","intro","loop","ending","intro_empty_feed","intro_empty_feed_semi","loop_feed"};double[]v={2.43,1.9,.6,.65,.6,1.63,1.02,.45};for(int i=0;i<keys.length;i++)t.set(keys[i],LuaValue.valueOf(v[i]));return t;}
 static LuaValue ud(API api){return CoerceJavaToLua.coerce(api);}
 static Varargs tick(LuaValue script,API api,boolean guard){return guard?Reload18.invoke(script.get("tick_reload"),ud(api)):script.get("tick_reload").invoke(ud(api));}
 static LuaValue good(){LuaTable c=new LuaTable();c.set("reloaded_count",0);c.set("needed_count",7);c.set("interrupted_time",-1);c.set("is_tactical",LuaValue.FALSE);return c;}
 public static void main(String[]args)throws Exception{
  LuaValue original=globals().load(Files.readString(Path.of(args[0])),"tacz_spas_12_gun_logic").call();
  LuaValue fixed=globals().load(Files.readString(Path.of(args[1])),"tacz_spas_12_gun_logic").call();
  API broken=new API(params());boolean reproduced=false;
  try{tick(original,broken,false);}catch(LuaError e){reproduced=e.getMessage().contains("interrupted_time");}
  check(reproduced,"Original crash must reproduce with real bundled LuaJ");
  LuaValue[]bad={LuaValue.NIL,LuaValue.valueOf(2),new LuaTable(),LuaValue.valueOf("wrong")};
  for(LuaValue badCache:bad)for(LuaValue script:new LuaValue[]{original,fixed}){
   API a=new API(params());a.cache=badCache;a.time=999999;
   Varargs result=tick(script,a,true);check(result.arg1().toint()==0&&result.arg(2).toint()==-1,"orphan reload must cancel");check(a.inventory==64&&a.magazine==0&&a.barrel==0,"no synthesized ammunition");
   check(script.get("start_reload").call(ud(a)).toboolean(),"retry initializes cache");a.time=0;check(tick(script,a,true).arg1().toint()==1,"retry starts normally");
  }
  for(String field:new String[]{"reloaded_count","needed_count","is_tactical","interrupted_time"}){
   API a=new API(params());a.cache=good();a.cache.set(field,LuaValue.NIL);check(tick(original,a,true).arg1().toint()==0,"missing "+field);
  }
  for(double invalid:new double[]{Double.NaN,Double.POSITIVE_INFINITY,-5}){LuaValue c=good();c.set("reloaded_count",LuaValue.valueOf(invalid));check(!Reload18.valid(c),"invalid numeric cache");}
  // Normal reloads must match the unmodified .17 script exactly at every tick.
  for(int mode:new int[]{0,2})for(int state:new int[]{1,3})for(int needed:new int[]{0,1,7,12})for(int interrupt:new int[]{-1,1200,3300}){
   API a=new API(params()),b=new API(params());a.fireMode=b.fireMode=mode;a.state=b.state=state;a.needed=b.needed=needed;
   original.get("start_reload").call(ud(a));fixed.get("start_reload").call(ud(b));
   for(int time=0;time<=12000;time+=50){
    a.time=b.time=time;
    if(time==interrupt){original.get("interrupt_reload").call(ud(a));fixed.get("interrupt_reload").call(ud(b));}
    Varargs x=tick(original,a,false),y=tick(fixed,b,true);
    check(x.arg1().equals(y.arg1())&&x.arg(2).equals(y.arg(2)),"valid state/countdown differ");check(a.snapshot().equals(b.snapshot()),"valid ammunition/cache differ");
    if(x.arg1().toint()==0)break;
   }
  }
  // The native hook protects already extracted original scripts; direct fixed resource also cancels nil.
  check(tick(fixed,new API(params()),false).arg1().toint()==0,"fixed resource guard");
  API other=new API(params());other.gunId="tacz:ak47";
  LuaValue probe=globals().load("return function(api) return 3, 42 end").call();
  Varargs result=Reload18.invoke(probe,ud(other));check(result.arg1().toint()==3&&result.arg(2).toint()==42,"unrelated guns unaffected");
  API valid=new API(params());valid.cache=good();boolean propagated=false;
  LuaValue fault=globals().load("return function(api) error('distinct unrelated failure') end").call();
  try{Reload18.invoke(fault,ud(valid));}catch(LuaError e){propagated=e.getMessage().contains("distinct unrelated failure");}check(propagated,"do not swallow other script errors");
  System.out.println(checks+" checks passed using bundled LuaJ: original crash reproduced, orphan cancellation, restart, differential reload and interruption");
 }
}
