package dev.zomboid.taczinteract;
import java.lang.ref.WeakReference;

/** Only exempts valid USE interactions from the weapon mods' own input blockers. */
public final class Priority {
    static Object owner,world;
    static boolean claimed;
    static WeakReference<Object> handledUse=new WeakReference<>(null);
    static final String ISO="dev.zomboid.isotacz.Bridge";
    static Object call(Object o,String name,Object...args){return Reflect.call(o,name,args);}
    static Object get(Object o,String name){return Reflect.get(o,name);}
    static Class<?> type(String name){return Reflect.type(name);}
    static Class<?> optional(String name){try{return Class.forName(name);}catch(ClassNotFoundException missing){return null;}}
    static boolean yes(Object o){return Boolean.TRUE.equals(o);}
    static Object mc(){return call(type("net.minecraft.client.Minecraft"),"getInstance");}
    static void session(Object mc){Object player=get(mc,"player"),level=get(mc,"level");if(player!=owner||level!=world){owner=player;world=level;claimed=false;}}
    static boolean ready(Object mc){session(mc);return owner!=null&&world!=null&&get(mc,"gameMode")!=null&&get(mc,"screen")==null&&!yes(call(owner,"isSpectator"));}
    static boolean iso(){Class<?> mod=optional("com.cleannrooster.dungeons_iso.mod.Mod");return mod!=null&&yes(get(mod,"enabled"));}
    static boolean is(String name,Object object){Class<?> clazz=optional(name);return object!=null&&clazz!=null&&clazz.isInstance(object);}
    public static boolean bodyId(String id){return id.startsWith("doomsday_decoration:remains_")||id.equals("zombie_remains_loot:skeletal_remains");}
    static boolean block(Object mc,Object pos){
        Object state=call(world,"getBlockState",pos);if(yes(call(state,"isAir")))return false;
        Object block=call(state,"getBlock");Object registry=get(type("net.minecraft.core.registries.BuiltInRegistries"),"BLOCK");
        if(bodyId(call(registry,"getKey",block).toString()))return true;
        if(call(state,"getMenuProvider",world,pos)!=null)return true;
        Class<?> tags=type("net.minecraft.tags.BlockTags");
        for(String name:new String[]{"DOORS","TRAPDOORS","FENCE_GATES","BUTTONS","ALL_SIGNS","BEDS","CAMPFIRES","CAULDRONS"})if(yes(call(state,"is",get(tags,name))))return true;
        return is("net.minecraft.world.level.block.LeverBlock",block)||is("net.minecraft.world.level.block.BellBlock",block)||is("net.minecraft.world.MenuProvider",call(world,"getBlockEntity",pos));
    }
    static boolean entity(Object entity){return entity!=null&&(entity.getClass().getName().equals("de.maxhenkel.corpse.entities.CorpseEntity")||is("net.minecraft.world.MenuProvider",entity)||is("net.minecraft.world.Container",entity));}
    static boolean visible(Object player,Object hit,Object pos){
        Object eye=call(player,"getEyePosition"),point=call(hit,"getLocation");
        Object context=Reflect.make("net.minecraft.world.level.ClipContext",eye,point,get(type("net.minecraft.world.level.ClipContext$Block"),"COLLIDER"),get(type("net.minecraft.world.level.ClipContext$Fluid"),"NONE"),player);
        Object obstruction=call(world,"clip",context);
        if(!call(obstruction,"getType").toString().equals("BLOCK"))return true;
        if(pos!=null&&pos.equals(call(obstruction,"getBlockPos")))return true;
        return ((Number)call(eye,"distanceTo",call(obstruction,"getLocation"))).doubleValue()>=((Number)call(eye,"distanceTo",point)).doubleValue()-.05;
    }
    public static Object target(){
        Object mc=mc();if(!ready(mc))return null;
        if(iso()){
            Class<?> nativeActions=optional("dev.zomboid.isotacz.NativeActions");
            if(nativeActions!=null&&(yes(call(nativeActions,"riding",mc))||yes(call(nativeActions,"food",mc))))return null;
            Object hit=call(type(ISO),"cursorPick",mc);
            return hit!=null&&yes(call(type(ISO),"interactable",mc,hit))?hit:null;
        }
        Object hit=get(mc,"hitResult");if(hit==null)return null;
        String kind=call(hit,"getType").toString();Object pos=null;
        if(kind.equals("BLOCK")){
            pos=call(hit,"getBlockPos");if(!yes(call(owner,"canInteractWithBlock",pos,0d))||!block(mc,pos))return null;
        }else if(kind.equals("ENTITY")){
            Object entity=call(hit,"getEntity");if(!entity(entity)||!yes(call(owner,"canInteractWithEntity",entity,0d)))return null;
        }else return null;
        return visible(owner,hit,pos)?hit:null;
    }
    static void select(Object hit){Object mc=mc();Reflect.set(mc,"hitResult",hit);Reflect.set(mc,"crosshairPickEntity",call(hit,"getType").toString().equals("ENTITY")?call(hit,"getEntity"):null);}
    static void stopAim(){
        Object mc=mc(),player=get(mc,"player");if(player==null)return;
        if(call(type("com.tacz.guns.api.item.IGun"),"getIGunOrNull",call(player,"getMainHandItem"))!=null)call(call(type("com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator"),"fromLocalPlayer",player),"aim",false);
    }
    static boolean affected(){
        Object item=call(call(owner,"getMainHandItem"),"getItem");
        return is("com.tacz.guns.api.item.IGun",item)||is("me.xjqsh.lrtactical.api.item.ICustomItem",item)&&yes(call(item,"shouldBlockUse"));
    }
    static boolean protectedDoor(Object hit){
        if(!call(hit,"getType").toString().equals("BLOCK"))return false;
        Object state=call(world,"getBlockState",call(hit,"getBlockPos"));
        if(!yes(call(state,"is",get(type("net.minecraft.tags.BlockTags"),"DOORS"))))return false;
        Class<?> locks=optional("com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat");
        if(locks==null||!yes(call(locks,"isCurrentTargetLocked")))return false;
        return yes(call(locks,"isCurrentTargetRevealed"))||yes(call(locks,"requestCurrentDoorOpenAttempt"));
    }
    public static boolean allowUse(Object event){
        if(!yes(call(event,"isUseItem")))return false;
        if(handledUse.get()==event)return true;
        if(yes(call(event,"isCanceled"))||!ready(mc())||!affected())return false;
        Object hit=target();if(hit==null)return false;
        handledUse=new WeakReference<>(event);select(hit);claimed=true;stopAim();
        if(protectedDoor(hit)){call(event,"setSwingHand",false);call(event,"setCanceled",true);}
        return true;
    }
    public static boolean aimEvent(Object event){
        int button=((Number)call(event,"getButton")).intValue(),action=((Number)call(event,"getAction")).intValue();
        if(button!=1)return false;
        if(action==0){claimed=false;return false;}
        if(action!=1)return claimed;
        if(target()==null)return false;
        claimed=true;stopAim();return true;
    }
    public static boolean holdAim(){
        Object mc=mc();session(mc);
        Object key=get(type("com.tacz.guns.client.input.AimKey"),"AIM_KEY");
        if(!yes(call(key,"matchesMouse",1)))return false;
        if(!yes(call(key,"isDown"))){claimed=false;return false;}
        if(!ready(mc))return false;
        if(claimed||target()!=null){claimed=true;stopAim();return true;}return false;
    }
    public static boolean meleeEvent(Object event){
        if(((Number)call(event,"getButton")).intValue()!=1||((Number)call(event,"getAction")).intValue()!=1)return false;
        Class<?> attacks=optional("me.xjqsh.lrtactical.client.input.AttackKeys");if(attacks==null)return false;
        Object key=get(attacks,"SPECIAL_ATTACK");if(!yes(call(key,"matchesMouse",1)))return false;
        Object mc=mc();if(!ready(mc)||!(claimed||target()!=null))return false;
        while(yes(call(key,"consumeClick"))){} // Prevent a queued heavy attack after opening/closing.
        claimed=true;stopAim();return true;
    }
}
