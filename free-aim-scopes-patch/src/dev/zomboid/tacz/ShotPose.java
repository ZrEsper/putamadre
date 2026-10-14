package dev.zomboid.tacz;
/** Restores camera-relative pose without discarding recoil applied during the shot. */
public final class ShotPose {
    public static float[] capture(float[] before) {
        if(before==null)return null;
        Object p=R.get(Bridge.mc(),"player");
        return p==null?null:new float[]{before[0],before[1],(float)R.num(R.call(p,"getXRot")),(float)R.num(R.call(p,"getYRot"))};
    }
    public static float[] restored(float[] saved,float pitch,float yaw) {
        if(saved==null || saved.length<4)return null;
        double delta=yaw-saved[3];delta-=Math.floor((delta+180)/360)*360;
        return new float[]{Math.max(-90,Math.min(90,saved[0]+pitch-saved[2])),saved[1]+(float)delta};
    }
    public static void restore(float[] saved) {
        if(saved==null)return;
        Object p=R.get(Bridge.mc(),"player");if(p==null)return;
        float[] actual=restored(saved,(float)R.num(R.call(p,"getXRot")),(float)R.num(R.call(p,"getYRot")));
        if(actual!=null){R.call(p,"setXRot",actual[0]);R.call(p,"setYRot",actual[1]);}
    }
}
