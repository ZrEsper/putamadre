package dev.zomboid.isotacz;
/** Aim only along the cursor's actual projected ray; recoil never chooses a target. */
public final class FreeAim {
    public static double recoil(double previous,double impulse,double dt) {
        if(!Double.isFinite(previous)) previous=0;
        if(!Double.isFinite(impulse)) impulse=0;
        if(!Double.isFinite(dt)) dt=0;
        return previous*Math.exp(-7*Math.max(0,Math.min(.25,dt)))+impulse;
    }
    public static void aim() {
        if(MountedAim.blocked()) return;
        try {
            Bridge.keepPerspective();
            Object mc=Bridge.mc(),player=R.get(mc,"player");
            if(Bridge.rightAim && (R.get(mc,"screen")!=null || !Bridge.active())) Bridge.setAim(false);
            if(!Bridge.active() || player==null || R.get(mc,"screen")!=null) {Bridge.aimed=false;return;}
            R.set(Bridge.mod(),"targeted",null);R.set(Bridge.mod(),"lockOnTarget",null);
            if(!Bridge.gun(player)) {Bridge.aimed=false;return;}
            Object point=CursorTarget.aimPoint(mc),eye=R.call(player,"getEyePosition");
            double dx=Bridge.v(point,"x")-Bridge.v(eye,"x"),dy=Bridge.v(point,"y")-Bridge.v(eye,"y"),dz=Bridge.v(point,"z")-Bridge.v(eye,"z");
            if(dx*dx+dy*dy+dz*dz<.04) return;
            double[] desired=Rules.angles(dx,dy,dz);
            long now=System.nanoTime();
            if(Bridge.aimed && Bridge.aimPlayer==player) {
                double dt=(now-Bridge.aimFrame)/1e9;
                Bridge.recoilYaw=recoil(Bridge.recoilYaw,Rules.wrap(R.num(R.call(player,"getYRot"))-Bridge.lastAppliedYaw),dt);
                Bridge.recoilPitch=recoil(Bridge.recoilPitch,R.num(R.call(player,"getXRot"))-Bridge.lastAppliedPitch,dt);
            } else {Bridge.recoilYaw=Bridge.recoilPitch=0;}
            double yaw=desired[0]+Bridge.recoilYaw,pitch=Math.max(-89.9,Math.min(89.9,desired[1]+Bridge.recoilPitch));
            if(!Double.isFinite(yaw)||!Double.isFinite(pitch))return;
            R.set(Bridge.mod(),"livingPitch",(float)R.num(R.call(player,"getXRot")));
            R.set(Bridge.mod(),"livingYaw",(float)R.num(R.call(player,"getYRot")));
            R.set(Bridge.mod(),"livingHeadYaw",(float)R.num(R.call(player,"getYHeadRot")));
            R.set(Bridge.mod(),"livingBodyYaw",(float)R.num(R.get(player,"yBodyRot")));
            Bridge.lastDesiredYaw=desired[0];Bridge.lastDesiredPitch=desired[1];
            Bridge.lastAppliedYaw=yaw;Bridge.lastAppliedPitch=pitch;Bridge.aimFrame=now;Bridge.aimed=true;Bridge.aimPlayer=player;
            R.call(player,"setYRot",(float)yaw);R.call(player,"setYHeadRot",(float)yaw);R.set(player,"yBodyRot",(float)yaw);R.call(player,"setXRot",(float)pitch);
        } catch(Throwable e) {Bridge.warn("free cursor aim",e);}
    }
}
