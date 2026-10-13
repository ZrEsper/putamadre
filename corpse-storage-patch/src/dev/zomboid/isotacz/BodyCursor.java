package dev.zomboid.isotacz;

/** Makes the visual target agree with the already existing, reach-checked corpse interaction. */
public final class BodyCursor {
    static Object player,level,cached;
    static long lastScan;
    public static void select() {
        try {
            Object mc=Bridge.mc();
            Object currentPlayer=R.get(mc,"player"),currentLevel=R.get(mc,"level");
            if (!Bridge.active() || currentPlayer==null || R.get(mc,"screen")!=null || NativeActions.nativeInput() || !Bridge.gun(currentPlayer)) {player=level=cached=null;lastScan=0;return;}
            long now=System.nanoTime();
            if(player!=currentPlayer || level!=currentLevel || now-lastScan>=50_000_000L || lastScan==0) {
                player=currentPlayer;level=currentLevel;lastScan=now;cached=Bridge.cursorPick(mc);
            }
            Object hit=cached;
            if (hit==null) return;
            String kind=R.call(hit,"getType").toString();
            Object entity=null;
            boolean body;
            if (kind.equals("ENTITY")) { entity=R.call(hit,"getEntity"); body=Bridge.corpse(entity); }
            else if (kind.equals("BLOCK")) body=Bridge.remains(Bridge.blockId(R.call(R.get(mc,"level"),"getBlockState",R.call(hit,"getBlockPos"))));
            else body=false;
            if (body && Bridge.interactable(mc,hit)) { R.set(mc,"hitResult",hit); R.set(mc,"crosshairPickEntity",entity); }
        } catch (Throwable failure) { Bridge.warn("corpse outline target",failure); }
    }
}
