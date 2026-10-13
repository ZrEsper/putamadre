package dev.zomboid.isotacz;
public class CursorChecks {
    static int count;static void check(boolean b){if(!b)throw new AssertionError("Body cursor "+count);count++;}
    public record Hit(Object entity,String block){public String getType(){return entity==null?"BLOCK":"ENTITY";}public Object getEntity(){return entity;}public String getBlockPos(){return block;}}
    static void fresh(){BodyCursor.lastScan=0;Bridge.MC.hitResult=null;}
    public static void main(String[]args) {
        Hit body=new Hit(null,"doomsday_decoration:remains_3");Bridge.picked=body;fresh();BodyCursor.select();check(Bridge.MC.hitResult==body);check(Bridge.MC.crosshairPickEntity==null);
        int picks=Bridge.picks;for(int i=0;i<10;i++)BodyCursor.select();check(Bridge.picks==picks);
        fresh();Bridge.picked=new Hit(null,"minecraft:stone");BodyCursor.select();check(Bridge.MC.hitResult==null);
        fresh();Object entity=new de.maxhenkel.corpse.entities.CorpseEntity();Hit entityHit=new Hit(entity,null);Bridge.picked=entityHit;BodyCursor.select();check(Bridge.MC.hitResult==entityHit);check(Bridge.MC.crosshairPickEntity==entity);
        fresh();Bridge.reachable=false;BodyCursor.select();check(Bridge.MC.hitResult==null);Bridge.reachable=true;
        fresh();NativeActions.nativeInput=true;BodyCursor.select();check(Bridge.MC.hitResult==null);check(BodyCursor.cached==null);NativeActions.nativeInput=false;
        fresh();Bridge.gun=false;picks=Bridge.picks;BodyCursor.select();check(Bridge.picks==picks);Bridge.gun=true;
        fresh();Bridge.MC.screen=new Object();BodyCursor.select();check(Bridge.MC.hitResult==null);Bridge.MC.screen=null;
        fresh();Bridge.active=false;BodyCursor.select();check(Bridge.MC.hitResult==null);Bridge.active=true;
        fresh();Bridge.MC.player=null;BodyCursor.select();check(Bridge.MC.hitResult==null);Bridge.MC.player=new Object();
        fresh();Bridge.picked=body;BodyCursor.select();picks=Bridge.picks;Bridge.MC.level=new Bridge.Level();BodyCursor.select();check(Bridge.picks==picks+1);
        System.out.println("Real body cursor helper: gun-only targeting, world/menu/mount guards, reach guard and bounded scans: "+count+" checks (camera API doubles)");
    }
}
