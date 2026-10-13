package dev.zomboid.taczinteract;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.*;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.client.event.*;
import com.tacz.guns.client.input.AimKey;
import com.tacz.guns.client.event.ClientPreventGunClick;
import me.xjqsh.lrtactical.client.ClientEventsHandler;
import me.xjqsh.lrtactical.client.input.AttackKeys;

public class Checks {
    static int checks;
    static void check(boolean test){if(!test)throw new AssertionError("Interaction check "+checks);checks++;}
    public static class Gun extends Item implements com.tacz.guns.api.item.IGun{}
    public static class Melee extends Item implements me.xjqsh.lrtactical.api.item.ICustomItem{public boolean shouldBlockAttack(){return true;}public boolean shouldBlockUse(){return true;}}
    public static class MenuEntity extends net.minecraft.world.entity.Entity implements net.minecraft.world.MenuProvider{}
    public static class State {
        public final String id;public final Set<Object> tags=new HashSet<>();public boolean menu;
        public State(String id){this.id=id;}public boolean isAir(){return id.equals("minecraft:air");}public String getBlock(){return id;}public Object getMenuProvider(Object world,Object pos){return menu?new Object():null;}public boolean is(Object tag){return tags.contains(tag);}
    }
    static final Minecraft mc=Minecraft.M;
    static void reset(Item item){
        mc.screen=null;mc.player=new LocalPlayer();mc.player.main=new ItemStack(item);mc.level=new net.minecraft.client.multiplayer.ClientLevel();mc.gameMode=new net.minecraft.client.multiplayer.MultiPlayerGameMode();mc.hitResult=new HitResult(HitResult.Type.MISS,new Vec3(3,0,0));mc.crosshairPickEntity=null;
        com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.locked=false;com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.revealed=false;com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.handled=true;com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.requests=0;Priority.claimed=false;Priority.owner=null;Priority.world=null;AimKey.AIM_KEY.down=false;AimKey.AIM_KEY.mouse=1;AttackKeys.SPECIAL_ATTACK.mouse=1;AttackKeys.SPECIAL_ATTACK.clicks=0;com.cleannrooster.dungeons_iso.mod.Mod.enabled=false;dev.zomboid.isotacz.NativeActions.mounted=false;dev.zomboid.isotacz.NativeActions.consuming=false;
    }
    static void block(String id,String tag,boolean menu){State state=new State(id);if(tag!=null)state.tags.add(tag);state.menu=menu;mc.level.state=state;mc.hitResult=new BlockHitResult(new BlockPos(3,0,0),3);}
    static InputEvent.InteractionKeyMappingTriggered click(boolean use){var e=new InputEvent.InteractionKeyMappingTriggered(use);ClientPreventGunClick.onClickInput(e);ClientEventsHandler.onClickInput(e);return e;}
    public static void main(String[]args){
        check(Priority.bodyId("doomsday_decoration:remains_1"));check(Priority.bodyId("zombie_remains_loot:skeletal_remains"));check(!Priority.bodyId("minecraft:stone"));
        String[][] blocks={{"minecraft:chest","","menu"},{"minecraft:barrel","","menu"},{"minecraft:oak_door","DOORS",""},{"minecraft:oak_trapdoor","TRAPDOORS",""},{"minecraft:oak_fence_gate","FENCE_GATES",""},{"minecraft:stone_button","BUTTONS",""},{"minecraft:oak_sign","ALL_SIGNS",""},{"doomsday_decoration:remains_1","",""},{"zombie_remains_loot:skeletal_remains","",""}};
        for(Item item:List.of(new Gun(),new Melee()))for(String[] kind:blocks){
            reset(item);block(kind[0],kind[1].isEmpty()?null:kind[1],kind[2].equals("menu"));mc.player.ads=item instanceof com.tacz.guns.api.item.IGun;
            var event=click(true);check(!event.canceled&&event.swing);check(!mc.player.ads);mc.startUseItem();check(mc.gameMode.uses==1);
        }
        for(Item item:List.of(new Gun(),new Melee()))for(net.minecraft.world.entity.Entity body:List.of(new de.maxhenkel.corpse.entities.CorpseEntity(),new MenuEntity())){
            reset(item);mc.hitResult=new EntityHitResult(body);check(!click(true).canceled);check(mc.crosshairPickEntity==body);mc.startUseItem();check(mc.gameMode.uses==1);
        }
        reset(new Gun());block("minecraft:chest",null,true);check(click(false).canceled); // Left attack behavior remains native.
        reset(new Melee());block("minecraft:chest",null,true);check(click(false).canceled);
        reset(new Gun());check(click(true).canceled);
        reset(new Melee());check(click(true).canceled);
        reset(new Gun());block("minecraft:stone",null,false);check(Priority.target()==null);check(click(true).canceled);
        reset(new Gun());block("minecraft:chest",null,true);mc.player.range=false;check(Priority.target()==null);check(click(true).canceled);
        reset(new Gun());block("minecraft:chest",null,true);mc.level.obstruction=new BlockHitResult(new BlockPos(1,0,0),1);check(Priority.target()==null);check(click(true).canceled);
        reset(new Gun());block("minecraft:chest",null,true);mc.level.obstruction=new BlockHitResult(new BlockPos(3,0,0),3);check(Priority.target()!=null);
        reset(new Gun());block("minecraft:chest",null,true);var protectedEvent=new InputEvent.InteractionKeyMappingTriggered(true);protectedEvent.canceled=true;ClientPreventGunClick.onClickInput(protectedEvent);ClientEventsHandler.onClickInput(protectedEvent);check(protectedEvent.canceled);
        reset(new Gun());mc.screen=new Object();block("minecraft:chest",null,true);check(Priority.target()==null);
        reset(new Gun());mc.player.spectator=true;block("minecraft:chest",null,true);check(Priority.target()==null);
        reset(new Gun());block("minecraft:chest",null,true);mc.player=null;check(Priority.target()==null);
        reset(new Gun());mc.hitResult=new EntityHitResult(new net.minecraft.world.entity.decoration.ItemFrame());check(!click(true).canceled);
        reset(new Gun());block("minecraft:oak_door","DOORS",false);AimKey.AIM_KEY.down=true;mc.player.ads=true;AimKey.onAimPress(new InputEvent.MouseButton.Post(1,1));check(!mc.player.ads);check(Priority.claimed);
        mc.startUseItem();check(mc.gameMode.uses==1);mc.hitResult=new HitResult(HitResult.Type.MISS,new Vec3(3,0,0));for(int i=0;i<3;i++)AimKey.onAimHoldingPreInput(new ClientTickEvent.Post());check(!mc.player.ads);
        AimKey.onAimPress(new InputEvent.MouseButton.Post(1,0));AimKey.AIM_KEY.down=false;check(!Priority.claimed);
        AimKey.onAimPress(new InputEvent.MouseButton.Post(1,1));check(mc.player.ads);
        reset(new Gun());AimKey.AIM_KEY.down=true;AimKey.onAimHoldingPreInput(new ClientTickEvent.Post());check(mc.player.ads);
        reset(new Gun());block("minecraft:chest",null,true);AimKey.AIM_KEY.mouse=2;AimKey.onAimPress(new InputEvent.MouseButton.Post(2,1));check(mc.player.ads);AimKey.AIM_KEY.down=true;check(!Priority.holdAim());
        reset(new Melee());block("minecraft:chest",null,true);AttackKeys.SPECIAL_ATTACK.clicks=2;AttackKeys.onSpAttack(new InputEvent.MouseButton.Post(1,1));check(mc.player.combat.attacks==0);check(AttackKeys.SPECIAL_ATTACK.clicks==0);mc.startUseItem();check(mc.gameMode.uses==1);
        reset(new Melee());AttackKeys.SPECIAL_ATTACK.clicks=1;AttackKeys.onSpAttack(new InputEvent.MouseButton.Post(1,1));check(mc.player.combat.attacks==1);check(mc.player.swings==1);
        reset(new Melee());block("minecraft:chest",null,true);AttackKeys.SPECIAL_ATTACK.mouse=2;AttackKeys.SPECIAL_ATTACK.clicks=1;AttackKeys.onSpAttack(new InputEvent.MouseButton.Post(2,1));check(mc.player.combat.attacks==1);
        reset(new Gun());com.cleannrooster.dungeons_iso.mod.Mod.enabled=true;block("minecraft:chest",null,true);Object cursor=mc.hitResult;dev.zomboid.isotacz.Bridge.selected=cursor;dev.zomboid.isotacz.Bridge.valid=true;mc.hitResult=new HitResult(HitResult.Type.MISS,new Vec3(30,0,0));check(!click(true).canceled);check(mc.hitResult==cursor);
        dev.zomboid.isotacz.Bridge.valid=false;check(Priority.target()==null);dev.zomboid.isotacz.Bridge.valid=true;dev.zomboid.isotacz.NativeActions.mounted=true;check(Priority.target()==null);dev.zomboid.isotacz.NativeActions.mounted=false;dev.zomboid.isotacz.NativeActions.consuming=true;check(Priority.target()==null);
        reset(new Gun());block("minecraft:chest",null,true);Priority.claimed=true;mc.player=new LocalPlayer();check(Priority.target()!=null);check(!Priority.claimed);
        reset(new Melee());block("minecraft:chest",null,true);check(!Priority.meleeEvent(new InputEvent.MouseButton.Post(0,1)));check(!Priority.meleeEvent(new InputEvent.MouseButton.Post(1,0)));
        reset(new Gun());block("minecraft:oak_door","DOORS",false);com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.locked=true;var locked=click(true);check(locked.canceled&&!locked.swing);check(com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.requests==1);check(!mc.player.ads);mc.startUseItem();check(mc.gameMode.uses==0);check(com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.requests==2);
        reset(new Melee());block("minecraft:oak_door","DOORS",false);com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.locked=true;check(click(true).canceled);check(com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.requests==1);
        reset(new Gun());block("minecraft:oak_door","DOORS",false);com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.locked=true;com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.revealed=true;check(click(true).canceled);check(com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.requests==0);
        reset(new Gun());block("minecraft:oak_door","DOORS",false);com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.locked=true;com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.handled=false;check(!click(true).canceled);check(com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.requests==1);
        reset(new Item());block("minecraft:oak_door","DOORS",false);com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.locked=true;check(!click(true).canceled);check(com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat.requests==0);
        System.out.println("Passed "+checks+" input-method and priority checks using exact patched event bytecode and explicit Minecraft/API doubles.");
    }
}
